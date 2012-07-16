package org.jity.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.jity.agent.Agent;
import org.jity.agent.AgentException;
import org.jity.common.DateUtil;
import org.jity.common.TimeUtil;
import org.jity.referential.PersonnalCalendar;
import org.jity.referential.PersonnalCalendarException;
import org.jity.referential.dateCalc.DateException;
import org.jity.referential.dateCalc.WeekCalc;

import junit.framework.TestCase;

public class TestWeekCalc extends TestCase {
	private static final Logger logger = Logger.getLogger(TestWeekCalc.class);
		
	public void testIsValidDayName(){
		
		assertEquals(WeekCalc.isValidDayName("mon"), true);
		assertEquals(WeekCalc.isValidDayName("tue"), true);
		assertEquals(WeekCalc.isValidDayName("wed"), true);
		assertEquals(WeekCalc.isValidDayName("thu"), true);
		assertEquals(WeekCalc.isValidDayName("fri"), true);
		assertEquals(WeekCalc.isValidDayName("sat"), true);
		assertEquals(WeekCalc.isValidDayName("sun"), true);
		
	}
	
	public void testGetDayName(){

		try {
			assertEquals(WeekCalc.getDayName(1), "mon");
			assertEquals(WeekCalc.getDayName(2), "tue");
			assertEquals(WeekCalc.getDayName(3), "wed");
			assertEquals(WeekCalc.getDayName(4), "thu");
			assertEquals(WeekCalc.getDayName(5), "fri");
			assertEquals(WeekCalc.getDayName(6), "sat");
			assertEquals(WeekCalc.getDayName(7), "sun");
		} catch (DateException e) {
			e.printStackTrace();
			System.exit(1);
		}
				
	}
	
	public void testGetFirstWeekDay(){

			Calendar cal1 = new GregorianCalendar();
			cal1.clear();
			cal1.set(2012, 0, 4); // Jan 3
			
			Calendar cal2 = new GregorianCalendar();
			cal2.clear();
			cal2.set(2012, 0, 2); // Jan 2
			
			Date firstDayOfTheWeek = WeekCalc.getFirstWeekDay(cal1.getTime());
			
			assertEquals(firstDayOfTheWeek.compareTo(cal2.getTime()), 0);
			
			Calendar cal3 = new GregorianCalendar();
			cal3.clear();
			cal3.set(2012, 4, 6); // May 6
			
			Calendar cal4 = new GregorianCalendar();
			cal4.clear();
			cal4.set(2012, 3, 30); // Apr 30
			
			firstDayOfTheWeek = WeekCalc.getFirstWeekDay(cal3.getTime());
			
			assertEquals(firstDayOfTheWeek.compareTo(cal4.getTime()), 0);
	}

	
	public void testGetLastWeekDay() {

		Calendar cal1 = new GregorianCalendar();
		cal1.clear();
		cal1.set(2012, 0, 4); // Jan 3
		logger.info(DateUtil.dateToString(cal1.getTime()));
		
		Calendar cal2 = new GregorianCalendar();
		cal2.clear();
		cal2.set(2012, 0, 8); // Jan 8
		logger.info(DateUtil.dateToString(cal2.getTime()));
		
		Date lastDayOfTheWeek = WeekCalc.getLastWeekDay(cal1.getTime());
		logger.info(DateUtil.dateToString(lastDayOfTheWeek));
		
		assertEquals(lastDayOfTheWeek.compareTo(cal2.getTime()), 0);
		
		Calendar cal3 = new GregorianCalendar();
		cal3.clear();
		cal3.set(2012, 4, 31); // May 6
		logger.info(DateUtil.dateToString(cal3.getTime()));
		
		Calendar cal4 = new GregorianCalendar();
		cal4.clear();
		cal4.set(2012, 5, 3); // June 3
		logger.info(DateUtil.dateToString(cal4.getTime()));
		
		lastDayOfTheWeek = WeekCalc.getLastWeekDay(cal3.getTime());
		logger.info(DateUtil.dateToString(lastDayOfTheWeek));
		assertEquals(lastDayOfTheWeek.compareTo(cal4.getTime()), 0);
	}
	
	
}
