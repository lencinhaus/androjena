/*
 * (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package com.hp.hpl.jena.rdf.model.test;

import com.hp.hpl.jena.rdf.model.*;

import com.hp.hpl.jena.shared.Lock;

import junit.framework.*;

/**
 * @author Andy Seaborne
 * @version $Id: TestConcurrency.java,v 1.1 2009/06/29 08:55:33 castagna Exp $
 */
//ANDROID: modified to avoid runTest override in TestCase
public class TestConcurrency extends TestCase {

	/** Creates new RDQLTestSuite */
	static public TestSuite suite() {
		return new TestSuite(TestConcurrency.class, "Model concurrency control");
	}

	// Test suite to exercise the locking
	static long SLEEP = 100;
	static int threadCount = 0;

	// Note : reuse the model across tests.
	final static Model model1 = ModelFactory.createDefaultModel();
	final static Model model2 = ModelFactory.createDefaultModel();

	final static int threadTotal = 10;

	public void testLockNestingSameModel1() {
		Nesting(model1, Lock.READ, Lock.READ, false);
	}

	public void testLockNestingSameModel2() {
		Nesting(model1, Lock.WRITE, Lock.WRITE, false);
	}

	public void testLockNestingSameModel3() {
		Nesting(model1, Lock.READ, Lock.WRITE, true);
	}

	public void testLockNestingSameModel4() {
		Nesting(model1, Lock.WRITE, Lock.READ, false);
	}

	public void testLockNestingDifferentModel1() {
		Nesting(model1, Lock.READ, model2, Lock.READ, false);
	}

	public void testLockNestingDifferentModel2() {
		Nesting(model1, Lock.WRITE, model2, Lock.WRITE, false);
	}

	public void testLockNestingDifferentModel3() {
		Nesting(model1, Lock.READ, model2, Lock.WRITE, false);
	}

	public void testLockNestingDifferentModel4() {
		Nesting(model1, Lock.WRITE, model2, Lock.READ, false);
	}

	private void Nesting(Model outerModel, boolean outerLock,
			boolean innerLock, boolean exceptionExpected) {
		Nesting(outerModel, outerLock, outerModel, innerLock, exceptionExpected);
	}

	private void Nesting(Model outerModel, boolean outerLock, Model innerModel,
			boolean innerLock, boolean exceptionExpected) {
		boolean gotException = false;
		try {
			outerModel.enterCriticalSection(outerLock);

			try {
				try {
					// Should fail if outerLock is READ and innerLock is WRITE
					// and its on the same model, inner and outer.
					innerModel.enterCriticalSection(innerLock);

				} finally {
					innerModel.leaveCriticalSection();
				}
			} catch (Exception ex) {
				gotException = true;
			}

		} finally {
			outerModel.leaveCriticalSection();
		}

		if (exceptionExpected)
			assertTrue("Failed to get expected lock promotion error",
					gotException);
		else
			assertTrue("Got unexpected lock promotion error", !gotException);
	}

	public void testParallel() throws Throwable {
		Model model = ModelFactory.createDefaultModel();
		Thread threads[] = new Thread[threadTotal];

		boolean getReadLock = Lock.READ;
		for (int i = 0; i < threadTotal; i++) {
			String nextId = "T" + Integer.toString(++threadCount);
			threads[i] = new Operation(model, getReadLock);
			threads[i].setName(nextId);
			threads[i].start();

			getReadLock = !getReadLock;
		}

		boolean problems = false;
		for (int i = 0; i < threadTotal; i++) {
			try {
				threads[i].join(200 * SLEEP);
			} catch (InterruptedException intEx) {
			}
		}

		// Try again for any we missed.
		for (int i = 0; i < threadTotal; i++) {
			if (threads[i].isAlive())
				try {
					threads[i].join(200 * SLEEP);
				} catch (InterruptedException intEx) {
				}
			if (threads[i].isAlive()) {
				System.out.println("Thread " + threads[i].getName()
						+ " failed to finish");
				problems = true;
			}
		}

		assertTrue("Some thread failed to finish", !problems);
	}

	class Operation extends Thread {
		Model model;
		boolean readLock;

		Operation(Model m, boolean withReadLock) {
			model = m;
			readLock = withReadLock;
		}

		@Override
		public void run() {
			for (int i = 0; i < 2; i++) {
				try {
					model.enterCriticalSection(readLock);
					if (readLock)
						readOperation(false);
					else
						writeOperation(false);
				} finally {
					model.leaveCriticalSection();
				}
			}
		}
	}

	// Operations ----------------------------------------------

	volatile int writers = 0;

	// The example model operations
	void doStuff(String label, boolean doThrow) {
		String id = Thread.currentThread().getName();
		// Puase a while to cause other threads to (try to) enter the region.
		try {
			Thread.sleep(SLEEP);
		} catch (InterruptedException intEx) {
		}
		if (doThrow)
			throw new RuntimeException(label);
	}

	// Example operations

	public void readOperation(boolean doThrow) {
		if (writers > 0)
			System.err.println("Concurrency error: writers around!");
		doStuff("read operation", false);
		if (writers > 0)
			System.err.println("Concurrency error: writers around!");
	}

	public void writeOperation(boolean doThrow) {
		writers++;
		doStuff("write operation", false);
		writers--;

	}
}

/*
 * (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard
 * Development Company, LP All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
