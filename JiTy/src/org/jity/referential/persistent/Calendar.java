package org.jity.referential.persistent;

public class Calendar {
	private double id;
	private String name;
	private String description;
	private int year;
	private String openDaysTab;
	
	public Calendar(String name, String description, int year, String openDaysTab) {
		this.description = description;
		this.year = year;
		this.openDaysTab = openDaysTab;
	}
	
}
