package org.jity.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {
	
	static public Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(TestServer.class);
		suite.addTestSuite(TestAgent.class);
		
		suite.addTestSuite(TestWeekCalc.class);
		
		suite.addTestSuite(TestCalendar.class);
		
		
		
		suite.addTestSuite(TestCalendarInstruction.class);
		
		return suite;
	}
	
}
