package it.polimi.dei.dbgroup.pedigree.androjena.test;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;

public class LightweightTestResult extends TestResult {
	private List<TestListener> fListeners = new ArrayList<TestListener>();
	private int fErrorCount = 0;
	private int fFailureCount = 0;
	private int fRunCount = 0;

	@Override
	public synchronized void addError(Test test, Throwable t) {
		// just notify the fListeners without saving the error
		fErrorCount++;
		for (TestListener listener : fListeners) {
			listener.addError(test, t);
		}
	}

	@Override
	public synchronized void addFailure(Test test, AssertionFailedError t) {
		// just notify the fListeners without saving the failure
		fFailureCount++;
		for (TestListener listener : fListeners) {
			listener.addFailure(test, t);
		}
	}

	/**
	 * Registers a TestListener
	 */
	public synchronized void addListener(TestListener listener) {
		fListeners.add(listener);
	}

	/**
	 * Unregisters a TestListener
	 */
	public synchronized void removeListener(TestListener listener) {
		fListeners.remove(listener);
	}

	/**
	 * Informs the result that a test was completed.
	 */
	public void endTest(Test test) {
		for (TestListener listener : fListeners) {
			listener.endTest(test);
		}
	}

	/**
	 * Gets the number of detected errors.
	 */
	public synchronized int errorCount() {
		return fErrorCount;
	}

	/**
	 * Returns an Enumeration for the errors
	 */
	public synchronized Enumeration errors() {
		throw new UnsupportedOperationException(
				"method errors is not supported in LightweightTestResult");
	}

	/**
	 * Gets the number of detected failures.
	 */
	public synchronized int failureCount() {
		return fFailureCount;
	}

	/**
	 * Returns an Enumeration for the failures
	 */
	public synchronized Enumeration failures() {
		throw new UnsupportedOperationException(
				"method failures is not supported in LightweightTestResult");
	}

	/**
	 * Gets the number of run tests.
	 */
	public synchronized int runCount() {
		return fRunCount;
	}

	/**
	 * Informs the result that a test will be started.
	 */
	public void startTest(Test test) {
		final int count = test.countTestCases();
		synchronized (this) {
			fRunCount += count;
		}
		for (TestListener listener : fListeners) {
			listener.startTest(test);
		}
	}
}
