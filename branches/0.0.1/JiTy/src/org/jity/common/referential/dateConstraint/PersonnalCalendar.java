package org.jity.common.referential.dateConstraint;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.jity.common.util.DateUtil;

/**
 * own defined calendar used for date calcul
 * WARNING: Calendar goes from 25/12/YYYY-1 to 07/01/YYYY+1
 * Number of days is 380. 
 * @author 09344a
 *
 */
public class PersonnalCalendar {
	private static final Logger logger = Logger.getLogger(PersonnalCalendar.class);
	
	private long id;
	private String name;
	private String description;
	private int year;
	
	/**
	 * This String is 366 characters max length. One characters for one days
	 * + 7 caracters for first week (from 25/12/yy-1 including)
	 * + 7 caracters for end week (to 7/01/yy+1 including)
	 * if characters is 'O': The day is open
	 * if characters is 'C': The day is close 
	 */
	private String days;
	
	public PersonnalCalendar() {
		
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

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}
	
	public PersonnalCalendar(String name, String description, int year, String openDays) {
		this.name = name;
		this.description = description;
		this.year = year;
		this.days = openDays;
	}

	/**
	 * Initialize the open days by setting all days of the year open
	 * @throws PersonnalCalendarException
	 */
	public void initializeWithAllDaysOpen() throws DateConstraintException {
		
		if (this.year == 0)
			throw new DateConstraintException(this.name+": Year not defined");
		
		try {
			String openDays = "";
			
			// + 7 caracters for first week (from 25/12/yy-1 including)
			// + 7 caracters for end week (to 7/01/yy+1 including)
			int maxDaysInCalendar = YearCalc.getMaxNumberofDayInYear(this.year)+14;
			
			for (int i=1;i<=maxDaysInCalendar;i++) {
				openDays = openDays + "O";
			}
			
			this.days = openDays;
			
		} catch (Exception e) {
			throw new DateConstraintException(this.name+": "+e.getMessage());
		}
	}

