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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;


public class PrivateCalendar {

	private String name;

	private String description;

	/**
	 * Calendar year
	 */
	private int year = 0;

	/**
	 * Open Days Of week set by user (true if opened, false if closed) First
	 * (#0) is Monday, #6 is Sunday
	 */
	private boolean[] openDaysOfWeek = new boolean[7];

	/**
	 * if true, French public holidays are suppressed form open days list
	 */
	private boolean applyFrenchPublicHoliday = false;

	/**
	 * Contains open days of the year
	 */
	private ArrayList<Date> openDaysList = new ArrayList<Date>();

	/**
	 * is open days list OK ?
	 */
	private boolean openDaysListOK = false;

	/**
	 * Open Days initialisation for current year
	 * 
	 * @param
	 * @throws DateException
	 */
	private void OpenDaysListInitialisation() throws DateException {

		// Test is year is set
		if (this.year == 0) {
			throw new DateException("Year not set for Calendar " + this.name
					+ ".");
		}

		// First, we set all date of the year open
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.setTime(YearCalc.getFirstYearDay(this.year));

		do {
			// Add all dates in the list
			this.openDaysList.add(cal.getTime());
			cal.add(Calendar.DAY_OF_MONTH, 1);
		} while (cal.getTime().compareTo(YearCalc.getLastYearDay(this.year)) != 0);

		// Second, apply Week days that user set to closed
		Iterator<Date> iterOpenDays = this.openDaysList.iterator();
		while (iterOpenDays.hasNext()) {
			Date date = iterOpenDays.next();

			int dayNumberInWeek = WeekCalc.getDayNumberInWeek(date);

			// if the week day is closed, suppress it from the open day list
			for (int i = 1; i <= 7; i++) {
				if (dayNumberInWeek == i && !this.openDaysOfWeek[i - 1])
					iterOpenDays.remove();
			}

			// if (numeroJour == 1 && !this.openDaysOfWeek[0])
			// iterOpenDays.remove();
			// if (numeroJour == 2 && !this.openDaysOfWeek[1])
			// iterOpenDays.remove();
			// if (numeroJour == 3 && !this.openDaysOfWeek[2])
			// iterOpenDays.remove();
			// if (numeroJour == 4 && !this.openDaysOfWeek[3])
			// iterOpenDays.remove();
			// if (numeroJour == 5 && !this.openDaysOfWeek[4])
			// iterOpenDays.remove();
			// if (numeroJour == 6 && !this.openDaysOfWeek[5])
			// iterOpenDays.remove();
			// if (numeroJour == 7 && !this.openDaysOfWeek[6])
			// iterOpenDays.remove();
		}

		// Third, apply public holiday if set
		if (this.applyFrenchPublicHoliday) {
			iterOpenDays = this.openDaysList.iterator();
			while (iterOpenDays.hasNext()) {
				Date date = iterOpenDays.next();
				if (YearCalc.isFrenchPublicHoliday(date))
					iterOpenDays.remove();
			}
		}

		openDaysListOK = true;

	}

	/**
	 * True if date is openned in this calendar
	 * 
	 * @param date
	 * @return
	 * @throws DateException
	 */
	public boolean isOpenDay(Date date) throws DateException {

		// Test if calendar as being initialized
		if (!this.openDaysListOK) {
			this.OpenDaysListInitialisation();
		}

		if (YearCalc.getYearNumber(date) != this.year) {
			throw new DateException("Calendar is set for year " + this.year
					+ " not for year" + YearCalc.getYearNumber(date));
		}

		if (this.openDaysList.contains(date)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * True if date is closed in this calendar
	 * 
	 * @param date
	 * @return
	 * @throws DateException
	 */
	public boolean isClosedDay(Date date) throws DateException {
		return !this.isOpenDay(date);
	}

}
