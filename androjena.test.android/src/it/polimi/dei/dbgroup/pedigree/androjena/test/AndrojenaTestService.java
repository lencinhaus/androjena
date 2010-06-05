package it.polimi.dei.dbgroup.pedigree.androjena.test;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class AndrojenaTestService extends Service {
	private static final String TAG = "AndrojenaTestService";
	private final RemoteCallbackList<IAndrojenaTestServiceListener> listeners = new RemoteCallbackList<IAndrojenaTestServiceListener>();
	private boolean testing = false;
	private Thread testRunnerThread = null;

	private static interface ITestFactory {
		public String getName();

		public Test createTest();
	}

	private static final ITestFactory[] TEST_FACTORIES = new ITestFactory[] {
			new ITestFactory() {

				@Override
				public String getName() {
					return "Enhanced";
				}

				@Override
				public Test createTest() {
					// enhanced
					return com.hp.hpl.jena.enhanced.test.TestPackage.suite();
				}
			}, new ITestFactory() {
				@Override
				public String getName() {
					return "Graph";
				}

				@Override
				public Test createTest() {
					// graph
					return com.hp.hpl.jena.graph.test.TestPackage.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Mem";
				}

				@Override
				public Test createTest() {
					// mem
					return com.hp.hpl.jena.mem.test.TestMemPackage.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Model";
				}

				@Override
				public Test createTest() {
					// model
					return com.hp.hpl.jena.rdf.model.test.TestPackage.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "N3";
				}

				@Override
				public Test createTest() {
					// N3
					return com.hp.hpl.jena.n3.N3TestSuite.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Turtle";
				}

				@Override
				public Test createTest() {
					// Turtle
					return com.hp.hpl.jena.n3.turtle.TurtleTestSuite.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "XML Output";
				}

				@Override
				public Test createTest() {
					// xmloutput
					return com.hp.hpl.jena.xmloutput.TestPackage.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Util";
				}

				@Override
				public Test createTest() {
					// util
					return com.hp.hpl.jena.util.TestPackage.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Jena Iterator";
				}

				@Override
				public Test createTest() {
					// Jena iterator
					return com.hp.hpl.jena.util.iterator.test.TestPackage
							.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Mega";
				}

				@Override
				public Test createTest() {
					// Mega
					return com.hp.hpl.jena.regression.MegaTestSuite.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Assembler";
				}

				@Override
				public Test createTest() {
					// Assembler
					return com.hp.hpl.jena.assembler.test.TestAssemblerPackage
							.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "ARP";
				}

				@Override
				public Test createTest() {
					// ARP
					return com.hp.hpl.jena.rdf.arp.TestPackage.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Vocabularies";
				}

				@Override
				public Test createTest() {
					// Vocabularies
					return com.hp.hpl.jena.vocabulary.test.TestVocabularies
							.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Shared";
				}

				@Override
				public Test createTest() {
					// Shared
					return com.hp.hpl.jena.shared.TestSharedPackage.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Reasoners";
				}

				@Override
				public Test createTest() {
					// Reasoners
					return com.hp.hpl.jena.reasoner.test.TestPackage.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Composed Graphs";
				}

				@Override
				public Test createTest() {
					// Composed graphs
					return com.hp.hpl.jena.graph.compose.test.TestPackage
							.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Ontology";
				}

				@Override
				public Test createTest() {
					// Ontology
					return com.hp.hpl.jena.ontology.impl.TestPackage.suite();
				}
			}, new ITestFactory() {

				@Override
				public String getName() {
					return "Cmd-line Utils";
				}

				@Override
				public Test createTest() {
					// cmdline utils
					return jena.test.TestPackage.suite();
				}
			} };

	private final IAndrojenaTestService.Stub binder = new IAndrojenaTestService.Stub() {

		@Override
		public void unregisterListener(IAndrojenaTestServiceListener listener)
				throws RemoteException {
			if (listener != null)
				listeners.unregister(listener);
		}

		@Override
		public boolean stopTests() throws RemoteException {
			synchronized (AndrojenaTestService.this) {
				if (isTesting()) {
					if (result != null) {
						result.stop();
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public boolean startTests() throws RemoteException {
			synchronized (AndrojenaTestService.this) {
				if (!isTesting()) {
					testing = true;
					testRunnerThread = new Thread(testRunner);
					testRunnerThread.start();
					return true;
				}
			}
			return false;
		}

		@Override
		public void registerListener(IAndrojenaTestServiceListener listener)
				throws RemoteException {
			if (listener != null)
				listeners.register(listener);
		}

		@Override
		public boolean isTesting() throws RemoteException {
			return testing;
		}
	};

	private TestResult result = null;

	private final Runnable testRunner = new Runnable() {
		private String suiteName;
		private String testName;
		private int N;
		private boolean suiteStarted = false;
		private boolean testStarted = false;
		private Runtime runtime = Runtime.getRuntime();

		private final TestListener testListener = new TestListener() {

			@Override
			public void startTest(Test test) {
				testName = test.toString();
				String testClass = test.getClass().getName();
				N = listeners.beginBroadcast();
				for (int i = 0; i < N; i++) {
					try {
						listeners.getBroadcastItem(i).testStarted(testName,
								testClass, runtime.freeMemory(),
								runtime.totalMemory());
					} catch (RemoteException ex) {
						log(

								"an error occurred while notifying listeners about test start",
								ex);
					}
				}
				listeners.finishBroadcast();
				testStarted = true;
			}

			@Override
			public void endTest(Test test) {
				N = listeners.beginBroadcast();
				for (int i = 0; i < N; i++) {
					try {
						listeners.getBroadcastItem(i).testEnded(testName,
								runtime.freeMemory(), runtime.totalMemory());
					} catch (RemoteException ex) {
						log(

								"an error occurred while notifying listeners about test end",
								ex);
					}
				}
				listeners.finishBroadcast();
				testStarted = false;
			}

			@Override
			public void addFailure(Test test, AssertionFailedError t) {
				ParcelableException pex = new ParcelableException(t);
				N = listeners.beginBroadcast();
				for (int i = 0; i < N; i++) {
					try {
						listeners.getBroadcastItem(i).testFailed(testName,
								runtime.freeMemory(), runtime.totalMemory(),
								pex);
					} catch (RemoteException ex) {
						log(

								"an error occurred while notifying listeners about test failure",
								ex);
					}
				}
				listeners.finishBroadcast();
			}

			@Override
			public void addError(Test test, Throwable t) {
				ParcelableException pex = new ParcelableException(t);
				N = listeners.beginBroadcast();
				for (int i = 0; i < N; i++) {
					try {
						listeners.getBroadcastItem(i).testError(testName,
								runtime.freeMemory(), runtime.totalMemory(),
								pex);
					} catch (RemoteException ex) {
						log(

								"an error occurred while notifying listeners about test error",
								ex);
					}
				}
				listeners.finishBroadcast();
			}
		};

		@Override
		public void run() {

			try {
				synchronized (AndrojenaTestService.this) {
					result = new LightweightTestResult();
				}
				result.addListener(testListener);
				int suiteCount = TEST_FACTORIES.length;

				N = listeners.beginBroadcast();
				for (int i = 0; i < N; i++) {
					try {
						listeners.getBroadcastItem(i).testsStarted(suiteCount,
								runtime.maxMemory());
					} catch (RemoteException ex) {
						log(

								"an error occurred while notifying listeners about tests start",
								ex);
					}
				}
				listeners.finishBroadcast();

				for (ITestFactory testFactory : TEST_FACTORIES) {
					suiteName = testFactory.getName();
					Test test = testFactory.createTest();
					int testCount = test.countTestCases();

					N = listeners.beginBroadcast();
					for (int i = 0; i < N; i++) {
						try {
							listeners.getBroadcastItem(i).suiteStarted(
									suiteName, testCount);
						} catch (RemoteException ex) {
							log(

									"an error occurred while notifying listeners about suite start",
									ex);
						}
					}
					listeners.finishBroadcast();
					suiteStarted = true;

					test.run(result);

					test = null;

					N = listeners.beginBroadcast();
					for (int i = 0; i < N; i++) {
						try {
							listeners.getBroadcastItem(i).suiteEnded(suiteName);
						} catch (RemoteException ex) {
							log(

									"an error occurred while notifying listeners about suite end",
									ex);
						}
					}
					listeners.finishBroadcast();
					suiteStarted = false;
					if (result.shouldStop())
						break;
				}

				result.removeListener(testListener);
				synchronized (AndrojenaTestService.this) {
					result = null;
				}

				N = listeners.beginBroadcast();
				for (int i = 0; i < N; i++) {
					try {
						listeners.getBroadcastItem(i).testsEnded(true, null);
					} catch (RemoteException ex) {
						log(

								"an error occurred while notifying listeners about tests end",
								ex);
					}
				}
				listeners.finishBroadcast();
			} catch (Throwable t) {
				synchronized (AndrojenaTestService.this) {
					result = null;
				}
				ParcelableException pex = new ParcelableException(t);
				N = listeners.beginBroadcast();
				for (int i = 0; i < N; i++) {
					try {
						IAndrojenaTestServiceListener listener = listeners
								.getBroadcastItem(i);
						if (testStarted)
							listener.testEnded(testName, runtime.freeMemory(),
									runtime.totalMemory());
						if (suiteStarted)
							listener.suiteEnded(suiteName);
						listener.testsEnded(false, pex);
					} catch (RemoteException ex) {
						log(

								"an error occurred while notifying listeners about tests error",
								ex);
					}
				}
				listeners.finishBroadcast();
			} finally {
				synchronized (AndrojenaTestService.this) {
					testing = false;
				}
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		log("service client bound");
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		boolean result = super.onUnbind(intent);
		log("service client unbound");
		return result;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		log("service created");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		log("service destroyed");
	}

	private static void log(String s) {
		Log.i(TAG, s);
	}

	private static void log(String s, Throwable t) {
		Log.e(TAG, s, t);
	}
}
