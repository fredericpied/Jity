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

import java.util.Date;


public class PlanifRule {

	/**
	 * Planification rules Sentence = rule-dayNum-dayType-dayName-refCalc
	 * 
	 * operator is before after equal not
	 * 
	 * dayNum is in "all", “first”, “last” or number in 1 to 365
	 * 
	 * dayType is in "calend", "closed", "openned"
	 * 
	 * dayName is in “day”,”mon”,”tue”,”wed”,”thu”,”fri”,”sat”,”sun”
	 * 
	 * refCalc in “week”, “month”, “year”, all month
	 * 
	 * 
	 * Exemples: equal-first-calend-day-month before-first-calend-day-jun TODO
	 * (sentence AND sentence) TODO (sentence OR sentence)
	 * 
	 */
	private String planifSentence;

	/**
	 * Calendar used to interpret PlanifRule
	 */
	private PrivateCalendar calendar;

	private static final String[] OPERATOR_KEYWORDS = { "before", "after",
			"not", "equal" };
	private static final String[] DAY_NUM_KEYWORDS = { "all", "first", "last" };
	private static final String[] DAY_TYPE_KEYWORDS = { "calend", "openned",
			"closed" };
	private static final String[] DAY_NAME_KEYWORDS = { "day", "mon", "tue",
			"wed", "thu", "fri", "sat", "sun" };
	private static final String[] PERIOD_KEYWORDS = { "week", "month", "year" };

	/**
	 * Return true if value is in tab
	 * 
	 * @param tab
	 * @param value
	 * @return
	 */
	private static boolean valueIsInTab(String[] tab, String value) {
		for (int i = 0; i < tab.length; i++) {
			if (tab[i].equals(value))
				return true;
		}
		return false;
	}

	/**
	 * Return true if the date is a planed for planifRules
	 * 
	 * @param date
	 * @return
	 * @throws DateException
	 */
	public boolean isPlannedDay(Date date) throws DateException {

		// If calendar is not set, exception
		if (this.calendar == null) {
			throw new DateException("Calendar as not been set.");
		}

		// If planifString is not set, return true if its an open day
		// regard to calendar
		if (this.planifSentence == null) {
			return calendar.isOpenDay(date);
		}

		// Extract sentence
		String[] planifSentenceSplit = this.planifSentence.split("-");
		String operator = planifSentenceSplit[0];
		String dayNumber = planifSentenceSplit[1];
		String dayType = planifSentenceSplit[2];
		String dayName = planifSentenceSplit[3];
		String period = planifSentenceSplit[4];

		// Controling sentence format
		if (!valueIsInTab(OPERATOR_KEYWORDS, operator)) {
			throw new DateException(operator + " is not an valid operator.");
		}

		if (!valueIsInTab(DAY_TYPE_KEYWORDS, dayType)) {
			throw new DateException(dayType + " is not an valid day type.");
		}

		if (!valueIsInTab(DAY_NAME_KEYWORDS, dayName)) {
			throw new DateException(dayName + " is not an valid day name.");
		}

		if (!valueIsInTab(PERIOD_KEYWORDS, period)) {
			throw new DateException(period + " is not an valid period.");
		}

		if (!valueIsInTab(DAY_NUM_KEYWORDS, dayNumber)
				&& (Integer.parseInt(dayNumber) < 1 || Integer
						.parseInt(dayNumber) > 365)) {
			throw new DateException(dayNumber + " is not an valid day number.");
		}

		return false;
	}

}
