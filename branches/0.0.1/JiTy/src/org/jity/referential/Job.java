package org.jity.referential;

import org.jity.referential.dateConstraint.DateConstraint;

public class Job {

	private long id;
	private String name;
	private String description;
	private String hostName;
	private int hostPort;
	private String hostUserName;
	private String commandPath;
	private boolean isActived;
	private DateConstraint dateConstraint;

	public Job() {
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

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getHostPort() {
		return hostPort;
	}

	public void setHostPort(int port) {
		this.hostPort = port;
	}

	public String getHostUserName() {
		return hostUserName;
	}

	public void setHostUserName(String hostUserName) {
		this.hostUserName = hostUserName;
	}

	public String getCommandPath() {
		return commandPath;
	}

	public void setCommandPath(String commandPath) {
		this.commandPath = commandPath;
	}

	public boolean getIsActived() {
		return isActived;
	}

	public void setIsActived(boolean isActived) {
		this.isActived = isActived;
	}
	
	public DateConstraint getDateConstraint() {
		return dateConstraint;
	}

	public void setDateConstraint(DateConstraint dateConstraint) {
		this.dateConstraint = dateConstraint;
	}

}
