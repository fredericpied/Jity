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
package org.jity.referential.dateCalc;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jity.referential.PersonnalCalendar;
import org.jity.referential.PersonnalCalendarException;

public abstract class MonthCalc {

	/**
	 * Months name tab
	 */
	public static String[] tabMonthsNames = {"jan","feb","mar","apr","mai","jun",
		"jul","aug","sep","oct","nov","dec"};
	
	
	/**
	 * True if month name exist
	 * @param nomMois
	 * @return
	 */
	public static boolean isValidMonthName(String MonthName) {
		
		for (int i=0;i<tabMonthsNames.length;i++) {
			if (tabMonthsNames[i].equals(MonthName)) return true;
		}
		
		return false;
	}
		
	/**
	 * Get month number in the year
	 * @param monthName
	 * @return
	 * @throws DateException 
	 */
	public static int getMonthNumberByName(String monthName) throws DateException {
		for (int i=0;i<tabMonthsNames.length;i++) {
			if (tabMonthsNames[i].equals(monthName)) return i+1;
		}
		throw new DateException("Month "+monthName+" not exist");
	}
	
	/**
	 * Get Month numeber
	 * @param date
	 * @return
	 */
	public static int getMonthNumberByDate(Date date) {
		Calendar calToTest = new GregorianCalendar();
		calToTest.setTime(date);
		return calToTest.get(Calendar.MONTH)+1; // January = 0
	}
	
	
	/**
	 * Get month name by number
	 * @param numMois
	 * @return
	 * @throws ExceptionVTOM
	 */
	public static String getMonthNameByNumber(int monthNum) throws DateException {
		if (monthNum < 1 || monthNum > 12 ) {
			throw new DateException("Month number "+monthNum+" not exist");
		}
		return tabMonthsNames[monthNum-1];
	}
	
	/**
	 * Get last month day Date
	 * @param date
	 * @return
	 */
	public static Date getLastMonthDay(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}
	
	/**
	 * Get first month day
	 * @param date
	 * @return
	 */
	public static Date getFirstMonthDay(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	/**
	 * Return first open day in the month according to specified PersonnalCalendar
	 * @param date
	 * @param persCal
	 * @return
	 * @throws PersonnalCalendarException
	 */
	public static Date getFirstOpenMonthDay(Date date, PersonnalCalendar persCal) throws PersonnalCalendarException {
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.setTime(getFirstMonthDay(date));
		
		int maxNbDay = getDayNumberInMonth(getLastMonthDay(date));
		
		for (int i=1;i<=maxNbDay;i++) {
			if (persCal.isAnOpenDay(cal.getTime())) {
				return cal.getTime();
			} else {
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
			
		return null;
	}
	
	/**
	 * Return n ieme open day in the month according to specified PersonnalCalendar
	 * @param date
	 * @param persCal
	 * @return
	 * @throws PersonnalCalendarException
	 */
	public static Date getNiemeOpenMonthDay(Date date, PersonnalCalendar persCal, int nbDay) throws PersonnalCalendarException {
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.setTime(getFirstMonthDay(date));
		
		int nbDayCount = 0;
		
		int maxNbDay = getDayNumberInMonth(getLastMonthDay(date));
		
		for (int i=1;i<=maxNbDay;i++) {
			if (persCal.isAnOpenDay(cal.getTime())) {
				nbDayCount++;
				return cal.getTime();
			} else {
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
			
		return null;
	}
	
	/**
	 * Return n ieme close day in the month according to specified PersonnalCalendar
	 * @param date
	 * @param persCal
	 * @return
	 * @throws PersonnalCalendarException
	 */
	public static Date getNiemeCloseMonthDay(Date date, PersonnalCalendar persCal, int nbDay) throws PersonnalCalendarException {
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.setTime(getFirstMonthDay(date));
		
		int nbDayCount = 0;
		
		int maxNbDay = getDayNumberInMonth(getLastMonthDay(date));
		
		for (int i=1;i<=maxNbDay;i++) {
			if (!persCal.isAnOpenDay(cal.getTime())) {
				nbDayCount++;
				return cal.getTime();
			} else {
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
			
		return null;
	}
	
	/**
	 * Return first close day in the month according to specified PersonnalCalendar
	 * @param date
	 * @param persCal
	 * @return
	 * @throws PersonnalCalendarException
	 */
	public static Date getFirstCloseMonthDay(Date date, PersonnalCalendar persCal) throws PersonnalCalendarException {
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.setTime(getFirstMonthDay(date));
		
		int maxNbDay = getDayNumberInMonth(getLastMonthDay(date));
		
		for (int i=1;i<=maxNbDay;i++) {
			if (!persCal.isAnOpenDay(cal.getTime())) {
				return cal.getTime();
			} else {
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
			
		return null;
	}
	
	/**
	 * Return last open day in the month according to specified PersonnalCalendar
	 * @param date
	 * @param persCal
	 * @return
	 * @throws PersonnalCalendarException
	 */
	public static Date getLastOpenMonthDay(Date date, PersonnalCalendar persCal) throws PersonnalCalendarException {
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.setTime(getLastMonthDay(date));
		
		int maxNbDay = getDayNumberInMonth(getLastMonthDay(date));
		
		for (int i=1;i<=maxNbDay;i++) {
			if (persCal.isAnOpenDay(cal.getTime())) {
				return cal.getTime();
			} else {
				cal.add(Calendar.DAY_OF_MONTH, -1);
			}
		}
			
		return null;
	}
	
	/**
	 * Return last close day in the month according to specified PersonnalCalendar
	 * @param date
	 * @param persCal
	 * @return
	 * @throws PersonnalCalendarException
	 */
	public static Date getLastCloseMonthDay(Date date, PersonnalCalendar persCal) throws PersonnalCalendarException {
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.setTime(getLastMonthDay(date));
		
		int maxNbDay = getDayNumberInMonth(getLastMonthDay(date));
		
		for (int i=1;i<=maxNbDay;i++) {
			if (!persCal.isAnOpenDay(cal.getTime())) {
				return cal.getTime();
			} else {
				cal.add(Calendar.DAY_OF_MONTH, -1);
			}
		}
			
		return null;
	}
	
	/**
	 * return true if date1 and date2 is in the same month
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameMonth(Date date1, Date date2) {
		Calendar cal1 = new GregorianCalendar();
		cal1.setTime(date1);
		
		Calendar cal2 = new GregorianCalendar();
		cal2.setTime(date2);
	
		return cal2.get(Calendar.MONTH) == cal1.get(Calendar.MONTH);
	}
		
	/**
	 * Get Day number in the month
	 * @param date
	 * @return
	 */
	public static int getDayNumberInMonth(Date date) {
		Calendar calToTest = new GregorianCalendar();
		calToTest.setTime(date);
		return calToTest.get(Calendar.DAY_OF_MONTH);
	}
	
}
