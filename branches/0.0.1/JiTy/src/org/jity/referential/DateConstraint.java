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

	private static final String[] OPERATOR_KEYWORDS = { "before", "after",
		"not", "equal" };

	private static final String[] DAY_NUM_KEYWORDS = { "all", "first", "last" };

	//  private static final String[] DAY_TYPE_KEYWORDS = { "calend", "openned",
	//		"closed" };

	private static final String[] DAY_NAME_KEYWORDS = { "day", "mon", "tue",
		"wed", "thu", "fri", "sat", "sun" };

	private static final String[] PERIOD_KEYWORDS = { "week", "month", "year" };

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
			String[] planifRuleSplit = this.planifRule.split("-");
			String operator = planifRuleSplit[0];
			String dayNumber = planifRuleSplit[1];
			String dayType = planifRuleSplit[2];
			String dayName = planifRuleSplit[3];
			String period = planifRuleSplit[4];

			// DAY NAME
			if (! dayName.equals("day")) {
				int execDateDayNumberInWeek = WeekCalc.getDayNumberInWeek(execDate);
				int ruleDayNumberInWeek = WeekCalc.getDayNumberInWeekByName(dayName);

				// If day name are different, return false now
				if (execDateDayNumberInWeek != ruleDayNumberInWeek) return false;
				else dayName="execDay"; // Same dayname as execDate

			} else { // if (! dayName.equals("day")) {
				dayName="execDay";
			}

			// PERIOD
			if (! period.equals("week") && ! period.equals("month") && ! period.equals("year")) {
				int execDateMonthNumber = MonthCalc.getMonthNumberByDate(execDate);
				int ruleMonthNumber = MonthCalc.getMonthNumberByName(period);

				// If month name are different, return false now
				if (execDateMonthNumber != ruleMonthNumber) return false;
				else period="execMonth"; // Same month as execDate

			}

			Date calculateDate = null;

			// DAY NUMBER
			if (dayNumber.equals("first")) {

				if (period.equals("week")) {

					if (dayType.equals("open")) {
						calculateDate = WeekCalc.getFirstOpenWeekDay(execDate, this.persCalendar);
					} else if (dayType.equals("close")) {
						calculateDate = WeekCalc.getFirstCloseWeekDay(execDate, this.persCalendar);
					} else if (dayType.equals("calend")) {
						calculateDate = WeekCalc.getFirstWeekDay(execDate);
					}


				} else if (period.equals("month") || period.equals("execMonth")) {

					if (dayType.equals("open")) {
						calculateDate = MonthCalc.getFirstOpenMonthDay(execDate, this.persCalendar);
					} else if (dayType.equals("close")) {
						calculateDate = MonthCalc.getFirstCloseMonthDay(execDate, this.persCalendar);
					} else if (dayType.equals("calend")) {
						calculateDate = MonthCalc.getFirstMonthDay(execDate);
					}

				} else if (period.equals("year")) {

					if (dayType.equals("open")) {
						calculateDate = YearCalc.getFirstOpenYearDay(YearCalc.getYearNumber(execDate), this.persCalendar);
					} else if (dayType.equals("close")) {
						calculateDate = YearCalc.getFirstCloseYearDay(YearCalc.getYearNumber(execDate), this.persCalendar);
					} else if (dayType.equals("calend")) {
						calculateDate = YearCalc.getFirstYearDay(YearCalc.getYearNumber(execDate));
					}

				}

			} else if (dayNumber.equals("last")) {

				if (period.equals("week")) {
					if (dayType.equals("open")) {
						calculateDate = WeekCalc.getLastOpenWeekDay(execDate, this.persCalendar);
					} else if (dayType.equals("close")) {
						calculateDate = WeekCalc.getLastCloseWeekDay(execDate, this.persCalendar);
					} else if (dayType.equals("calend")) {
						calculateDate = WeekCalc.getLastWeekDay(execDate);
					}

				} else if (period.equals("month")) {

					if (dayType.equals("open")) {
						calculateDate = MonthCalc.getLastOpenMonthDay(execDate, this.persCalendar);
					} else if (dayType.equals("close")) {
						calculateDate = MonthCalc.getLastCloseMonthDay(execDate, this.persCalendar);
					} else if (dayType.equals("calend")) {
						calculateDate = MonthCalc.getLastMonthDay(execDate);
					}

				} else if (period.equals("year")) {

					if (dayType.equals("open")) {
						calculateDate = YearCalc.getLastOpenYearDay(YearCalc.getYearNumber(execDate), this.persCalendar);
					} else if (dayType.equals("close")) {
						calculateDate = YearCalc.getLastCloseYearDay(YearCalc.getYearNumber(execDate), this.persCalendar);
					} else if (dayType.equals("calend")) {
						calculateDate = YearCalc.getLastYearDay(YearCalc.getYearNumber(execDate));
					}

				}

			} else {
				throw new DateConstraintException("Unable to resolv planification rule:"+this.planifRule+"(numeric stringDayNumber)");
			}



			//if (calculateDate == null)
			//return false;

			//before after equal not
			// the value 0 if the calculateDate is equal to execDate;
			// a value less than 0 if execDate is before calculateDate;
			// and a value greater than 0 if execDate is after calculateDate.
			if (operator.equals("equal")) {
				if (execDate.compareTo(calculateDate) == 0) return true;
				else return false;
			} else if (operator.equals("before")) {
				if (execDate.compareTo(calculateDate) > 0) return true;
				else return false;
			} else if (operator.equals("after")) {
				if (execDate.compareTo(calculateDate) < 0) return true;
				else return false;
			}  else if (operator.equals("not")) {
				if (execDate.compareTo(calculateDate) != 0) return true;
				else return false;
			}
			
			throw new DateConstraintException("Unable to resolv planification rule:"+this.planifRule);


		} catch (PersonnalCalendarException e) {
			throw new DateConstraintException(e.getMessage());
		}

	}

}
