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
package org.jity.common;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Somes methods for user with ArrayList and "String separed by comma" lists
 * @author 09344A
 *
 */
public abstract class ListUtil {
	/**
	 * Convert a String with values separates by comma to ArrayList<String>
	 * @param string
	 * return ArrayList<String>
	 */
	public static ArrayList<String> stringToArrayList(String string) {
		ArrayList<String> arrayList = new ArrayList<String>();
		if (string != null && string.length() > 0) {
			String[] tabValeur = string.split(",");
			for (int i=0;i<tabValeur.length;i++) {
				arrayList.add(tabValeur[i].trim());
			}
		}
		return arrayList;
	}
	
	
	/**
	 * Convert an ArrayList<String> to String with values separates by comma
	 * @param arrayList
	 * @return String
	 */
	public static String ArrayListToString(ArrayList<String> arrayList) {
		String string = "";
		
		Iterator<String> iterList = arrayList.iterator();
		while (iterList.hasNext()) {
			string = string + iterList.next() + ",";
		}
		
		// Last comma suppression
		if (string.length() > 1) {
			return string.substring(0, string.length()-1);
		} else {
			return "";
		}
		
	}
}
