package org.jity.common.referential.timeConstraint;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeConstraint {
		
	private static Calendar timeToCal(String time) throws TimeConstraintException {
		String[] tabTime = time.split(":");

		if (tabTime.length != 2)
			throw new TimeConstraintException("Time constraint syntax error: "+time+" (not HH:MM format)");
		
		int hours = Integer.parseInt(tabTime[0]);
		
		if (hours > 23 ||  hours < 0)
			throw new TimeConstraintException("Time constraint syntax error: "+time+" (hour must be >0 and <23)");
			
		int minutes = Integer.parseInt(tabTime[1]);

		if (minutes > 59 ||  minutes < 0)
			throw new TimeConstraintException("Time constraint syntax error: "+time+" (minutes must be >0 and <59)");
		
		Calendar calTest = new GregorianCalendar();
		calTest.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tabTime[0]));
		calTest.set(Calendar.MINUTE, Integer.parseInt(tabTime[1]));
		calTest.set(Calendar.SECOND, 0);
		calTest.set(Calendar.MILLISECOND, 0);
				
		return calTest;
	}
	
	public static boolean isAValidTime(String validTime, String timeToTest) throws TimeConstraintException {
		
		Calendar validCal = timeToCal(validTime);
		Calendar calToTest = timeToCal(timeToTest);
		
		if (validCal.compareTo(calToTest) >= 0) return true;
			else return false;
	}
	
	public static boolean isAValidWithCurrentTime(String validTime) throws TimeConstraintException {
		
		if (validTime == null) return true;
		
		Calendar validCal = timeToCal(validTime);
		
		Calendar calToTest = new GregorianCalendar();
		calToTest.set(Calendar.SECOND, 0);
		calToTest.set(Calendar.MILLISECOND, 0);
		
		if (validCal.compareTo(calToTest) >= 0) return true;
			else return false;
		
	}

	
}
