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
package org.jity.common.referential.dateConstraint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

public abstract class YearCalc {
	private static final Logger logger = Logger.getLogger(YearCalc.class);
	
	
	/**
	 * Return year number
	 * @param date
	 * @return
	 */
	public static int getYearNumber(Date date) {
		Calendar calToTest = new GregorianCalendar();
		calToTest.setTime(date);
		return calToTest.get(Calendar.YEAR);
	}
	
	/**
	 * Return last day of the year
	 * @param year
	 * @return Date
	 */
	public static Date getLastYearDay(int year) {
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, 11); // For December (January = 0)
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}
	
	/**
	 * Retour max days in the year
	 * @param year
	 * @return
	 */
	public static int getMaxNumberofDayInYear(int year) {
		return getDayNumberInYear(getLastYearDay(year));
	}
	
	/**
	 * Get N ieme day of the year type close or open
	 * @param dateToTest
	 * @param persCal
	 * @param nbDay
	 * @return Date
	 * @throws PersonnalCalendarException 
	 */
	public static Date getNiemeYearDay(Date dateToTest, PersonnalCalendar persCal, int nbDay, String dayType)
			throws PersonnalCalendarException {

		Calendar calToTest = new GregorianCalendar();
		calToTest.clear();

		if (! dayType.equals("close") &&
				! dayType.equals("open") &&
				! dayType.equals("calend"))
			throw new PersonnalCalendarException("dayType must be \"calend\", \"open\" or \"close\"");

		int yearNum = getYearNumber(dateToTest);
		int numberOfDaysInTheYear = getDayNumberInYear(getLastYearDay(yearNum));
		
		if (nbDay > 0) {

			// Initialize a new calendar whith the first day of the year
			calToTest.setTime(getFirstYearDay(yearNum));

			// Last day of the year
			Date lastYearDay = getLastYearDay(yearNum);
			
			if (nbDay > numberOfDaysInTheYear)
				throw new PersonnalCalendarException("nbDay must be < "+numberOfDaysInTheYear);

			int dayCount = 0;

			// While end of month not reached test each day of month
			do {

				if (dayType.equals("close") && ! persCal.isAnOpenDay(calToTest.getTime())) {
					// If it's an close day, count 1 day
					dayCount++;

					// If nbDay reach, return actual cal value
					if (dayCount == nbDay)
						return calToTest.getTime();
				} else if (dayType.equals("open") && persCal.isAnOpenDay(calToTest.getTime())) {
					// If it's an open day, count 1 day
					dayCount++;

					// If nbDay reach, return actual cal value
					if (dayCount == nbDay)
						return calToTest.getTime();
				} else if (dayType.equals("calend")) {
					dayCount++;

					// If nbDay reach, return actual cal value
					if (dayCount == nbDay)
						return calToTest.getTime();
				}
				
				calToTest.add(Calendar.DAY_OF_YEAR, 1);

			} while (lastYearDay.compareTo(calToTest.getTime()) == 1);

			return null;

		} else {

			// Initialize a new calendar this the first day of the year
			calToTest.setTime(getLastYearDay(yearNum));

			// Last day of the week
			Date firstYearDay = getFirstYearDay(yearNum);

			if (nbDay < -numberOfDaysInTheYear)
				throw new PersonnalCalendarException("nbDay must be > -"+numberOfDaysInTheYear);

			int dayCount = 0;

			// While end of month not reached test each day of month
			do {

				if (dayType.equals("close") && ! persCal.isAnOpenDay(calToTest.getTime())) {
					// If it's an close day, count 1 day
					dayCount--;

					// If nbDay reach, return actual cal value
					if (dayCount == nbDay)
						return calToTest.getTime();
				
				} else if (dayType.equals("open") && persCal.isAnOpenDay(calToTest.getTime())) {
					// If it's an open day, count 1 day
					dayCount--;

					// If nbDay reach, return actual cal value
					if (dayCount == nbDay)
						return calToTest.getTime();
				} else if (dayType.equals("calend")) {
					dayCount--;

					// If nbDay reach, return actual cal value
					if (dayCount == nbDay)
						return calToTest.getTime();
				}
				
				calToTest.add(Calendar.DAY_OF_YEAR, -1);

			} while (firstYearDay.compareTo(calToTest.getTime()) == -1);

			return null;
		}
	}
	
	/**
	 * Return first day of the year
	 * @param year
	 * @return Date
	 */
	public static Date getFirstYearDay(int year) {
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, 0); // January
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}
	
	/**
	 * Return Easter Eggs sunday date for the year
	 * @param year
	 * @return
	 * @throws DateException 
	 */
	public static Date getEasterEggsSyndayDate(int year) throws DateConstraintException {
		//http://quincy.inria.fr/data/courses/ipa-java-99/td1.html
		Date easterEggsDate = null;
		int dayNumber;
		String stringDay, stringMonth, stringYear;
		
		int g = (year % 19) + 1;
		int c = year / 100 + 1;
		int x = 3 * c / 4 - 12;
		int z = (8 * c + 5) / 25 - 5;
		int d = 5 * year / 4 - x - 10;
		int e = (11 * g + 20 + z - x) % 30;
		if ( e == 25 && g > 11 || e == 24 )
			++e;
		int n = 44 - e;
		if (n < 21)
			n = n + 30;
		int j = n + 7 - ((d + n) % 7);
		
		
		if (j > 31) {
			dayNumber = j - 31;
			stringMonth = "04"; // April
		} else {
			dayNumber = j;
			stringMonth = "03"; // March
		}

		stringDay = Integer.toString(dayNumber);
		if (stringDay.length() == 1) {
			stringDay = "0"+stringDay;
		}
		
		stringYear = Integer.toString(year);
		
		try {
			easterEggsDate = new SimpleDateFormat("dd/MM/yyyy").parse(stringDay+"/"+
					stringMonth+"/"+stringYear);
		} catch (ParseException ex) {
			throw new DateConstraintException(ex.getMessage());
		}
		
		return easterEggsDate;
	}
	
	/**
	 * Get true if date is a french public holiday
	 * @param date
	 * @return boolean
	 * @throws DateException 
	 */
	public static boolean isFrenchPublicHoliday(Date date) throws DateConstraintException  {
		
		Calendar calToTest = new GregorianCalendar();
		calToTest.setTime(date);
		int numeroAnnee = getYearNumber(date);
	
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		
		// For Calendar, month of year start à 0 (January)
		
		// It's 01/01 (Jour de l'an) ?
		Calendar calDate1Janvier = new GregorianCalendar();
		calDate1Janvier.clear(); // suppress hours, minutes, seconds ...
		calDate1Janvier.set(numeroAnnee, 0, 1);
		logger.trace("Jour de l'an: "+dateFormat.format(calDate1Janvier.getTime()));
		if (calToTest.equals(calDate1Janvier)) return true;

		// It's 01/05 (Fète du travail) ?		
		Calendar calDate1Mai = new GregorianCalendar();
		calDate1Mai.clear();
		calDate1Mai.set(numeroAnnee, 4, 1, 0, 0, 0);
		logger.trace("Fete du travail:"+dateFormat.format(calDate1Mai.getTime()));
		if (calToTest.equals(calDate1Mai)) return true;
		
		// It's 01/05 (Victoire 1945) ?	
		Calendar calDate8Mai = new GregorianCalendar();
		calDate8Mai.clear();
		calDate8Mai.set(numeroAnnee, 4, 8, 0, 0, 0);
		logger.trace("Victoire 1945:"+dateFormat.format(calDate8Mai.getTime()));
		if (calToTest.equals(calDate8Mai)) return true;
		
		// It's 14/07 (Fête Nationnale) ?
		Calendar calDate14Juillet = new GregorianCalendar();
		calDate14Juillet.clear();
		calDate14Juillet.set(numeroAnnee, 6, 14);
		logger.trace("14 juillet: "+dateFormat.format(calDate14Juillet.getTime()));
		if (calToTest.equals(calDate14Juillet)) return true;
		
		// It's 15/08 (Assomption) ?
		Calendar calDate15Aout = new GregorianCalendar();
		calDate15Aout.clear();
		calDate15Aout.set(numeroAnnee, 7, 15);
		logger.trace("15 aout: "+dateFormat.format(calDate15Aout.getTime()));
		if (calToTest.equals(calDate15Aout)) return true;
		
		// It's 01/11 (Toussaint) ?
		Calendar calDate1Novembre = new GregorianCalendar();
		calDate1Novembre.clear();
		calDate1Novembre.set(numeroAnnee, 10, 1);
		logger.trace("Toussaint: "+dateFormat.format(calDate1Novembre.getTime()));
		if (calToTest.equals(calDate1Novembre)) return true;
		
		// It's 11/11 (Armistice 1918) ?
		Calendar calDate11Novembre = new GregorianCalendar();
		calDate11Novembre.clear();
		calDate11Novembre.set(numeroAnnee, 10, 11);
		logger.trace("Armistice: "+dateFormat.format(calDate11Novembre.getTime()));
		if (calToTest.equals(calDate11Novembre)) return true;
		
		// It's 25/12 (Noel) ?
		Calendar calDate25Decembre = new GregorianCalendar();
		calDate25Decembre.clear();
		calDate25Decembre.set(numeroAnnee, 11, 25);
		logger.trace("Noel: "+dateFormat.format(calDate25Decembre.getTime()));
		if (calToTest.equals(calDate25Decembre)) return true;
	
		// It's Lundi de paques ?
		// Lundi de Paques = Dimanche de paques + 1 jour
		Calendar calDateLundiPaques = new GregorianCalendar();
		calDateLundiPaques.setTime(getEasterEggsSyndayDate(numeroAnnee));
		calDateLundiPaques.add(Calendar.DAY_OF_MONTH, 1);
		logger.trace("Lundi de Paques: "+dateFormat.format(calDateLundiPaques.getTime()));
		if (calToTest.equals(calDateLundiPaques)) return true;

		// It's Ascension ?
		// Jeudi de l'Ascension = Dimanche de paques + 39 jours
		Calendar calDateJeudiAscension = new GregorianCalendar();
		calDateJeudiAscension.setTime(getEasterEggsSyndayDate(numeroAnnee));
		calDateJeudiAscension.add(Calendar.DAY_OF_MONTH, 39);
		logger.trace("Jeudi de l'Ascension: "+dateFormat.format(calDateJeudiAscension.getTime()));
		if (calToTest.equals(calDateJeudiAscension)) return true;
		
//		// It's Lundi de Pentecote
//		// Lundi de Pentecote = Dimanche de paques + 50 jours
//		Calendar calDateLundiPentecote = new GregorianCalendar();
//		calDateLundiPentecote.setTime(getEasterEggsSyndayDate(numeroAnnee));
//		calDateLundiPentecote.add(Calendar.DAY_OF_MONTH, 50);
//		logger.trace("Lundi de Pentecote: "+dateFormat.format(calDateLundiPentecote.getTime()));
//		if (calToTest.equals(calDateLundiPentecote)) return true;
				
		return false;
	}
	
	
	/**
	 * Return true if date1 and date2 is in the same year
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameYear(Date date1, Date date2) {
		Calendar cal1 = new GregorianCalendar();
		cal1.setTime(date1);
		
		Calendar cal2 = new GregorianCalendar();
		cal2.setTime(date2);
	
		return cal2.get(Calendar.YEAR) == cal1.get(Calendar.YEAR);
	}
		
	/**
	 * get Day Number in year
	 * @param date
	 * @return
	 */
	public static int getDayNumberInYear(Date date) {
		Calendar calToTest = new GregorianCalendar();
		calToTest.setTime(date);
		return calToTest.get(Calendar.DAY_OF_YEAR);
	}
		
}
