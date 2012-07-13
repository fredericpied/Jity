package org.jity.referential;

import java.util.Date;
import java.util.GregorianCalendar;

import org.jity.referential.dateCalc.DateException;
import org.jity.referential.dateCalc.MonthCalc;
import org.jity.referential.dateCalc.WeekCalc;

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
	
	private Calendar calendar;
	
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

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
	/**
	 * Return true if the constrainte is valid for an exec Date
	 * @param execDate
	 * @return
	 * @throws DateConstraintException
	 * @throws DateException 
	 */
	public boolean isAValidDate(Date execDate) throws DateConstraintException, DateException {

		if (this.calendar == null) 
			throw new DateConstraintException("Calendar is not defined for the Date Constraint");
		
		try {
			// If it's a closed day in the calendar
			if (!this.calendar.isAnOpenDay(execDate)) {
				return false;
			} else {
				// It's an open Days in the calendar
				
				// If no planif rules set, return true
				if (this.planifRule == null || this.planifRule.length() == 0) {
					return true; 
				} else {
					
					// Extract sentence
					String[] planifRuleSplit = this.planifRule.split("-");
					String operator = planifRuleSplit[0];
					
					String stringDayNumber = planifRuleSplit[1];

					String dayType = planifRuleSplit[2];

					String dayName = planifRuleSplit[3];
					
					String period = planifRuleSplit[4];

					// DAY NAME
					if (! dayName.equals("day")) {
						int execDateDayNumberInWeek = WeekCalc.getDayNumberInWeek(execDate);
						int ruleDayNumberInWeek = WeekCalc.getDayNumberInWeekByName(dayName);
					
						// If day name are different, return false now
						if (execDateDayNumberInWeek != ruleDayNumberInWeek) return false;
						else dayName="TRUE"; // Same dayname as execDate
												
					} else { // if (! dayName.equals("day")) {
						dayName="TRUE";
					}

					// PERIOD
					if (! period.equals("week") && ! period.equals("month") && ! period.equals("year")) {
						int execDateMonthNumber = MonthCalc.getMonthNumberByDate(execDate);
						int ruleMonthNumber = MonthCalc.getMonthNumberByName(period);
					
						// If month name are different, return false now
						if (execDateMonthNumber != ruleMonthNumber) return false;
						else period="TRUE"; // Same month as execDate
												
					} else { // if (! period.equals("week") && ! period.equals("month") && ! period.equals("year")) {
						
					}
				
					
					// DAY NUMBER
					int dayNumberInPeriod = 0;
					
					if (stringDayNumber.equals("first")) {
						dayNumberInPeriod = 1;
					} else if (stringDayNumber.equals("last")) {

						if (period.equals("week")) {
							if (dayType.equals("open")) {
								
								java.util.Calendar calToTest = new GregorianCalendar();
								calToTest.clear();
								calToTest.setTime(execDate);
								
								for (int i=7;i>0;i--) {
								
									Date dateLastDayOfWeek = WeekCalc.getLastDayOfTheWeek(calToTest.getTime());
									if (this.calendar.isAnOpenDay(dateLastDayOfWeek)) {
										break;
									} else {
										
									}
								}
								
								
								
							} else if (dayType.equals("close")) {
								
							} else if (dayType.equals("calend")) {
								dayNumberInPeriod = 7;
							}
							
							
						} else if (period.equals("month")) {
					
							
						} else if (period.equals("year")) {
						
						}
						
						
					}	
					
					
					
				}
				
			}
			
			
		
		
		} catch (CalendarException e) {
			throw new DateConstraintException(e.getMessage());
		}
		
		return false;
	}

}
