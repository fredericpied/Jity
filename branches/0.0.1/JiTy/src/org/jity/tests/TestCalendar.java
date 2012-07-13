package org.jity.tests;

import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.jity.agent.Agent;
import org.jity.agent.AgentException;
import org.jity.common.TimeUtil;
import org.jity.referential.PersonnalCalendar;
import org.jity.referential.PersonnalCalendarException;

import junit.framework.TestCase;

public class TestCalendar extends TestCase {
	private static final Logger logger = Logger.getLogger(TestCalendar.class);
		
	public void testGetNumberOfDaysInTheYear() {
		
		PersonnalCalendar cal = new PersonnalCalendar();
		cal.setName("calTest");

		cal.setYear(2016);
		assertEquals(cal.getNumberOfDaysInTheYear(), 366);	

		cal.setYear(2015);
		assertEquals(cal.getNumberOfDaysInTheYear(), 365);	
		
		cal.setYear(2014);
		assertEquals(cal.getNumberOfDaysInTheYear(), 365);	

		cal.setYear(2013);
		assertEquals(cal.getNumberOfDaysInTheYear(), 365);	
		
		cal.setYear(2012);
		assertEquals(cal.getNumberOfDaysInTheYear(), 366);	

		cal.setYear(2011);
		assertEquals(cal.getNumberOfDaysInTheYear(), 365);	

		cal.setYear(2010);
		assertEquals(cal.getNumberOfDaysInTheYear(), 365);

		cal.setYear(2009);
		assertEquals(cal.getNumberOfDaysInTheYear(), 365);	

		cal.setYear(2008);
		assertEquals(cal.getNumberOfDaysInTheYear(), 366);
	}
	
	
	public void testSetAllOpenDays(){
		PersonnalCalendar cal = new PersonnalCalendar();
		cal.setName("calTest");
		
		try {
			cal.setYear(2010);
			cal.initializeWithAllDaysOpen();
			assertEquals(cal.getOpenDays().length(), cal.getNumberOfDaysInTheYear());
		
			cal.setYear(2012);
			cal.initializeWithAllDaysOpen();
			assertEquals(cal.getOpenDays().length(), cal.getNumberOfDaysInTheYear());
		
		} catch (PersonnalCalendarException e) {
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
			
			String openDays1 = cal.getOpenDays();
			cal.setOneDayType(1, "C");

			String openDays2 = cal.getOpenDays();
			
			assertEquals(openDays1.length(), openDays2.length());
			
			cal.setOneDayType(cal.getNumberOfDaysInTheYear(), "C");
			String openDays3 = cal.getOpenDays();
			
			assertEquals(openDays1.length(), openDays3.length());
			
		} catch (PersonnalCalendarException e) {
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
			
			cal.setOneDayType(1, "C");
			cal.setOneDayType(cal.getNumberOfDaysInTheYear(), "C");

			assertEquals(cal.getOneDayType(1), "C");
			
			assertEquals(cal.getOneDayType(2), "O");

			assertEquals(cal.getOneDayType(59), "O");
			
			assertEquals(cal.getOneDayType(cal.getNumberOfDaysInTheYear()), "C");

			
			
		} catch (PersonnalCalendarException e) {
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

			assertEquals(cal.getNumberOfClosedDays(), 10);
			
			//cal.showClosedDays();
			
		} catch (PersonnalCalendarException e) {
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
			
			assertEquals(cal.getNumberOfClosedDays(), 52);

			// Adding Sunday closed
			cal.addClosedDayOfWeek(7);
			
			assertEquals(cal.getNumberOfClosedDays(), 105);
			
		} catch (PersonnalCalendarException e) {
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
			
		} catch (PersonnalCalendarException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	
}
