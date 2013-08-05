package org.jity.common.referential.dateConstraint;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import org.jity.common.util.DateUtil;
import org.jity.common.util.ListUtil;

/**
 * DateConstraint is a type of Constraint. The planification rule define one or few day when the
 * job must execute
 *  
 * @author 09344a
 *
 */
public class DateConstraint {
	private static final Logger logger = Logger.getLogger(DateConstraint.class);  
	private long id;
	/**
	 * Planification rule format:
	 *	<OPERATOR_KEYWORDS>_<DAY_NUM_KEYWORDS>_<DAY_TYPE_KEYWORDS>_<DAY_NAME_KEYWORDS>_<PERIOD_KEYWORDS>
	 * sentences is composed by 5 keyword separates by "_" (underscore)
	 * where:
	 * <OPERATOR_KEYWORDS> in {"before","after","not","equal"}
	 * <DAY_NUM_KEYWORDS> in {"first","last"} or is an integer between -31 and 31 excluding 0
	 * <DAY_TYPE_KEYWORDS> in {"calend","open"}
	 * <DAY_NAME_KEYWORDS> in {"day","mon","tue","wed","thu","fri","sat","sun"}
	 * <PERIOD_KEYWORDS> in {"week","month","year","jan","feb","mar","apr","mai",
	 *					"jun","jul","aug","sep","oct","nov","dec"};
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
	 */
	public boolean isAValidDate(Date execDate) throws DateConstraintException {

		if (this.persCalendar == null) 
			throw new DateConstraintException("PersonnalCalendar is not defined for the Date Constraint");

		if (this.persCalendar.getYear() != YearCalc.getYearNumber(execDate)) 
			throw new DateConstraintException("PersonnalCalendar is not defined for year "+YearCalc.getYearNumber(execDate));

			// If no planif rules set, return true
			if (this.planifRule == null || this.planifRule.length() == 0) {
				return persCalendar.isAnOpenDay(execDate);
			}


			// Extract sentence
			String[] planifRuleSplit = this.planifRule.split("_");
			
			// If sentence not compose of 5 characters sequence, throw Exception
			if (planifRuleSplit.length != 5)
				throw new DateConstraintException(this.planifRule+": Syntax error in Planification Rule: Separator must be an underscore \"_\"");
			
			String operator = planifRuleSplit[0];
			String stringDayNumber = planifRuleSplit[1];
			String dayType = planifRuleSplit[2];
			String dayName = planifRuleSplit[3];
			String period = planifRuleSplit[4];

			// Syntax test
			if (! existInTab(OPERATOR_KEYWORDS, operator))
				throw new DateConstraintException(this.planifRule+": operator ("+operator+") must be in {"+ListUtil.tabToString(OPERATOR_KEYWORDS)+"}");

			if (! existInTab(DAY_TYPE_KEYWORDS, dayType))
				throw new DateConstraintException(this.planifRule+": day type ("+dayType+") must be in {"+ListUtil.tabToString(DAY_TYPE_KEYWORDS)+"}");

			if (! existInTab(DAY_NAME_KEYWORDS, dayName))
				throw new DateConstraintException(this.planifRule+": day name ("+dayName+") must be in {"+ListUtil.tabToString(DAY_NAME_KEYWORDS)+"}");
			
			if (! existInTab(PERIOD_KEYWORDS, period))
				throw new DateConstraintException(this.planifRule+": period ("+period+") must be in {"+ListUtil.tabToString(PERIOD_KEYWORDS)+"}");


				// if opérator is equal and it's a closed day in the calendar and planif rules dont includes calend day 
				if (operator.equals("equal") && dayType.equals("open")
						&& ! this.persCalendar.isAnOpenDay(execDate))
					return false;

			
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

				// Si l'opérateur est équal et que le mois courant est différent du mois de planification
				// on sort de suite.
				if (operator.equals("equal") && execDateMonthNumber != ruleMonthNumber) return false;
				
				//if (execDateMonthNumber == ruleMonthNumber) period="execMonth"; // Same month as execDate
			}
			
