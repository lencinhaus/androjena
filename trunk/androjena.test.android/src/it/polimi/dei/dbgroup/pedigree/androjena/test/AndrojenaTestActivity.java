package it.polimi.dei.dbgroup.pedigree.androjena.test;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;
import android.app.Activity;
import android.content.res.Configuration;
import it.polimi.dei.dbgroup.pedigree.androjena.test.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AndrojenaTestActivity extends Activity implements OnClickListener, OnCheckedChangeListener,
		EnhancedTestListener {

	private Button startButton;
	private TextView outputTextView;
	private CheckBox logCheckbox;
	private static final int MESSAGE_UPDATE = 1;
	private static final int MESSAGE_FINISHED = 2;
	private String runningTestName = "";
	private int totalTests = 0;
	private int runTests = 0;
	private int failedTests = 0;
	private int erroredTests = 0;
	private boolean running = false;
	private boolean onlyLogErrorsAndFailures = false;
	private String output = "";
	private TestRunnerThread testRunnerThread = null;
	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MESSAGE_UPDATE) {
				updateOutput();
			} else if (msg.what == MESSAGE_FINISHED) {
				finished();
			} else
				super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		createUI();
	}
	
	private void createUI() {
		setContentView(R.layout.jena_test_activity);
		startButton = (Button) findViewById(R.id.start_button);
		startButton.setOnClickListener(this);
		if(running) startButton.setText(R.string.stop_tests);
		else startButton.setText(R.string.start_tests);

		outputTextView = (TextView) findViewById(R.id.output_textview);
		updateOutputTextView();
		
		logCheckbox = (CheckBox) findViewById(R.id.log_checkbox);
		logCheckbox.setOnCheckedChangeListener(this);
		logCheckbox.setChecked(onlyLogErrorsAndFailures);
	}

	private boolean shouldLogNonError() {
		return !onlyLogErrorsAndFailures;
	}

	@Override
	public void onClick(View v) {
		if (!running) {
			running = true;
			startButton.setText(getString(R.string.stop_tests));
			output = "";
			updateOutputTextView();
			totalTests = 0;
			runTests = 0;
			runningTestName = "";
			erroredTests = 0;
			failedTests = 0;
			testRunnerThread = new TestRunnerThread(this);
			new Thread(testRunnerThread).start();
		} else {
			testRunnerThread.stop();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		onlyLogErrorsAndFailures = isChecked;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		createUI();
	}



	private static class TestRunnerThread implements Runnable {
		private EnhancedTestListener listener;
		private TestResult result = null;

		public TestRunnerThread(EnhancedTestListener listener) {
			this.listener = listener;
		}

		@Override
		public void run() {
			try {
				Test test = AllTests.suite();
				listener.testsStarted(test.countTestCases());
				result = new TestResult();
				result.addListener(listener);
				test.run(result);
				listener.testsFinished(result);
				result = null;
			}catch (Exception ex) {
				listener.testsError(ex);
			}
		}

		public void stop() {
			if(result != null) {
				result.stop();
			}
		}
	}

	private void updateOutput() {
		output = "total tests : " + totalTests + "\n" + "runs        : "
				+ runTests + "\n" + "failures    : " + failedTests + "\n"
				+ "errors      : " + erroredTests + "\n" + "remaining   : "
				+ (totalTests - runTests) + "\n" + "running     : "
				+ runningTestName;
		updateOutputTextView();
	}
	
	private void updateOutputTextView() {
		if(outputTextView != null) outputTextView.setText(output);
	}

	private void finished() {
		testRunnerThread = null;
		running = false;
		startButton.setText(R.string.start_tests);
	}

	@Override
	public void addError(Test test, Throwable t) {
		log("error: " + runningTestName, t);
		erroredTests++;
		sendUpdate();
	}

	@Override
	public void addFailure(Test test, AssertionFailedError t) {
		log("failure: " + runningTestName, t);
		failedTests++;
		sendUpdate();
	}

	@Override
	public void endTest(Test test) {
		if(shouldLogNonError()) log("finished: " + runningTestName);
		runTests++;
		sendUpdate();
	}

	@Override
	public void startTest(Test test) {
		runningTestName = test.toString();
		if(shouldLogNonError()) log("started: " + runningTestName);
		sendUpdate();
	}

	@Override
	public void testsFinished(TestResult result) {
		sendFinished();
	}

	@Override
	public void testsStarted(int testCount) {
		totalTests = testCount;
		sendUpdate();
	}

	@Override
	public void testsError(Throwable t) {
		log("An error occurred while running tests", t);
		sendFinished();
	}

	private static final String TAG = "JenaTests";

	private void log(String s) {
		Log.i(TAG, s);
	}

	private void log(String s, Throwable t) {
		Log.e(TAG, s, t);
	}

	private void sendUpdate() {
		handler.sendEmptyMessage(MESSAGE_UPDATE);
	}

	private void sendFinished() {
		handler.sendEmptyMessage(MESSAGE_FINISHED);
	}
}
