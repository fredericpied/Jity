package org.jity.tests;

import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.jity.agent.Agent;
import org.jity.agent.AgentException;
import org.jity.common.referential.dateConstraint.PersonnalCalendar;
import org.jity.common.referential.dateConstraint.DateConstraintException;
import org.jity.common.referential.dateConstraint.YearCalc;
import org.jity.common.util.TimeUtil;

import junit.framework.TestCase;

public class TestCalendar extends TestCase {
	private static final Logger logger = Logger.getLogger(TestCalendar.class);
	
	public void testSetAllOpenDays(){
		PersonnalCalendar cal = new PersonnalCalendar();
		cal.setName("calTest");
		
		try {
			cal.setYear(2010);
			cal.initializeWithAllDaysOpen();
			assertEquals(cal.getDays().length(), YearCalc.getMaxNumberofDayInYear(2010)+14);
		
			cal.setYear(2012);
			cal.initializeWithAllDaysOpen();
			assertEquals(cal.getDays().length(), YearCalc.getMaxNumberofDayInYear(2012)+14);
		
		} catch (DateConstraintException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testSetOneDayType() {
		try {

			PersonnalCalendar cal = new PersonnalCalendar();
			cal.setName("calTest");
			cal.setYear(2012);
			cal.initializeWithAllDaysOpen();
			
			String openDays1 = cal.getDays();
			cal.setOneDayType(8, "C");

			String openDays2 = cal.getDays();
			
			assertEquals(openDays1.length(), openDays2.length());
			
			cal.setOneDayType(365, "C");
			String openDays3 = cal.getDays();
			
			assertEquals(openDays1.length(), openDays3.length());
			
		} catch (DateConstraintException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public void testGetOneDayType() {
		try {

			PersonnalCalendar cal = new PersonnalCalendar();
			cal.setName("calTest");
			cal.setYear(2012);
			cal.initializeWithAllDaysOpen();
			
			cal.setOneDayType(8, "C");
			cal.setOneDayType(365, "C");

			assertEquals(cal.getOneDayType(8), "C");
			
			assertEquals(cal.getOneDayType(2), "O");

			assertEquals(cal.getOneDayType(59), "O");
			
			assertEquals(cal.getOneDayType(365), "C");

			
			
		} catch (DateConstraintException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public void testAddFrenchHolydays() {
		try {

			PersonnalCalendar cal = new PersonnalCalendar();
			cal.setName("calTest");
			cal.setYear(2012);
			cal.initializeWithAllDaysOpen();

			//cal.showClosedDays();
			
			assertEquals(cal.getNumberOfClosedDays(), 0);

			cal.addFrenchHolydays();

			assertEquals(cal.getNumberOfClosedDays(), 12);
			
			cal.showClosedDays();
			
		} catch (DateConstraintException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public void testAddClosedDayOfWeek() {
		try {

			PersonnalCalendar cal = new PersonnalCalendar();
			cal.setName("calTest");
			cal.setYear(2012);
			cal.initializeWithAllDaysOpen();
			
			assertEquals(cal.getNumberOfClosedDays(), 0);

			// Adding Saturday closed
			cal.addClosedDayOfWeek(6);
			
			assertEquals(cal.getNumberOfClosedDays(), 53);

			// Adding Sunday closed
			cal.addClosedDayOfWeek(7);
			
			assertEquals(cal.getNumberOfClosedDays(), 107);
			
		} catch (DateConstraintException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testIsAnOpenDay() {
		try {

			PersonnalCalendar cal1 = new PersonnalCalendar();
			cal1.setName("cal1_5OpenDaysPerWeeks");
			cal1.setYear(2012);
			cal1.initializeWithAllDaysOpen();
			cal1.addClosedDayOfWeek(6);
			cal1.addClosedDayOfWeek(7);
			
			PersonnalCalendar cal2 = new PersonnalCalendar();
			cal2.setName("cal1_6OpenDaysPerWeeks");
			cal2.setYear(2012);
			cal2.initializeWithAllDaysOpen();
			cal2.addClosedDayOfWeek(7);
			
			PersonnalCalendar cal3 = new PersonnalCalendar();
			cal3.setName("cal1_6OpenDaysPerWeeksPlusFrenchHolidays");
			cal3.setYear(2012);
			cal3.initializeWithAllDaysOpen();
			cal3.addClosedDayOfWeek(7);
			cal3.addFrenchHolydays();
			
			java.util.Calendar calToTest = new GregorianCalendar();
			calToTest.clear();
			calToTest.set(2012,0,1); // dim, Jan 1 2012
			
			assertEquals(cal1.isAnOpenDay(calToTest.getTime()), false);
			assertEquals(cal2.isAnOpenDay(calToTest.getTime()), false);
			assertEquals(cal3.isAnOpenDay(calToTest.getTime()), false);
			
			calToTest.set(2012,1,1); // wed, Feb 1 2012
			
			assertEquals(cal1.isAnOpenDay(calToTest.getTime()), true);
			assertEquals(cal2.isAnOpenDay(calToTest.getTime()), true);
			assertEquals(cal3.isAnOpenDay(calToTest.getTime()), true);
			
			calToTest.set(2012,1,4); // sat, Feb 4 2012
			
			assertEquals(cal1.isAnOpenDay(calToTest.getTime()), false);
			assertEquals(cal2.isAnOpenDay(calToTest.getTime()), true);
			assertEquals(cal3.isAnOpenDay(calToTest.getTime()), true);
			
			calToTest.set(2012,4,8); // tur, May 8 2012
			
			assertEquals(cal1.isAnOpenDay(calToTest.getTime()), true);
			assertEquals(cal2.isAnOpenDay(calToTest.getTime()), true);
			assertEquals(cal3.isAnOpenDay(calToTest.getTime()), false);
			
			calToTest.set(2012,6,14); // sat, Jul 14 2012
			
			assertEquals(cal1.isAnOpenDay(calToTest.getTime()), false);
			assertEquals(cal2.isAnOpenDay(calToTest.getTime()), true);
			assertEquals(cal3.isAnOpenDay(calToTest.getTime()), false);
			
		} catch (DateConstraintException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	
}
