package org.jity.referential;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.jity.common.DateUtil;
import org.jity.referential.dateCalc.DateException;
import org.jity.referential.dateCalc.YearCalc;
import org.jity.tests.TestCalendar;

public class Calendar {
	private static final Logger logger = Logger.getLogger(Calendar.class);
	
	private long id;
	private String name;
	private String description;
	private int year;
	
	/**
	 * This String is 366 characters max length. One characters for one days
	 * if characters is 'O': The day is open
	 * if characters is 'C': The day is close 
	 */
	private String openDays;
	
	public Calendar() {
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getOpenDays() {
		return openDays;
	}

	public void setOpenDays(String openDays) {
		this.openDays = openDays;
	}
	
	/**
	 * Return number of days in the year of the calendar
	 * @return int
	 */
	public int getNumberOfDaysInTheYear() {
		java.util.Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(this.year, 11, 31); // Dec 31
		return cal.get(java.util.Calendar.DAY_OF_YEAR);
	}
	
	public Calendar(String name, String description, int year, String openDays) {
		this.name = name;
		this.description = description;
		this.year = year;
		this.openDays = openDays;
	}

	/**
	 * Initialize the open days by setting all days of the year open
	 * @throws CalendarException
	 */
	public void initializeWithAllDaysOpen() throws CalendarException {
		
		if (this.year == 0)
			throw new CalendarException(this.name+": Year not defined");
		
		try {
			String openDays = "";
			
			for (int i=1;i<=this.getNumberOfDaysInTheYear();i++) {
				openDays = openDays + "O";
			}
			
			this.openDays = openDays;
			
		} catch (Exception e) {
			throw new CalendarException(this.name+": "+e.getMessage());
		}
	}

	/**
	 * Return number of closed days in this calendar
	 * @return int
	 * @throws CalendarException
	 */
	public int getNumberOfClosedDays() throws CalendarException {
		int number = 0;
		
		for (int day = 1;day <= this.getNumberOfDaysInTheYear();day ++) {
			if (this.getOneDayType(day).equals("C")) {
				number++;
			}
		}
		
		return number;
	}
	
	/**
	 * Set type (Open or closed) for a specific days
	 * @param dayNumber
	 * @param type ("O" or "C")
	 * @throws CalendarException 
	 */
	public void setOneDayType(int dayNumber, String type) throws CalendarException {
				
		if (this.openDays.length() == 0)
			throw new CalendarException(this.name+": Open days not initialized");
		
		if (dayNumber > this.openDays.length())
			throw new CalendarException(this.name+": Specified day number > max number of days in this year");
		
		String openDaysBefore = this.openDays.substring(0, dayNumber-1);
		String openDaysAfter = this.openDays.substring(dayNumber, this.openDays.length());
		
		this.openDays = openDaysBefore+type+openDaysAfter;
	}
	
	/**
	 * Get type (Open or closed) for a specific days
	 * @param dayNumber
	 * @throws CalendarException
	 * @return String 
	 */
	public String getOneDayType(int dayNumber) throws CalendarException {
				
		if (this.openDays.length() == 0)
			throw new CalendarException(this.name+": Open days not initialized");
		
		if (dayNumber > this.openDays.length())
			throw new CalendarException(this.name+": Specified day number > max number of days in this year");
		
		return this.openDays.substring(dayNumber-1, dayNumber);
	}
	
	/**
	 * List all closed days on the default logger
	 * @throws CalendarException
	 */
	public void showClosedDays() throws CalendarException {
				
		if (this.openDays.length() == 0)
			throw new CalendarException(this.name+": Open days not initialized");
		
		for (int day = 1;day <= this.getNumberOfDaysInTheYear();day ++) {
			if (this.getOneDayType(day).equals("C")) {
				java.util.Calendar cal = new GregorianCalendar();
				cal.clear();
				cal.set(java.util.Calendar.YEAR, this.year);
				cal.set(java.util.Calendar.DAY_OF_YEAR, day);
				logger.info(DateUtil.dateToString(cal.getTime()));
			}
		}
	}
	
	/**
	 * Set closed days for a day of week (1 = monday) to 7 = sunday)
	 * @param dayOfWeek
	 * @throws CalendarException
	 */
	public void addClosedDayOfWeek(int dayOfWeek) throws CalendarException {
				
		if (this.openDays.length() == 0)
			throw new CalendarException(this.name+": Open days not initialized");
		
		if (dayOfWeek < 1 || dayOfWeek > 7)
			throw new CalendarException(this.name+": Day of week must be 1 (monday) to 7 (sunday)");
		
		// default, is 1:Sunday and 7:Monday
		if (dayOfWeek < 7) dayOfWeek++;
		else if (dayOfWeek == 7) dayOfWeek = 1;
		
		java.util.Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(java.util.Calendar.YEAR, this.year);
		
		for (int day = 1;day <= this.getNumberOfDaysInTheYear();day ++) {
			cal.set(java.util.Calendar.DAY_OF_YEAR, day);
			if (cal.get(java.util.Calendar.DAY_OF_WEEK) == dayOfWeek) 
				this.setOneDayType(day, "C");
		}
	}
	
	
	/**
	 * Set closed days for the french holidays
	 * @throws CalendarException 
	 */
	public void addFrenchHolydays() throws CalendarException {
		
		if (this.openDays.length() == 0)
			throw new CalendarException(this.name+": Open days not initialized");
		
		java.util.Calendar cal = new GregorianCalendar();
		cal.clear();
		
		try {
			
			cal.set(this.year-1, 11, 31); // Dec 31
					
			for (int day = 1;day <= this.getNumberOfDaysInTheYear();day ++) {
				cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
				
				if (YearCalc.isFrenchPublicHoliday(cal.getTime())) {
					this.setOneDayType(day, "C");
				}
				
			}
		
		} catch (DateException e) {
			throw new CalendarException(this.name+": "+e.getMessage());
		}
	}
		
	/**
	 * Return true if date is inclued as an open days in this calendar
	 * @param date
	 * @return boolean
	 * @throws CalendarException 
	 */
	public boolean isAnOpenDay(Date date) throws CalendarException {
		
		if (this.openDays.length() == 0)
			throw new CalendarException(this.name+": Open days not initialized");
			
		// Attention: Pour Calendar, les mois débutent à 0
		java.util.Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.setTime(date);

		// Test if year of date not equal year of this calendar
		if (cal.get(java.util.Calendar.YEAR) != this.year)
			throw new CalendarException(this.name+": Calendar year != Date year to test");
					
		int dayNumber = cal.get(java.util.Calendar.DAY_OF_YEAR);
		
		if (this.getOneDayType(dayNumber).equals("O")) return true;
		else return false;
	}
	
	
	
}
