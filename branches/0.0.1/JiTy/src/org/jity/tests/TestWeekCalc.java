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

	public void testIsValidDayName() {
		assertEquals(WeekCalc.isValidDayName("mon"), true);
		assertEquals(WeekCalc.isValidDayName("tue"), true);
		assertEquals(WeekCalc.isValidDayName("wed"), true);
		assertEquals(WeekCalc.isValidDayName("thu"), true);
		assertEquals(WeekCalc.isValidDayName("fri"), true);
		assertEquals(WeekCalc.isValidDayName("sat"), true);
		assertEquals(WeekCalc.isValidDayName("sun"), true);
	}

	public void testGetDayName() {

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

	public void testGetFirstWeekDay() {
		try {

			PersonnalCalendar persCal = new PersonnalCalendar();
			persCal.setName("5OpenDaysPerWeek");
			persCal.setYear(2012);
			persCal.initializeWithAllDaysOpen();
			persCal.addFrenchHolydays();
			persCal.addClosedDayOfWeek(6);
			persCal.addClosedDayOfWeek(7);

			Date dateToTest1 = DateUtil.stringToDate("04/01/2012");
			logger.info("Date to test: " + DateUtil.dateToString(dateToTest1));
			Date goodDate1 = DateUtil.stringToDate("02/01/2012");
			logger.info("Good result: " + DateUtil.dateToString(goodDate1));
			Date calculateDate1 = WeekCalc.getFirstWeekDay(dateToTest1);
			logger.info("Calculate result: " + DateUtil.dateToString(calculateDate1));
			assertEquals(calculateDate1.compareTo(goodDate1), 0);

			dateToTest1 = DateUtil.stringToDate("06/05/2012");
			logger.info("Date to test: " + DateUtil.dateToString(dateToTest1));
			goodDate1 = DateUtil.stringToDate("30/04/2012");
			logger.info("Good result: " + DateUtil.dateToString(goodDate1));
			calculateDate1 = WeekCalc.getFirstWeekDay(dateToTest1);
			logger.info("Calculate result: " + DateUtil.dateToString(calculateDate1));
			assertEquals(calculateDate1.compareTo(goodDate1), 0);

		} catch (PersonnalCalendarException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (DateException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	public void testGetLastWeekDay() {

		try {

			PersonnalCalendar persCal = new PersonnalCalendar();
			persCal.setName("5OpenDaysPerWeek");
			persCal.setYear(2012);
			persCal.initializeWithAllDaysOpen();
			persCal.addFrenchHolydays();
			persCal.addClosedDayOfWeek(6);
			persCal.addClosedDayOfWeek(7);

			Date dateToTest1 = DateUtil.stringToDate("04/01/2012");
			logger.info("Date to test: " + DateUtil.dateToString(dateToTest1));
			Date goodDate1 = DateUtil.stringToDate("08/01/2012");
			logger.info("Good result: " + DateUtil.dateToString(goodDate1));
			Date calculateDate1 = WeekCalc.getLastWeekDay(dateToTest1);
			logger.info("Calculate result: " + DateUtil.dateToString(calculateDate1));
			assertEquals(calculateDate1.compareTo(goodDate1), 0);

			dateToTest1 = DateUtil.stringToDate("31/05/2012");
			logger.info("Date to test: " + DateUtil.dateToString(dateToTest1));
			goodDate1 = DateUtil.stringToDate("03/06/2012");
			logger.info("Good result: " + DateUtil.dateToString(goodDate1));
			calculateDate1 = WeekCalc.getLastWeekDay(dateToTest1);
			logger.info("Calculate result: " + DateUtil.dateToString(calculateDate1));
			assertEquals(calculateDate1.compareTo(goodDate1), 0);

		} catch (PersonnalCalendarException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (DateException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void testGetFirstOpenWeekDay() {

		try {
			PersonnalCalendar persCal = new PersonnalCalendar();
			persCal.setName("5OpenDaysPerWeek");
			persCal.setYear(2012);
			persCal.initializeWithAllDaysOpen();
			persCal.addFrenchHolydays();
			persCal.addClosedDayOfWeek(6);
			persCal.addClosedDayOfWeek(7);

			Date dateToTest1 = DateUtil.stringToDate("13/04/2012");
			logger.info("Date to test: " + DateUtil.dateToString(dateToTest1));
			Date goodDate1 = DateUtil.stringToDate("10/04/2012");
			logger.info("Good result: " + DateUtil.dateToString(goodDate1));
			Date calculateDate1 = WeekCalc.getFirstOpenWeekDay(dateToTest1, persCal);
			logger.info("Calculate result: " + DateUtil.dateToString(calculateDate1));
			assertEquals(calculateDate1.compareTo(goodDate1), 0);
			
			dateToTest1 = DateUtil.stringToDate("15/08/2012");
			logger.info("Date to test: " + DateUtil.dateToString(dateToTest1));
			goodDate1 = DateUtil.stringToDate("13/08/2012");
			logger.info("Good result: " + DateUtil.dateToString(goodDate1));
			calculateDate1 = WeekCalc.getFirstOpenWeekDay(dateToTest1, persCal);
			logger.info("Calculate result: " + DateUtil.dateToString(calculateDate1));
			assertEquals(calculateDate1.compareTo(goodDate1), 0);
			
			persCal.addClosedDayOfWeek(1);
			
			dateToTest1 = DateUtil.stringToDate("06/06/2012");
			logger.info("Date to test: " + DateUtil.dateToString(dateToTest1));
			goodDate1 = DateUtil.stringToDate("05/06/2012");
			logger.info("Good result: " + DateUtil.dateToString(goodDate1));
			calculateDate1 = WeekCalc.getFirstOpenWeekDay(dateToTest1, persCal);
			logger.info("Calculate result: " + DateUtil.dateToString(calculateDate1));
			assertEquals(calculateDate1.compareTo(goodDate1), 0);
			
			dateToTest1 = DateUtil.stringToDate("28/12/2012");
			logger.info("Date to test: " + DateUtil.dateToString(dateToTest1));
			goodDate1 = DateUtil.stringToDate("26/12/2012");
			logger.info("Good result: " + DateUtil.dateToString(goodDate1));
			calculateDate1 = WeekCalc.getFirstOpenWeekDay(dateToTest1, persCal);
			logger.info("Calculate result: " + DateUtil.dateToString(calculateDate1));
			assertEquals(calculateDate1.compareTo(goodDate1), 0);
			
		} catch (PersonnalCalendarException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (DateException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void testGetLastOpenWeekDay() {

		try {
			PersonnalCalendar persCal = new PersonnalCalendar();
			persCal.setName("5OpenDaysPerWeek");
			persCal.setYear(2012);
			persCal.initializeWithAllDaysOpen();
			persCal.addFrenchHolydays();
			persCal.addClosedDayOfWeek(6);
			persCal.addClosedDayOfWeek(7);

			Date dateToTest1 = DateUtil.stringToDate("13/04/2012");
			logger.info("Date to test: " + DateUtil.dateToString(dateToTest1));
			Date goodDate1 = DateUtil.stringToDate("13/04/2012");
			logger.info("Good result: " + DateUtil.dateToString(goodDate1));
			Date calculateDate1 = WeekCalc.getLastOpenWeekDay(dateToTest1, persCal);
			logger.info("Calculate result: " + DateUtil.dateToString(calculateDate1));
			assertEquals(calculateDate1.compareTo(goodDate1), 0);
			
			dateToTest1 = DateUtil.stringToDate("15/08/2012");
			logger.info("Date to test: " + DateUtil.dateToString(dateToTest1));
			goodDate1 = DateUtil.stringToDate("17/08/2012");
			logger.info("Good result: " + DateUtil.dateToString(goodDate1));
			calculateDate1 = WeekCalc.getLastOpenWeekDay(dateToTest1, persCal);
			logger.info("Calculate result: " + DateUtil.dateToString(calculateDate1));
			assertEquals(calculateDate1.compareTo(goodDate1), 0);
			
			dateToTest1 = DateUtil.stringToDate("06/06/2012");
			logger.info("Date to test: " + DateUtil.dateToString(dateToTest1));
			goodDate1 = DateUtil.stringToDate("08/06/2012");
			logger.info("Good result: " + DateUtil.dateToString(goodDate1));
			calculateDate1 = WeekCalc.getLastOpenWeekDay(dateToTest1, persCal);
			logger.info("Calculate result: " + DateUtil.dateToString(calculateDate1));
			assertEquals(calculateDate1.compareTo(goodDate1), 0);
			
			dateToTest1 = DateUtil.stringToDate("24/12/2012");
			logger.info("Date to test: " + DateUtil.dateToString(dateToTest1));
			goodDate1 = DateUtil.stringToDate("28/12/2012");
			logger.info("Good result: " + DateUtil.dateToString(goodDate1));
			calculateDate1 = WeekCalc.getLastOpenWeekDay(dateToTest1, persCal);
			logger.info("Calculate result: " + DateUtil.dateToString(calculateDate1));
			assertEquals(calculateDate1.compareTo(goodDate1), 0);
			
		} catch (PersonnalCalendarException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (DateException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
