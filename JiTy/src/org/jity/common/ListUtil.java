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
