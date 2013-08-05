package org.jity.tests;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.jity.common.referential.dateConstraint.DateConstraint;
import org.junit.Test;

public class TestDateConstraint {

	@Test
	public void testSetPlanifRule() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetCalendar() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsAValidDate() {
		// On test pour toute les formules possibles.
		
		
		
		
		
	}

	
	
	/**
	 * Generate random planification rules
	 * @return
	 */
	private static String planifRuleGenerator() {
		
		String planifRule;
		
		int operatorInd = getRandomInt(0, DateConstraint.OPERATOR_KEYWORDS.length-1);
		int dayTypeInd = getRandomInt(0, DateConstraint.DAY_TYPE_KEYWORDS.length-1);
		int dayNameInd = getRandomInt(0, DateConstraint.DAY_NAME_KEYWORDS.length-1);
		int periodInd = getRandomInt(0, DateConstraint.PERIOD_KEYWORDS.length-1);

		//int dayNumInd = getRandomInt(0, DateConstraint.DAY_NUM_KEYWORDS.length-1);
		
		int maxDayInPeriod = 0;
		if (DateConstraint.PERIOD_KEYWORDS[periodInd].equals("week")) {
			maxDayInPeriod = 7;
		} else if (DateConstraint.PERIOD_KEYWORDS[periodInd].equals("year")) {
			Calendar cal = new GregorianCalendar();
			
//			if (period.equals("jan")) cal.set(Calendar.MONTH, 0);
//			if (period.equals("feb")) cal.set(Calendar.MONTH, 1);
//			if (period.equals("mar")) cal.set(Calendar.MONTH, 2);
//			if (period.equals("apr")) cal.set(Calendar.MONTH, 3);
//			if (period.equals("mai")) cal.set(Calendar.MONTH, 4);
//			if (period.equals("jun")) cal.set(Calendar.MONTH, 5);
//			if (period.equals("jul")) cal.set(Calendar.MONTH, 6);
//			if (period.equals("aug")) cal.set(Calendar.MONTH, 7);
//			if (period.equals("sep")) cal.set(Calendar.MONTH, 8);
//			if (period.equals("oct")) cal.set(Calendar.MONTH, 9);
//			if (period.equals("nov")) cal.set(Calendar.MONTH, 10);
//			if (period.equals("dec")) cal.set(Calendar.MONTH, 11);
			
			
			maxDayInPeriod = 365;
		} else {
			maxDayInPeriod = 30;
		}
		
		int dayNumInd = getRandomInt(1, maxDayInPeriod);
		
		if (getRandomInt(0, 1) == 1) {
			dayNumInd = dayNumInd * -1;
		}
		
		planifRule = DateConstraint.OPERATOR_KEYWORDS[operatorInd]+"_"+
			dayNumInd+"_"+
			DateConstraint.DAY_TYPE_KEYWORDS[dayTypeInd]+"_"+
			"day"+"_"+
			//DateConstraint.DAY_NAME_KEYWORDS[dayNameInd]+"_"+
			DateConstraint.PERIOD_KEYWORDS[periodInd];
		
		return planifRule;
		
	}
	
	/**
	 * Generate random int
	 * @param min
	 * @param max
	 * @return
	 */
	private  static int getRandomInt(int min, int max) {
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		return rand.nextInt(max - min + 1) + min;
	}
	
	
	
}
