/*
 * Copyright 2010 Lorenzo Carrara
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.polimi.dei.dbgroup.pedigree.androjena.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.zip.GZIPOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AndrojenaTestActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	private enum Status {
		STARTING, RUNNING, FATAL, COMPLETED
	}

	private Button startButton;
	private TextView outputTextView;
	private TextView labelsTextView;
	private CheckBox logCheckbox;
	private CheckBox sendCheckbox;
	private ProgressDialog sendProgress;
	private static final int MESSAGE_UPDATE_OUTPUT = 1;
	private static final int MESSAGE_UPDATE_LABELS = 2;
	private static final int MESSAGE_SENDER_STARTED = 3;
	private static final int MESSAGE_SENDER_PROGRESS = 4;
	private static final int MESSAGE_SENDER_FINISHED = 5;
	private static final int MESSAGE_SENDER_ERROR = 6;
	private static final int MESSAGE_SET_CONTROLS_ENABLED = 7;
	private static final int MESSAGE_SET_CONTROLS_CHECKED = 8;
	private static final int MESSAGE_SET_START_BUTTON_TEXT = 9;
	private static final int DIALOG_SEND_PROGRESS = 1;
	private static final int DIALOG_SEND_RESULT = 2;
	private static final int DIALOG_SEND_DISABLE = 3;
	private static final int DIALOG_INTRO = 4;
	private static final String SERIALIZED_LOG_FILENAME = "androjena-tests-log.xml.gz";
	private static final String RESULT_SEND_URL = "http://www.lencinhaus.com/androjena/send_results.php";
	private Status status = Status.COMPLETED;
	private String runningSuiteName = "";
	private String runningTestName = "";
	private String fatalErrorMessage = "";
	private int suiteCount = 0;
	private int currentSuite = 0;
	private int suiteTests = 0;
	private int currentTest = 0;
	private int runTests = 0;
	private int failedTests = 0;
	private int erroredTests = 0;
	private long maxMemory = 0;
	private long freeMemory = 0;
	private long totalMemory = 0;
	private StringBuilder outputStringBuilder = new StringBuilder();
	private StringBuilder labelsStringBuilder = new StringBuilder();
	private boolean running = false;
	private boolean sendingResults = false;
	private boolean onlyLogErrorsAndFailures = false;
	private boolean sendResults = true;
	private String output = "";
	private String labels = "";
	private boolean senderSuccess = false;
	private String senderResult = null;
	private IAndrojenaTestService service = null;
	private XmlSerializer serializer;
	private OutputStream serializerOS;
	private boolean serializedTestResult = false;
	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int mask;
			switch (msg.what) {
				case MESSAGE_UPDATE_OUTPUT:
					updateOutput();
					break;
				case MESSAGE_UPDATE_LABELS:
					updateLabels();
					break;
				case MESSAGE_SENDER_STARTED:
					senderStarted(msg.arg1);
					break;
				case MESSAGE_SENDER_PROGRESS:
					senderProgress(msg.arg1);
					break;
				case MESSAGE_SENDER_FINISHED:
					senderFinished((String) msg.obj);
					break;
				case MESSAGE_SENDER_ERROR:
					senderError((Throwable) msg.obj);
					break;
				case MESSAGE_SET_CONTROLS_ENABLED:
					mask = msg.arg1;
					if (logCheckbox != null) {
						int logCheckboxEnabled = mask & SET_MASK;
						if (logCheckboxEnabled != SET_NOTHING)
							logCheckbox
									.setEnabled(logCheckboxEnabled == SET_POSITIVE);
					}
					if (sendCheckbox != null) {
						int sendCheckboxEnabled = (mask >> 2) & SET_MASK;
						if (sendCheckboxEnabled != SET_NOTHING)
							sendCheckbox
									.setEnabled(sendCheckboxEnabled == SET_POSITIVE);
					}
					if (startButton != null) {
						int startButtonEnabled = (mask >> 4) & SET_MASK;
						if (startButtonEnabled != SET_NOTHING)
							startButton
									.setEnabled(startButtonEnabled == SET_POSITIVE);
					}
					break;
				case MESSAGE_SET_CONTROLS_CHECKED:
					mask = msg.arg1;
					if (logCheckbox != null) {
						int logCheckboxChecked = mask & SET_MASK;
						if (logCheckboxChecked != SET_NOTHING)
							logCheckbox
									.setChecked(logCheckboxChecked == SET_POSITIVE);
					}
					if (sendCheckbox != null) {
						int sendCheckboxChecked = (mask >> 2) & SET_MASK;
						if (sendCheckboxChecked != SET_NOTHING)
							sendCheckbox
									.setChecked(sendCheckboxChecked == SET_POSITIVE);
					}
					break;
				case MESSAGE_SET_START_BUTTON_TEXT:
					if(startButton != null) {
						startButton.setText((String)msg.obj);
					}
					break;
				default:
					super.handleMessage(msg);
					break;
			}
		}

	};

	private final ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;

			// disable all controls
			sendSetControlsEnabled(SET_NEGATIVE, SET_NEGATIVE, SET_NEGATIVE);
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			service = (IAndrojenaTestService) IAndrojenaTestService.Stub
					.asInterface(binder);

			try {
				// register service listener
				service.registerListener(serviceListener);

				// reenable controls
				sendSetControlsEnabled(SET_POSITIVE, SET_POSITIVE, SET_POSITIVE);
			} catch (RemoteException ex) {
				Log.e(TAG, "Remote exception while accessing service methods",
						ex);
			}
		}
	};

	private final IAndrojenaTestServiceListener serviceListener = new IAndrojenaTestServiceListener.Stub() {

		@Override
		public void testsStarted(int suiteCount, long maxMemory)
				throws RemoteException {
			status = Status.RUNNING;
			AndrojenaTestActivity.this.suiteCount = suiteCount;
			AndrojenaTestActivity.this.maxMemory = maxMemory;
			sendUpdateLabels();
			sendUpdateOutput();
			sendSetControlsEnabled(SET_NOTHING, SET_NOTHING, SET_POSITIVE);

			// log
			if (!onlyLogErrorsAndFailures)
				log("tests started");

			// serializer
			if (sendResults) {
				try {
					serializer.attribute("", "suite-count", Integer
							.toString(suiteCount));
					serializer.attribute("", "max-memory", Long
							.toString(maxMemory));
				} catch (Throwable t) {
					log("serializer error", t);
				}
			}
		}

		@Override
		public void testsEnded(boolean success, ParcelableException ex)
				throws RemoteException {
			if (success) {
				status = Status.COMPLETED;
			} else {
				status = Status.FATAL;
				fatalErrorMessage = ex.toString();
			}
			sendUpdateOutput();

			// log
			if (!onlyLogErrorsAndFailures) {
				if (success)
					log("tests ended");
				else
					log("tests error", ex);
			}

			// serializer
			if (sendResults) {
				try {
					if (!success)
						serializeException(ex);
				} catch (Throwable t) {
					log("serializer error", t);
				}
			}

			if (!sendResults)
				finished();
			else {
				sendingResults = true;
				stopSerializer();
				sendSetControlsEnabled(SET_NEGATIVE, SET_NOTHING, SET_NEGATIVE);
				new Thread(resultSender).start();
			}
		}

		@Override
		public void testStarted(String testName, String testClass,
				long freeMemory, long totalMemory) throws RemoteException {
			currentTest++;
			runningTestName = testName;
			AndrojenaTestActivity.this.freeMemory = freeMemory;
			AndrojenaTestActivity.this.totalMemory = totalMemory;
			sendUpdateOutput();

			// log
			if (!onlyLogErrorsAndFailures)
				log("test started: " + testName);

			// serializer
			if (sendResults) {
				try {
					serializer.startTag("", "test");
					serializer.attribute("", "name", testName);
					serializer.attribute("", "class", testClass);
					serializer.attribute("", "start-free-memory", Long
							.toString(freeMemory));
					serializer.attribute("", "start-total-memory", Long
							.toString(totalMemory));
					serializeCurrTimestamp("start-");
					serializedTestResult = false;
				} catch (Throwable t) {
					log("serializer error", t);
				}
			}
		}

		@Override
		public void testFailed(String testName, long freeMemory,
				long totalMemory, ParcelableException ex)
				throws RemoteException {
			failedTests++;
			sendUpdateOutput();

			// log
			log("test failed: " + testName, ex);

			// serializer
			if (sendResults) {
				try {
					serializeTestEndData("failed", freeMemory, totalMemory);
					serializedTestResult = true;
					serializeException(ex);
				} catch (Throwable t) {
					log("serializer error", t);
				}
			}
		}

		@Override
		public void testError(String testName, long freeMemory,
				long totalMemory, ParcelableException ex)
				throws RemoteException {
			erroredTests++;
			sendUpdateOutput();

			// log
			log("test error: " + testName, ex);

			// serializer
			if (sendResults) {
				try {
					serializeTestEndData("error", freeMemory, totalMemory);
					serializedTestResult = true;
					serializeException(ex);
				} catch (Throwable t) {
					log("serializer error", t);
				}
			}
		}

		@Override
		public void testEnded(String testName, long freeMemory, long totalMemory)
				throws RemoteException {
			runTests++;
			AndrojenaTestActivity.this.freeMemory = freeMemory;
			AndrojenaTestActivity.this.totalMemory = totalMemory;
			sendUpdateOutput();

			// log
			if (!onlyLogErrorsAndFailures)
				log("test ended: " + testName);

			// serializer
			if (sendResults) {
				try {
					if (!serializedTestResult) {
						serializeTestEndData("passed", freeMemory, totalMemory);
						serializedTestResult = true;
					}
					serializer.endTag("", "test");
				} catch (Throwable t) {
					log("serializer error", t);
				}
			}
		}

		@Override
		public void suiteStarted(String suiteName, int testCount)
				throws RemoteException {
			currentSuite++;
			suiteTests = testCount;
			currentTest = 0;
			runningSuiteName = suiteName;
			runningTestName = null;
			sendUpdateOutput();

			// log
			if (!onlyLogErrorsAndFailures)
				log("suite started: " + suiteName);

			// serializer
			if (sendResults) {
				try {
					serializer.startTag("", "suite");
					serializer.attribute("", "name", suiteName);
					serializer.attribute("", "suite-count", Integer
							.toString(testCount));
				} catch (Throwable t) {
					log("serializer error", t);
				}
			}
		}

		@Override
		public void suiteEnded(String suiteName) throws RemoteException {

			// log
			if (!onlyLogErrorsAndFailures)
				log("suite ended: " + suiteName);

			// serializer
			if (sendResults) {
				try {
					serializer.endTag("", "suite");
				} catch (Throwable t) {
					log("serializer error", t);
				}
			}
		}
	};

	private final Runnable resultSender = new Runnable() {

		@Override
		public void run() {
			try {
				// read input file size
				File logFile = getFileStreamPath(SERIALIZED_LOG_FILENAME);
				int fileSize = (int) logFile.length();
				handler.obtainMessage(MESSAGE_SENDER_STARTED, fileSize, 0)
						.sendToTarget();

				// open stream and send file
				URL url = new URL(RESULT_SEND_URL);
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setAllowUserInteraction(false);
				OutputStream os = connection.getOutputStream();
				InputStream is = openFileInput(SERIALIZED_LOG_FILENAME);
				byte[] buffer = new byte[4096];
				int read;
				while ((read = is.read(buffer, 0, buffer.length)) != -1) {
					os.write(buffer, 0, read);
					handler.obtainMessage(MESSAGE_SENDER_PROGRESS, read, 0)
							.sendToTarget();
				}
				os.close();
				is.close();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}
				reader.close();
				handler.obtainMessage(MESSAGE_SENDER_FINISHED, sb.toString())
						.sendToTarget();
			} catch (Throwable t) {
				handler.obtainMessage(MESSAGE_SENDER_ERROR, t).sendToTarget();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		createUI();
		
		showDialog(DIALOG_INTRO);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (service != null) {
			try {
				service.unregisterListener(serviceListener);
			} catch (RemoteException ex) {
				Log.e(TAG,
						"Service error while unregistering service listener",
						ex);
			}
			unbindService(connection);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog d = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(
				AndrojenaTestActivity.this);
		switch (id) {
			case DIALOG_INTRO:
				builder.setTitle(R.string.app_name)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(R.string.dialog_intro_message)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// bind to service
										dialog.cancel();
										if (!bindService(new Intent(AndrojenaTestActivity.this, AndrojenaTestService.class),
												connection, Context.BIND_AUTO_CREATE)) {
											Toast.makeText(AndrojenaTestActivity.this, "cannot bind to test service",
													Toast.LENGTH_LONG).show();
										}
									}
								}).setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										AndrojenaTestActivity.this.finish();
									}
								});
				d = builder.create();
				break;
			case DIALOG_SEND_PROGRESS:
				sendProgress = new ProgressDialog(this);
				sendProgress.setIndeterminate(false);
				// sendProgress.setTitle("Androjena Tests");
				sendProgress.setMessage("Sending test results to server");
				sendProgress.setCancelable(false);
				sendProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				d = sendProgress;
				break;
			case DIALOG_SEND_RESULT:
				builder.setTitle(R.string.app_name)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage("dummy message")
				.setPositiveButton(
						getString(R.string.ok),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								AndrojenaTestActivity.this.sendingResults = false;
								finished();
								dialog.cancel();
							}
						});
				d = builder.create();
				break;
			case DIALOG_SEND_DISABLE:
				builder.setTitle(R.string.app_name).setCancelable(false)
						.setIcon(android.R.drawable.ic_dialog_info).setMessage(
								R.string.dialog_sender_disable_message)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										AndrojenaTestActivity.this.sendResults = false;
										sendSetControlsChecked(SET_NOTHING,
												SET_NEGATIVE);
									}
								}).setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								});
				d = builder.create();
				break;
			default:
				d = super.onCreateDialog(id);
				break;
		}
		return d;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
			case DIALOG_SEND_RESULT:
				AlertDialog senderDialog = (AlertDialog) dialog;
				int formatId = senderSuccess ? R.string.dialog_sender_result_success_format
						: R.string.dialog_sender_result_error_format;
				int iconId = senderSuccess ? android.R.drawable.ic_dialog_info
						: android.R.drawable.ic_dialog_alert;
				senderDialog.setMessage(getString(formatId, senderResult));
				senderDialog.setIcon(iconId);
				break;
		}
	}

	private void createUI() {
		setContentView(R.layout.jena_test_activity);
		startButton = (Button) findViewById(R.id.start_button);
		startButton.setOnClickListener(this);
		if (running)
			startButton.setText(R.string.stop_tests);
		else
			startButton.setText(R.string.start_tests);
		startButton.setEnabled(service != null && status != Status.STARTING
				&& !sendingResults);

		outputTextView = (TextView) findViewById(R.id.output_textview);
		labelsTextView = (TextView) findViewById(R.id.labels_textview);
		updateOutputTextView();
		updateLabelsTextView();

		logCheckbox = (CheckBox) findViewById(R.id.log_checkbox);
		logCheckbox.setOnCheckedChangeListener(this);
		logCheckbox.setChecked(onlyLogErrorsAndFailures);
		logCheckbox.setEnabled(service != null
				&& !sendingResults);

		sendCheckbox = (CheckBox) findViewById(R.id.send_checkbox);
		sendCheckbox.setOnCheckedChangeListener(this);
		sendCheckbox.setChecked(sendResults);
		sendCheckbox.setEnabled(service != null
				&& (status == Status.COMPLETED || status == Status.FATAL) && !sendingResults);
	}

	@Override
	public void onClick(View v) {
		if (!running) {
			startTests();
		} else {
			stopTests();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == logCheckbox) {
			onlyLogErrorsAndFailures = isChecked;
		} else if (buttonView == sendCheckbox) {
			if (sendResults) {
				sendCheckbox.setChecked(true);
				showDialog(DIALOG_SEND_DISABLE);
			} else
				sendResults = isChecked;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		createUI();
	}

	private void startTests() {
		running = true;
		startButton.setEnabled(false);
		startButton.setText(getString(R.string.stop_tests));
		sendCheckbox.setEnabled(false);
		suiteCount = 0;
		currentSuite = 0;
		suiteTests = 0;
		currentTest = 0;
		runTests = 0;
		erroredTests = 0;
		failedTests = 0;
		maxMemory = 0;
		freeMemory = 0;
		totalMemory = 0;
		status = Status.STARTING;
		runningSuiteName = null;
		runningTestName = null;
		fatalErrorMessage = null;
		updateLabels();
		updateOutput();
		if (sendResults)
			startSerializer();
		try {
			if (!service.startTests()) {
				startButton.setEnabled(true);
				startButton.setText(getString(R.string.start_tests));
				sendCheckbox.setEnabled(true);
				Toast.makeText(this, "service cannot start tests",
						Toast.LENGTH_LONG).show();
			}
		} catch (RemoteException ex) {
			Log.e(TAG, "Service exception while starting tests", ex);
		}
	}

	private void startSerializer() {
		try {
			serializerOS = new GZIPOutputStream(openFileOutput(
					SERIALIZED_LOG_FILENAME, Context.MODE_PRIVATE));
			serializer = Xml.newSerializer();
			serializer.setOutput(serializerOS, "UTF-8");
			try {
				serializer
						.setProperty(
								"http://xmlpull.org/v1/doc/properties.html#serializer-indentation",
								"\t");
			} catch (Throwable serT) {
				try {
					serializer
							.setFeature(
									"http://xmlpull.org/v1/doc/features.html#indent-output",
									true);
				} catch (Throwable serT2) {
					Log.i(TAG, "XmlSerializer "
							+ serializer.getClass().getName()
							+ " does not support indentation", serT2);
				}
			}
			serializer.startDocument("UTF-8", null);
			serializer.startTag("", "test-run");
			serializeCurrTimestamp("start-");
			serializeBuild();
		} catch (Throwable t) {
			log("serializer error", t);
		}
	}

	private void stopSerializer() {
		try {
			serializer.endTag("", "test-run");
			serializer.endDocument();
			serializer.flush();
			serializerOS.close();
			serializer = null;
			serializerOS = null;
		} catch (Throwable t) {
			log("serializer error", t);
		}
	}

	private void serializeBuild() {
		try {
			// serializer.startTag("", "build");
			serializer.attribute("", "board", Build.BOARD);
			serializer.attribute("", "brand", Build.BRAND);
			serializer.attribute("", "device", Build.DEVICE);
			serializer.attribute("", "display", Build.DISPLAY);
			serializer.attribute("", "fingerprint", Build.FINGERPRINT);
			serializer.attribute("", "host", Build.HOST);
			serializer.attribute("", "id", Build.ID);
			serializer.attribute("", "model", Build.MODEL);
			serializer.attribute("", "product", Build.PRODUCT);
			serializer.attribute("", "tags", Build.TAGS);
			serializer.attribute("", "type", Build.TYPE);
			serializer.attribute("", "user", Build.USER);
			serializer.attribute("", "time", Long.toString(Build.TIME));
			serializer.attribute("", "version-incremental",
					Build.VERSION.INCREMENTAL);
			serializer.attribute("", "version-release", Build.VERSION.RELEASE);
			serializer.attribute("", "version-sdk", Build.VERSION.SDK);
			// serializer.endTag("", "build");
		} catch (Throwable myT) {
			log("serializer error", myT);
		}
	}

	private void serializeCurrTimestamp(String prefix) {
		try {
			serializer.attribute("", prefix + "timestamp", Long.toString(System
					.currentTimeMillis()));
		} catch (Throwable myT) {
			log("serializer error", myT);
		}
	}

	private void serializeTestEndData(String result, long freeMemory,
			long totalMemory) {
		try {

			serializer.attribute("", "result", result);
			serializer.attribute("", "end-free-memory", Long
					.toString(freeMemory));
			serializer.attribute("", "end-total-memory", Long
					.toString(totalMemory));
		} catch (Throwable myT) {
			log("serializer error", myT);
		}
		serializeCurrTimestamp("end-");
	}

	private void serializeException(ParcelableException t) {
		try {
			serializer.startTag("", "exception");
			serializer.attribute("", "class", t.getClassName());
			String message = t.getMessage();
			if (!TextUtils.isEmpty(message)) {
				serializer.startTag("", "message");
				serializer.text(message);
				serializer.endTag("", "message");
			}
			serializer.startTag("", "stack-trace");
			for (StackTraceElement stackFrame : t.getStackTrace()) {
				serializer.startTag("", "frame");
				serializer.attribute("", "class", stackFrame.getClassName());
				serializer.attribute("", "method", stackFrame.getMethodName());
				serializer.attribute("", "native", Boolean.toString(stackFrame
						.isNativeMethod()));
				String fname = stackFrame.getFileName();
				if (fname != null) {
					serializer.attribute("", "file", fname);
					serializer.attribute("", "line", Integer
							.toString(stackFrame.getLineNumber()));
				}
				serializer.endTag("", "frame");
			}
			serializer.endTag("", "stack-trace");
			if (t.getCause() != null)
				serializeException(t.getCause());
			serializer.endTag("", "exception");
		} catch (Throwable myT) {
			log("serializer error", myT);
		}
	}

	private void stopTests() {
		try {
			if (service.stopTests()) {
				startButton.setEnabled(false);
			}
		} catch (RemoteException ex) {
			Log.e(TAG, "Service exception while stopping test execution", ex);
		}
	}

	private static final double MB_DIV = (double) (1024 * 1024);
	private static final NumberFormat nf = new DecimalFormat("0.00");

	private void updateOutput() {
		synchronized (this) {
			outputStringBuilder.setLength(0);
			if (status != Status.STARTING) {
				outputStringBuilder.append(currentSuite);
				outputStringBuilder.append("/");
				outputStringBuilder.append(suiteCount);
				if (!TextUtils.isEmpty(runningSuiteName)) {
					outputStringBuilder.append(" (");
					outputStringBuilder.append(runningSuiteName);
					outputStringBuilder.append(")");
				}
				outputStringBuilder.append("\n");
				outputStringBuilder.append(currentTest);
				outputStringBuilder.append("/");
				outputStringBuilder.append(suiteTests);
				outputStringBuilder.append("\n");
				outputStringBuilder.append(runTests);
				outputStringBuilder.append("\n");
				outputStringBuilder.append(failedTests);
				outputStringBuilder.append("\n");
				outputStringBuilder.append(erroredTests);
				outputStringBuilder.append("\n");
				double maxMemoryMb = ((double) maxMemory) / MB_DIV;
				double usedMemoryMb = ((double) (totalMemory - freeMemory))
						/ MB_DIV;
				outputStringBuilder.append(nf.format(usedMemoryMb));
				outputStringBuilder.append("/");
				outputStringBuilder.append(nf.format(maxMemoryMb));
				outputStringBuilder.append(" MB\n");
			}
			switch (status) {
				case STARTING:
					outputStringBuilder.append("starting");
					break;
				case COMPLETED:
					outputStringBuilder.append("completed");
					break;
				case FATAL:
					outputStringBuilder.append("fatal error");
					if (!TextUtils.isEmpty(fatalErrorMessage)) {
						outputStringBuilder.append(" (");
						outputStringBuilder.append(fatalErrorMessage);
						outputStringBuilder.append(")");
					}
					break;
				case RUNNING:
					outputStringBuilder.append("running");
					if (!TextUtils.isEmpty(runningTestName)) {
						outputStringBuilder.append(" ");
						outputStringBuilder.append(runningTestName);
					}
					break;
				default:
					outputStringBuilder.append("unknown");
					break;
			}
			output = outputStringBuilder.toString();
		}
		updateOutputTextView();
	}

	private void updateLabels() {
		synchronized (this) {
			labelsStringBuilder.setLength(0);
			if (status != Status.STARTING) {
				labelsStringBuilder.append("suite : \n");
				labelsStringBuilder.append("tests : \n");
				labelsStringBuilder.append("runs : \n");
				labelsStringBuilder.append("failures : \n");
				labelsStringBuilder.append("errors : \n");
				labelsStringBuilder.append("memory : \n");
			}
			labelsStringBuilder.append("status : ");
			labels = labelsStringBuilder.toString();
		}
		updateLabelsTextView();
	}

	private void updateLabelsTextView() {
		if (labelsTextView != null)
			labelsTextView.setText(labels);
	}

	private void updateOutputTextView() {
		if (outputTextView != null)
			outputTextView.setText(output);
	}

	private void finished() {
		running = false;
		sendSetControlsEnabled(SET_POSITIVE, SET_POSITIVE, SET_POSITIVE);
		sendSetStartButtonText(getString(R.string.start_tests));
	}

	private void senderStarted(int numBytes) {
		showDialog(DIALOG_SEND_PROGRESS);
		sendProgress.setMax(numBytes);
		sendProgress.setProgress(0);
	}

	private void senderProgress(int sentBytes) {
		sendProgress.incrementProgressBy(sentBytes);
	}

	private void senderFinished(String result) {
		dismissDialog(DIALOG_SEND_PROGRESS);
		senderSuccess = true;
		senderResult = result;
		showDialog(DIALOG_SEND_RESULT);
	}

	private void senderError(Throwable t) {
		dismissDialog(DIALOG_SEND_PROGRESS);
		senderSuccess = false;
		senderResult = t.toString();
		showDialog(DIALOG_SEND_RESULT);
	}

	private static final String TAG = "AndrojenaTests";

	private void sendUpdateOutput() {
		handler.sendEmptyMessage(MESSAGE_UPDATE_OUTPUT);
	}

	private void sendUpdateLabels() {
		handler.sendEmptyMessage(MESSAGE_UPDATE_LABELS);
	}

	private static final int SET_NOTHING = 0;
	private static final int SET_POSITIVE = 1;
	private static final int SET_NEGATIVE = 2;
	private static final int SET_MASK = SET_POSITIVE | SET_NEGATIVE;

	private void sendSetControlsEnabled(int logCheckboxEnabled,
			int sendCheckboxEnabled, int startButtonEnabled) {
		int mask = 0;
		mask |= logCheckboxEnabled;
		mask |= (sendCheckboxEnabled << 2);
		mask |= (startButtonEnabled << 4);
		handler.obtainMessage(MESSAGE_SET_CONTROLS_ENABLED, mask, 0)
				.sendToTarget();
	}

	private void sendSetControlsChecked(int logCheckboxChecked,
			int sendCheckboxChecked) {
		int mask = 0;
		mask |= logCheckboxChecked;
		mask |= (sendCheckboxChecked << 2);
		handler.obtainMessage(MESSAGE_SET_CONTROLS_CHECKED, mask, 0)
				.sendToTarget();
	}
	
	private void sendSetStartButtonText(String text) {
		handler.obtainMessage(MESSAGE_SET_START_BUTTON_TEXT, text).sendToTarget();
	}

	private static void log(String s) {
		Log.i(TAG, s);
	}

	private static void log(String s, ParcelableException pex) {
		Log.e(TAG, s + "\n" + pex.getStackTraceString());
	}

	private static void log(String s, Throwable t) {
		Log.e(TAG, s, t);
	}
}
