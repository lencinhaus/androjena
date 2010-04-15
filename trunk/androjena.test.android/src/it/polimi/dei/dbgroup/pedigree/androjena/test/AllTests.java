package it.polimi.dei.dbgroup.pedigree.androjena.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import android.util.Log;

import com.hp.hpl.jena.test.TestPackage;

public class AllTests extends TestSuite {
	
	public static TestSuite suite() throws Exception {
		return new AllTests();
	}

	public AllTests() throws Exception {
		super("aJenaText");
		try {
			 addTest(new TestCase("runTestOverride") {

				@Override
				protected void runTest() throws Throwable {
					Log.i("AllTests", "running overridden runTest method");
				}

			});

			 addTest(TestPackage.suite());
		} catch (Exception ex) {
			Log.e("aJenaTest", "an error occurred while creating tests", ex);
			throw ex;
		}
	}
}
