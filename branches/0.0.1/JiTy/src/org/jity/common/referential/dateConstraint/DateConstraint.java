package org.jity.common.referential.dateConstraint;

import java.util.Date;

import org.jity.common.util.ListUtil;

/**
 * DateConstraint is a type of Constraint. The planification rule define one or few day when the
 * job must execute
 *  
 * @author 09344a
 *
 */
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

	public static final String[] OPERATOR_KEYWORDS = {"before","after","not","equal"};

	public static final String[] DAY_NUM_KEYWORDS = {"first","last"}; //,"all"};

	public static final String[] DAY_TYPE_KEYWORDS = {"calend","open"}; //,"close"};

	public static final String[] DAY_NAME_KEYWORDS = {"day","mon","tue","wed","thu","fri","sat","sun"};

	public static final String[] PERIOD_KEYWORDS = {"week","month","year","jan","feb","mar","apr","mai",
		"jun","jul","aug","sep","oct","nov","dec"};

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
	 * Return true if value exist in tab
	 * @param tab
	 * @param value
	 * @return
	 */
	private static boolean existInTab(String[] tab, String value) {
		for (int i=0;i<tab.length;i++) {
			if (tab[i].equals(value)) return true;
		}
		return false;
	}
		
	/**
	 * Return true if the constrainte is valid for an exec Date
	 * @param execDate
	 * @return
	 * @throws DateConstraintException
	 * @throws DateException 
	 */
	public boolean isAValidDate(Date execDate) throws DateConstraintException {

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
				throw new DateConstraintException("Syntax error in Planification Rule: "+this.planifRule);
			
			String operator = planifRuleSplit[0];
			String stringDayNumber = planifRuleSplit[1];
			String dayType = planifRuleSplit[2];
			String dayName = planifRuleSplit[3];
			String period = planifRuleSplit[4];

			// Syntax test
			if (! existInTab(OPERATOR_KEYWORDS, operator))
				throw new DateConstraintException(this.planifRule+": incorrect operator ("+ListUtil.tabToString(OPERATOR_KEYWORDS)+")");

			if (! existInTab(DAY_TYPE_KEYWORDS, dayType))
				throw new DateConstraintException(this.planifRule+": incorrect day type ("+ListUtil.tabToString(DAY_TYPE_KEYWORDS)+")");

			if (! existInTab(DAY_NAME_KEYWORDS, dayName))
				throw new DateConstraintException(this.planifRule+": incorrect day name ("+ListUtil.tabToString(DAY_NAME_KEYWORDS)+")");
			
			if (! existInTab(PERIOD_KEYWORDS, period))
				throw new DateConstraintException(this.planifRule+": incorrect day type ("+ListUtil.tabToString(PERIOD_KEYWORDS)+")");
						
			// TODO
			if (dayType.equals("close"))
				throw new DateConstraintException(this.planifRule+": dayType == close not yet implemented");
			
			// Transforming day number
			int dayNumber = 0;
			if (stringDayNumber.equals("first")) {
				dayNumber = 1;
			} else if (stringDayNumber.equals("last")) {
				dayNumber = -1;
			} else if (stringDayNumber.equals("all")) {
				throw new DateConstraintException(this.planifRule+": dayNumber == all not yet implemented");

				//dayNumber = 999;
				// TODO
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
				
				// If period is month name, calculating month number of execDate
				int execDateMonthNumber = MonthCalc.getMonthNumberByDate(execDate);
				
				// calculating month number of the rule
				int ruleMonthNumber = MonthCalc.getMonthNumberByName(period);

				// If month name are different, return false now
				if (execDateMonthNumber != ruleMonthNumber) return false;
				else period="execMonth"; // Same month as execDate
			}

			if (dayNumber > 0) {
				// Positive dayNumber
				
				// Test dayNumber value
				if (period.equals("week") && dayNumber > 7)
					throw new DateConstraintException(this.planifRule+": dayNumber cannot be > 7 when period = week");
				
				int maxNumberOfDaysInTheMonth = MonthCalc.getDayNumberInMonth(MonthCalc.getLastMonthDay(execDate));
				if ( (period.equals("month") || period.equals("execMonth")) && dayNumber > maxNumberOfDaysInTheMonth)
					throw new DateConstraintException(this.planifRule+": dayNumber cannot be > "+maxNumberOfDaysInTheMonth+" for this period");
				
				int maxNumberOfDaysInYear = YearCalc.getMaxNumberofDayInYear(YearCalc.getYearNumber(execDate));
				if (period.equals("year") && dayNumber > maxNumberOfDaysInYear)
					throw new DateConstraintException(this.planifRule+": dayNumber cannot be > "+maxNumberOfDaysInYear+" for this period");
				
			} else {
				// Negative dayNumber
				
				// Test dayNumber value
				if (period.equals("week") && dayNumber < -7)
					throw new DateConstraintException(this.planifRule+": dayNumber cannot be < -7 when period = week");
				
				int maxNumberOfDaysInTheMonth = MonthCalc.getDayNumberInMonth(MonthCalc.getLastMonthDay(execDate));
				if ( (period.equals("month") || period.equals("execMonth")) && dayNumber < -maxNumberOfDaysInTheMonth)
					throw new DateConstraintException(this.planifRule+": dayNumber cannot be < -"+maxNumberOfDaysInTheMonth+" for this period");
				
				int maxNumberOfDaysInYear = YearCalc.getMaxNumberofDayInYear(YearCalc.getYearNumber(execDate));
				if (period.equals("year") && dayNumber < -maxNumberOfDaysInYear)
					throw new DateConstraintException(this.planifRule+": dayNumber cannot be < -"+maxNumberOfDaysInYear+" for this period");
				
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
