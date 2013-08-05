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
package org.jity.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DateUtil {
	
	/**
	 * Default String format for Date
	 */
	public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";

	/**
	 * Default String format for Date Time
	 */
	public static final String DEFAULT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

	/**
	 * Default String format for file timestamp
	 */
	public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

	
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
	 * @throws DateConstraintException 
	 */
	public static Date stringToDate(String chaine) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		sdf.setLenient(false);

		return sdf.parse(chaine);
	}
	
}