			if (dayNumber > 0) {
				// Positive dayNumber
				
				// Test dayNumber value
				if (period.equals("week")) {
					if (dayNumber > 7)
						throw new DateConstraintException(this.planifRule+": dayNumber cannot be > 7 when period = week");
				} else if (period.equals("month")) {
					int maxNumberOfDaysInTheMonth = MonthCalc.getDayNumberInMonth(MonthCalc.getLastMonthDay(execDate));
					if (dayNumber > maxNumberOfDaysInTheMonth)
						throw new DateConstraintException(this.planifRule+": dayNumber cannot be > "+maxNumberOfDaysInTheMonth+" for this period");
				} else if (period.equals("year")) {
					int maxNumberOfDaysInYear = YearCalc.getMaxNumberofDayInYear(YearCalc.getYearNumber(execDate));
					if (dayNumber > maxNumberOfDaysInYear)
						throw new DateConstraintException(this.planifRule+": dayNumber cannot be > "+maxNumberOfDaysInYear+" for this period");	
				} else {

					Calendar cal = new GregorianCalendar();
					cal.clear();
					cal.setTime(execDate);
					
					if (period.equals("jan")) cal.set(Calendar.MONTH, 0);
					if (period.equals("feb")) cal.set(Calendar.MONTH, 1);
					if (period.equals("mar")) cal.set(Calendar.MONTH, 2);
					if (period.equals("apr")) cal.set(Calendar.MONTH, 3);
					if (period.equals("mai")) cal.set(Calendar.MONTH, 4);
					if (period.equals("jun")) cal.set(Calendar.MONTH, 5);
					if (period.equals("jul")) cal.set(Calendar.MONTH, 6);
					if (period.equals("aug")) cal.set(Calendar.MONTH, 7);
					if (period.equals("sep")) cal.set(Calendar.MONTH, 8);
					if (period.equals("oct")) cal.set(Calendar.MONTH, 9);
					if (period.equals("nov")) cal.set(Calendar.MONTH, 10);
					if (period.equals("dec")) cal.set(Calendar.MONTH, 11);
					
					int maxNumberOfDaysInTheMonth = MonthCalc.getDayNumberInMonth(MonthCalc.getLastMonthDay(cal.getTime()));
					if (dayNumber > maxNumberOfDaysInTheMonth)
						throw new DateConstraintException(this.planifRule+": dayNumber cannot be > "+maxNumberOfDaysInTheMonth+" for this period");
					
				}
				
				
				
				
			} else {
				// Negative dayNumber
				
				// Test dayNumber value
				if (period.equals("week")) {
					if (dayNumber < -7)
						throw new DateConstraintException(this.planifRule+": dayNumber cannot be < -7 when period = week");
				} else if (period.equals("month")) {
					int maxNumberOfDaysInTheMonth = MonthCalc.getDayNumberInMonth(MonthCalc.getLastMonthDay(execDate));
					if (dayNumber < -maxNumberOfDaysInTheMonth)
						throw new DateConstraintException(this.planifRule+": dayNumber cannot be < -"+maxNumberOfDaysInTheMonth+" for this period");
				} else if (period.equals("year")) {
					int maxNumberOfDaysInYear = YearCalc.getMaxNumberofDayInYear(YearCalc.getYearNumber(execDate));
					if (dayNumber < -maxNumberOfDaysInYear)
						throw new DateConstraintException(this.planifRule+": dayNumber cannot be < -"+maxNumberOfDaysInYear+" for this period");
				} else {
				
					Calendar cal = new GregorianCalendar();
					cal.clear();
					cal.setTime(execDate);
					
					if (period.equals("jan")) cal.set(Calendar.MONTH, 0);
					if (period.equals("feb")) cal.set(Calendar.MONTH, 1);
					if (period.equals("mar")) cal.set(Calendar.MONTH, 2);
					if (period.equals("apr")) cal.set(Calendar.MONTH, 3);
					if (period.equals("mai")) cal.set(Calendar.MONTH, 4);
					if (period.equals("jun")) cal.set(Calendar.MONTH, 5);
					if (period.equals("jul")) cal.set(Calendar.MONTH, 6);
					if (period.equals("aug")) cal.set(Calendar.MONTH, 7);
					if (period.equals("sep")) cal.set(Calendar.MONTH, 8);
					if (period.equals("oct")) cal.set(Calendar.MONTH, 9);
					if (period.equals("nov")) cal.set(Calendar.MONTH, 10);
					if (period.equals("dec")) cal.set(Calendar.MONTH, 11);
					
					int maxNumberOfDaysInTheMonth = MonthCalc.getDayNumberInMonth(MonthCalc.getLastMonthDay(cal.getTime()));
					if (dayNumber < -maxNumberOfDaysInTheMonth)
						throw new DateConstraintException(this.planifRule+": dayNumber cannot be < -"+maxNumberOfDaysInTheMonth+" for this period");
				
				}
			}

			
			Date calculateDate = null;
			
				if (dayName.equals("day")) {
					if (period.equals("week")) {
						calculateDate = WeekCalc.getNiemeWeekDay(execDate, this.persCalendar, dayNumber, dayType);
					} else if (period.equals("year")) {
						calculateDate = YearCalc.getNiemeYearDay(execDate, this.persCalendar, dayNumber, dayType);
					} else if (period.equals("month")) {
						calculateDate = MonthCalc.getNiemeMonthDay(execDate, this.persCalendar, dayNumber, dayType);
					} else if (existInTab(PERIOD_KEYWORDS, period)) {
						
						Calendar cal = new GregorianCalendar();
						cal.clear();
						cal.setTime(execDate);
						
						if (period.equals("jan")) cal.set(Calendar.MONTH, 0);
						if (period.equals("feb")) cal.set(Calendar.MONTH, 1);
						if (period.equals("mar")) cal.set(Calendar.MONTH, 2);
						if (period.equals("apr")) cal.set(Calendar.MONTH, 3);
						if (period.equals("mai")) cal.set(Calendar.MONTH, 4);
						if (period.equals("jun")) cal.set(Calendar.MONTH, 5);
						if (period.equals("jul")) cal.set(Calendar.MONTH, 6);
						if (period.equals("aug")) cal.set(Calendar.MONTH, 7);
						if (period.equals("sep")) cal.set(Calendar.MONTH, 8);
						if (period.equals("oct")) cal.set(Calendar.MONTH, 9);
						if (period.equals("nov")) cal.set(Calendar.MONTH, 10);
						if (period.equals("dec")) cal.set(Calendar.MONTH, 11);
						calculateDate = MonthCalc.getNiemeMonthDay(cal.getTime(), this.persCalendar, dayNumber, dayType);
					}
				} else {
									
					// dayName is a name of a Week day
					// TODO 
					if (!dayName.equals("day"))
						throw new DateConstraintException(this.planifRule+": dayName != day not yet implemented");
				}
						
			
			// No valid day for rules
			if (calculateDate == null) 
				throw new DateConstraintException(this.planifRule+": Unable to resolv planification rule");
			
			logger.debug("CalculatedDate: "+DateUtil.dateToString(calculateDate));
			
			// before after equal not
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




	}

}
