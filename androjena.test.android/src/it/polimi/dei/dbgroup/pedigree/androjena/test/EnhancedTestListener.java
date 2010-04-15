package it.polimi.dei.dbgroup.pedigree.androjena.test;

import junit.framework.TestListener;
import junit.framework.TestResult;

public interface EnhancedTestListener extends TestListener {
	public void testsStarted(int testCount);
	public void testsFinished(TestResult result);
	public void testsError(Throwable t);
}
