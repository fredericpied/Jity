package org.jity.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {
	
	static public Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(TestJob.class);
		
		
		
		return suite;
	}
	
}
