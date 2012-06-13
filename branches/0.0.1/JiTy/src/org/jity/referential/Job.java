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
package org.jity.referential;

import java.util.ArrayList;

/**
 * A job is an execution unit
 * @author 09344a
 *
 */
public class Job {

	private double id;
	
	private String name;

	private String description;

	/**
	 * Command to execute on execMachine
	 */
	private String command;
	
	/**
	 * Execution status of the job: "WAITING", "NOT_PLANED", "EXECUTING", "FAILED", "SUCCESS"
	 */
	private int status;
	private static final int NOT_PLANNED_STATUS = 0;
	private static final int WAITING_STATUS = 1;
	private static final int EXECUTING_STATUS = 2;
	private static final int FAILED_STATUS = 3;
	private static final int SUCCESSED_STATUS = 4;
	
	
	/**
	 * If false, the execution of this job is not permitted
	 */
	private boolean isActived = true;
	
	/**
	 * Execution constraints of this job: All must be validate for the job to start.
	 */
	private ArrayList<ExecConstraint> execConstraints = new ArrayList<ExecConstraint>();

	private ExecMachine execMachine;
	
	/**
	 * Local user on the execMachine use to exécute job command
	 */
	private String execUser;
}
