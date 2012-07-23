package org.jity.referential;

import java.util.Date;
import java.util.GregorianCalendar;

import org.jity.referential.dateCalc.DateException;
import org.jity.referential.dateCalc.MonthCalc;
import org.jity.referential.dateCalc.WeekCalc;
import org.jity.referential.dateCalc.YearCalc;

public class DateConstraint {

	private long id;
	/**
	 * Planification rules Sentence = rule-dayNum-dayType-dayName-refCalc
	 * 
	 * operator is before after equal not
	 * 
	 * dayNum is in "all", “first”, “last” or number in 1 to 365
	 * 
	 * dayType is in "calend", "close", "open"
	 * 
	 * dayName is in “day”,”mon”,”tue”,”wed”,”thu”,”fri”,”sat”,”sun”
	 * 
	 * refCalc in “week”, “month”, “year”, "jan","feb","mar","apr","mai","jun",
		"jul","aug","sep","oct","nov","dec"
	 * 
	 * 
	 * Exemples: equal-first-calend-day-month before-first-calend-day-jun TODO
	 * (sentence AND sentence) TODO (sentence OR sentence)
	 * 
	 */
	private String planifRule;

	private PersonnalCalendar persCalendar;

	private static final String OPERATOR_KEYWORDS = "before,after,not,equal";

	private static final String DAY_NUM_KEYWORDS = "all,first,last";

	private static final String DAY_TYPE_KEYWORDS = "calend,open,close";

	private static final String DAY_NAME_KEYWORDS = "day,mon,tue,wed,thu,fri,sat,sun";

	private static final String PERIOD_KEYWORDS = "week,month,year,jan,feb,mar,apr,mai,jun,jul,aug,sep,oct,nov,dec";

	public DateConstraint() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPlanifRule() {
		return planifRule;
	}

	public void setPlanifRule(String planifRule) {
		this.planifRule = planifRule;
	}

	public PersonnalCalendar getCalendar() {
		return persCalendar;
	}

	public void setCalendar(PersonnalCalendar calendar) {
		this.persCalendar = calendar;
	}

	/**
	 * Return true if the constrainte is valid for an exec Date
	 * @param execDate
	 * @return
	 * @throws DateConstraintException
	 * @throws DateException 
	 */
	public boolean isAValidDate(Date execDate) throws DateConstraintException, DateException {

		if (this.persCalendar == null) 
			throw new DateConstraintException("PersonnalCalendar is not defined for the Date Constraint");

		if (this.persCalendar.getYear() != YearCalc.getYearNumber(execDate)) 
			throw new DateConstraintException("PersonnalCalendar is not defined for year "+YearCalc.getYearNumber(execDate));

		try {
			// If it's a closed day in the calendar
			if (! this.persCalendar.isAnOpenDay(execDate))
				return false;

			// It's an open Days in the calendar

			// If no planif rules set, return true
			if (this.planifRule == null || this.planifRule.length() == 0) 
				return true; 

			// Extract sentence
			String[] planifRuleSplit = this.planifRule.split("_");
			
			// If sentence not compose of 5 characters sequence, throw Exception
			if (planifRuleSplit.length != 5)
				throw new DateConstraintException("Syntax error in Planification Rule");
			
			String operator = planifRuleSplit[0];
			String stringDayNumber = planifRuleSplit[1];
			String dayType = planifRuleSplit[2];
			String dayName = planifRuleSplit[3];
			String period = planifRuleSplit[4];

			// Syntax test
			if (! OPERATOR_KEYWORDS.contains(operator))
				throw new DateConstraintException(this.planifRule+": incorrect operator ("+OPERATOR_KEYWORDS+")");

			if (! DAY_TYPE_KEYWORDS.contains(dayType))
				throw new DateConstraintException(this.planifRule+": incorrect day type ("+DAY_TYPE_KEYWORDS+")");

			if (! DAY_NAME_KEYWORDS.contains(dayName))
				throw new DateConstraintException(this.planifRule+": incorrect day name ("+DAY_NAME_KEYWORDS+")");
			
			if (! PERIOD_KEYWORDS.contains(period))
				throw new DateConstraintException(this.planifRule+": incorrect day type ("+PERIOD_KEYWORDS+")");
						
			// TODO
			if (dayType.equals("close"))
				throw new DateConstraintException(this.planifRule+": dayType == close not yet implemented");
			
			// Transforming day numeber
			int dayNumber = 0;
			if (stringDayNumber.equals("first")) {
				dayNumber = 1;
			} else if (stringDayNumber.equals("last")) {
				dayNumber = -1;
			} else {
				dayNumber = Integer.parseInt(stringDayNumber);
			}

			
//			// DAY NAME
//			if (! dayName.equals("day")) {
//				int execDateDayNumberInWeek = WeekCalc.getDayNumberInWeekByDate(execDate);
//				int ruleDayNumberInWeek = WeekCalc.getDayNumberInWeekByName(dayName);
//
//				// If day name are different, return false now
//				if (execDateDayNumberInWeek != ruleDayNumberInWeek) return false;
//				else dayName="execDay"; // Same dayname as execDate
//
//			} else { // if (! dayName.equals("day")) {
//				dayName="execDay";
//			}

			// PERIOD
			if (! period.equals("week") 
					&& ! period.equals("month") 
					&& ! period.equals("year")) {
				
				// If period is month name
				int execDateMonthNumber = MonthCalc.getMonthNumberByDate(execDate);
				
				int ruleMonthNumber = MonthCalc.getMonthNumberByName(period);

				// If month name are different, return false now
				if (execDateMonthNumber != ruleMonthNumber) return false;
				else period="execMonth"; // Same month as execDate
			}

			Date calculateDate = null;
			
			if (dayName.equals("day")) {
				if (period.equals("week")) {
					calculateDate = WeekCalc.getNiemeWeekDay(execDate, this.persCalendar, dayNumber, dayType);
				} else if (period.equals("month") || period.equals("execMonth")) {
					calculateDate = MonthCalc.getNiemeMonthDay(execDate, this.persCalendar, dayNumber, dayType);
				} else if (period.equals("year")) {
					calculateDate = YearCalc.getNiemeYearDay(execDate, this.persCalendar, dayNumber, dayType);
				}
			} else {
				// dayName is a name of a Week day
				// TODO 
				if (!dayName.equals("day"))
					throw new DateConstraintException(this.planifRule+": dayName != day not yet implemented");
			}
			
			// No valid day for rules
			if (calculateDate == null) return false;
				//throw new DateConstraintException("Unable to resolv planification rule:"+this.planifRule);

			//before after equal not
			// the value 0 if the calculateDate is equal to execDate;
			// a value less than 0 if execDate is before calculateDate;
			// and a value greater than 0 if execDate is after calculateDate.
			if (operator.equals("equal")) {
				if (execDate.compareTo(calculateDate) == 0) return true;
				else return false;
			} else if (operator.equals("before")) {
				if (execDate.compareTo(calculateDate) < 0) return true;
				else return false;
			} else if (operator.equals("after")) {
				if (execDate.compareTo(calculateDate) > 0) return true;
				else return false;
			}  else if (operator.equals("not")) {
				if (execDate.compareTo(calculateDate) != 0) return true;
				else return false;
			}
			
			throw new DateConstraintException("Unable to resolv planification rule: "+this.planifRule);


		} catch (PersonnalCalendarException e) {
			throw new DateConstraintException(e.getMessage());
		}

	}

}