	/**
	 * Return number of closed days in this calendar
	 * @return int
	 * @throws DateConstraintException
	 */
	public int getNumberOfClosedDays() throws DateConstraintException {
		int number = 0;
		
		for (int day = 1;day <= this.days.length();day ++) {
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
	 * @throws DateConstraintException 
	 */
	public void setOneDayType(int dayNumber, String type) throws DateConstraintException {
		
		if (this.days.length() == 0)
			throw new DateConstraintException(this.name+": Open days not initialized");
		
		if (dayNumber > this.days.length())
			throw new DateConstraintException(this.name+": Specified day number > max number of days define in calendar");
		
		String openDaysBefore = this.days.substring(0, dayNumber-1);
		String openDaysAfter = this.days.substring(dayNumber, this.days.length());
		
		this.days = openDaysBefore+type+openDaysAfter;
	}
	
	/**
	 * Get type (Open or closed) for a specific days
	 * @param dayNumber
	 * @throws PersonnalCalendarException
	 * @return String 
	 */
	public String getOneDayType(int dayNumber) throws DateConstraintException {
				
		if (this.days.length() == 0)
			throw new DateConstraintException(this.name+": Open days not initialized");
		
		if (dayNumber > this.days.length())
			throw new DateConstraintException(this.name+": Specified day number > max number of days in this year");
		
		return this.days.substring(dayNumber-1, dayNumber);
	}
	
	private Date getDateFromDaysTab(int indice) {
		java.util.Calendar cal = new GregorianCalendar();
		cal.clear();
		
		if (indice <= 7) {
			cal.set(java.util.Calendar.YEAR, this.year - 1);
			cal.set(java.util.Calendar.MONTH, 11); // December
			cal.set(java.util.Calendar.DAY_OF_MONTH, 24);
			cal.add(java.util.Calendar.DAY_OF_MONTH, indice);
		} else if (indice > 7 && indice <= YearCalc.getMaxNumberofDayInYear(this.year)) {
			cal.set(java.util.Calendar.YEAR, this.year);
			cal.add(java.util.Calendar.DAY_OF_YEAR, indice - 8);
		} else {
			cal.set(java.util.Calendar.YEAR, this.year + 1);
			cal.set(java.util.Calendar.DAY_OF_YEAR, indice - YearCalc.getMaxNumberofDayInYear(this.year) - 7);
		}
		
		return cal.getTime();
		
	}
	
	/**
	 * List all closed days on the default logger
	 * @throws PersonnalCalendarException
	 */
	public void showClosedDays() throws DateConstraintException {
				
		if (this.days.length() == 0)
			throw new DateConstraintException(this.name+": Open days not initialized");
		
		for (int day = 1;day <= this.days.length();day ++) {
			if (this.getOneDayType(day).equals("C")) {
				logger.info("Closed day: "+DateUtil.dateToString(getDateFromDaysTab(day))+"(indice "+day+")" );
			}
		}
	}
	
	/**
	 * Set closed days for a day of week (1 = monday) to 7 = sunday)
	 * @param dayOfWeek
	 * @throws DateConstraintException
	 */
	public void addClosedDayOfWeek(int dayOfWeek) throws DateConstraintException {
				
		if (this.days.length() == 0)
			throw new DateConstraintException(this.name+": Open days not initialized");
		
		if (dayOfWeek < 1 || dayOfWeek > 7)
			throw new DateConstraintException(this.name+": Day of week must be 1 (monday) to 7 (sunday)");
		
		// default, is 1:Sunday and 7:Monday
		if (dayOfWeek < 7) dayOfWeek++;
		else if (dayOfWeek == 7) dayOfWeek = 1;
		
		java.util.Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(this.year-1, 11, 24); // 24/12/YYYY-1
		
		for (int day = 1;day <= this.days.length();day ++) {
			cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
			
			if (cal.get(java.util.Calendar.DAY_OF_WEEK) == dayOfWeek) {
				this.setOneDayType(day, "C");
			}
		}
				
	}
	
	
	/**
	 * Set closed days for the french holidays
	 * @throws DateConstraintException 
	 */
	public void addFrenchHolydays() throws DateConstraintException {
		
		if (this.days.length() == 0)
			throw new DateConstraintException(this.name+": Open days not initialized");
		
		java.util.Calendar cal = new GregorianCalendar();
		cal.clear();
		
		try {
			
			cal.set(this.year-1, 11, 24); // 24/12/YYYY-1
					
			for (int day = 1;day <= this.days.length();day ++) {
				cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
				if (YearCalc.isFrenchPublicHoliday(cal.getTime())) {
					this.setOneDayType(day, "C");
				}
				
			}
		
		} catch (DateConstraintException e) {
			throw new DateConstraintException(this.name+": "+e.getMessage());
		}
	}
		
	/**
	 * Return true if date is inclued as an open days in this calendar
	 * @param date
	 * @return boolean
	 * @throws DateConstraintException 
	 */
	public boolean isAnOpenDay(Date date) throws DateConstraintException {
		
		if (this.days.length() == 0)
			throw new DateConstraintException(this.name+": Open days not initialized");
			
		// Attention: Pour Calendar, les mois débutent à 0
		java.util.Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.setTime(date);

		// Test if year of date not equal year of this calendar
		if (cal.get(java.util.Calendar.YEAR) != this.year)
			throw new DateConstraintException(this.name+": Calendar year != Date year to test");
					
		int dayNumber = cal.get(java.util.Calendar.DAY_OF_YEAR);
		
		if (this.getOneDayType(dayNumber+7).equals("O")) return true;
		else return false;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			PersonnalCalendar cal1 = new PersonnalCalendar();
			int year = 2012;
			cal1.setYear(year);
			cal1.initializeWithAllDaysOpen();
			
			logger.info("Année: "+year);	
			logger.info("Taille tableau après initialisation: "+cal1.getDays().length());
			logger.info("Nb de jours en "+year+": "+ YearCalc.getMaxNumberofDayInYear(year));
			int tempInt1 = cal1.getDays().length()- 14;
			logger.info("Taille tableau - 14: "+tempInt1);
			
			logger.info("Nb de jours fermés: " + cal1.getNumberOfClosedDays());
			
			cal1.addFrenchHolydays();
			logger.info("addFrenchHolydays");
			cal1.showClosedDays();
			logger.info("Nb de jours fermés: " + cal1.getNumberOfClosedDays());
			
		} catch (DateConstraintException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.exit(0);
		
	}
	
	
}
