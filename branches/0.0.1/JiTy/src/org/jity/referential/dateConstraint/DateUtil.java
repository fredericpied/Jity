package org.jity.referential.dateConstraint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DateUtil {
	
	/**
	 * Default String format for Date
	 */
	private static final String DEFAULT_DATE_FORMAT = "EEE dd/MM/yyyy";
	
	/**
	 * Get String ("EEE dd/MM/yy") with Date
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		return dateFormat.format(date);
	}

	/**
	 * Get Date objet whith a String ("EEE dd/MM/yy")
	 * @param chaine
	 * @return
	 * @throws DateException 
	 */
	public static Date stringToDate(String chaine) throws DateException {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		sdf.setLenient(false);
		
		try {
			return sdf.parse(chaine);
		} catch (ParseException e) {
			throw new DateException(e.getMessage());
		}
	}

	
}
