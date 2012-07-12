package org.jity.referential;

public class Calendar {
	private long id;
	private String name;
	private String description;
	private int year;
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

	public Calendar(String name, String description, int year, String openDays) {
		this.name = name;
		this.description = description;
		this.year = year;
		this.openDays = openDays;
	}
	
}
