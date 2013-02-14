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
package org.jity.common.referential;

import java.util.Date;

import org.jity.common.referential.dateConstraint.DateConstraint;
import org.jity.common.referential.dateConstraint.DateConstraintException;
import org.jity.common.referential.timeConstraint.TimeConstraint;
import org.jity.common.referential.timeConstraint.TimeConstraintException;

/**
 * A job define a command line to execute on a host depending of somes constraints
 * @author Fred
 *
 */
public class Job {

	private long id;
	private String name;
	private String description;
	private String hostName;
	private int hostPort;
	private String hostUserName;
	private String commandPath;
	private boolean isEnable;
	private DateConstraint dateConstraint;
	private String startTime;

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

	public boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
	
	public DateConstraint getDateConstraint() {
		return dateConstraint;
	}

	public void setDateConstraint(DateConstraint dateConstraint) {
		this.dateConstraint = dateConstraint;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public boolean checkConstraint(Date execDate) throws DateConstraintException, TimeConstraintException {
		
		// If DateConstraint is valid for current execDate
		if (this.getDateConstraint().isAValidDate(execDate)) {
			
			if (TimeConstraint.isAValidWithCurrentTime(this.startTime)) {
				return true;
			}
				
		}
		
		return false;
		
	}
	
}
