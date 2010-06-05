package it.polimi.dei.dbgroup.pedigree.androjena.test;

import it.polimi.dei.dbgroup.pedigree.androjena.test.ParcelableException;

oneway interface IAndrojenaTestServiceListener
{
	void testsStarted(int suiteCount, long maxMemory);
	void suiteStarted(String suiteName, int testCount);
	void suiteEnded(String suiteName);
	void testStarted(String testName, String testClass, long freeMemory, long totalMemory);
	void testError(String testName, long freeMemory, long totalMemory, in ParcelableException ex);
	void testFailed(String testName, long freeMemory, long totalMemory, in ParcelableException ex);
	void testEnded(String testName, long freeMemory, long totalMemory);
	void testsEnded(boolean success, in ParcelableException ex);
}