/**
 *  JiTy : Open Job Scheduler
 *  Copyright (C) 2012 
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free
 *  Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA 02111-1307, USA
 *
 *  For questions, suggestions:
 *
 *  http://www.assembla.com/spaces/jity
 *
 */
package org.jity.common.referential.dateConstraint;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.jity.common.util.DateUtil;


public abstract class WeekCalc {
	private static final Logger logger = Logger.getLogger(WeekCalc.class);  
	/**
	 * Days name tab
	 */
	public static String[] tabDaysNames = {"mon","tue","wed","thu","fri","sat","sun"};

	/**
	 * True if day name exist
	 * @param dayName
	 * @return
	 */
	public static boolean isValidDayName(String dayName) {
		
		for (int i=0;i<tabDaysNames.length;i++) {
			if (tabDaysNames[i].equals(dayName)) return true;
		}
		
		return false;
	}
	
	/**
	 * Get first day of the week
	 * @param date
	 * @return
	 */
	public static Date getFirstWeekDay(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_WEEK, cal.getActualMinimum(Calendar.DAY_OF_WEEK)+1);
		return cal.getTime();
	}
	
	/**
	 * Get last day of the week
	 * @param date
	 * @return
	 */
	public static Date getLastWeekDay(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK)+1);
		return cal.getTime();
	}
	
	/**
	 * Get N ieme day of the week type close or open
	 * @param dateToTest
	 * @param persCal
	 * @param nbDay
	 * @return Date
	 * @throws DateConstraintException 
	 */
	public static Date getNiemeWeekDay(Date dateToTest, PersonnalCalendar persCal, int nbDay, String dayType)
			throws DateConstraintException {

		Calendar calToTest = new GregorianCalendar();
		calToTest.clear();

		if (! dayType.equals("close") &&
				! dayType.equals("open") &&
				! dayType.equals("calend"))
			throw new DateConstraintException("dayType must be \"calend\", \"open\" or \"close\"");
		
		if (nbDay > 0) {

			// Initialize a new calendar whith the first day of the week
			calToTest.setTime(getFirstWeekDay(dateToTest));
			logger.debug("Debut semaine: "+DateUtil.dateToString(calToTest.getTime()));
			
			// Last day of the week
			Date lastWeekDay = getLastWeekDay(dateToTest);
			logger.debug("Fin semaine: "+DateUtil.dateToString(lastWeekDay));
			
			if (nbDay > 7)
				throw new DateConstraintException("nbDay must be < 7");
			
			int dayCount = 0;

			// While end of week not reached test each day of week
			do {
				
				if (dayType.equals("close") && ! persCal.isAnOpenDay(calToTest.getTime())) {
					// If it's an close day, count 1 day
					dayCount++;

					// If nbDay reach, return actual cal value
					if (dayCount == nbDay)
						return calToTest.getTime();
				
				} else if (dayType.equals("open") && persCal.isAnOpenDay(calToTest.getTime())) {
					// If it's an open day, count 1 day
					dayCount++;

					// If nbDay reach, return actual cal value
					if (dayCount == nbDay)
						return calToTest.getTime();
				
				} else if (dayType.equals("calend")) {
					dayCount++;

					// If nbDay reach, return actual cal value
					if (dayCount == nbDay)
						return calToTest.getTime();
				}
				
				calToTest.add(Calendar.DAY_OF_WEEK, 1);

			} while (lastWeekDay.compareTo(calToTest.getTime()) != -1);

		} else {
			
			// Initialize a new calendar this the first day of the week
			calToTest.setTime(getLastWeekDay(dateToTest));

			// Last day of the week
			Date firstWeekDay = getFirstWeekDay(dateToTest);

			if (nbDay < -7)
				throw new DateConstraintException("nbDay must be > -7");

			int dayCount = 0;

			// While end of week not reached test each day of week
			do {

				if (dayType.equals("close") && ! persCal.isAnOpenDay(calToTest.getTime())) {
					// If it's an close day, count 1 day
					dayCount--;

					// If nbDay reach, return actual cal value
					if (dayCount == nbDay)
						return calToTest.getTime();
				
				} else if (dayType.equals("open") && persCal.isAnOpenDay(calToTest.getTime())) {
					// If it's an open day, count 1 day
					dayCount--;

					// If nbDay reach, return actual cal value
					if (dayCount == nbDay)
						return calToTest.getTime();
				} else if (dayType.equals("calend")) {
					dayCount--;

					// If nbDay reach, return actual cal value
					if (dayCount == nbDay)
						return calToTest.getTime();
				}
				
				calToTest.add(Calendar.DAY_OF_WEEK, -1);

			} while (firstWeekDay.compareTo(calToTest.getTime()) != 1);

		}
		

		throw new DateConstraintException("enable to getNiemeWeekDay");
	}
		
	/**
	 * Return day number in the week according to the day name
	 * @param dayName
	 * @return int
	 * @throws DateConstraintException 
	 */
	public static int getDayNumberInWeekByName(String dayName) throws DateConstraintException {
		for (int i=0;i<tabDaysNames.length;i++) {
			if (tabDaysNames[i].equals(dayName)) return i+1;
		}
		throw new DateConstraintException("Day name "+dayName+" not exist.");
	}

	/**
	 * Return day name (french lu->Di = 1->7)
	 * TODO gérer les numéros de jours en anglais
	 * @param weekDayNumber
	 * @return
	 * @throws DateConstraintException
	 */
	public static String getDayNameInWeekByNumber(int weekDayNumber) throws DateConstraintException {
		if (weekDayNumber < 1 || weekDayNumber > 7 ) {
			throw new DateConstraintException("week day number "+weekDayNumber+" not exist.");
		}
		return tabDaysNames[weekDayNumber-1];
	}
	
	/**
	 * Get day number in week (mon = 1, sun = 7) of dateToTest
	 * @param dateToTest
	 * @return int
	 */
	public static int getDayNumberInWeekByDate(Date dateToTest) {
		Calendar calToTest = new GregorianCalendar();
		calToTest.setTime(dateToTest);
		
		int dayNumberInWeek = calToTest.get(Calendar.DAY_OF_WEEK); //Sunday = 1
		if (dayNumberInWeek == 1) {
			dayNumberInWeek = 7;
		} else  {
			dayNumberInWeek = dayNumberInWeek - 1;
		}
		
		return dayNumberInWeek;
	}
	
}
