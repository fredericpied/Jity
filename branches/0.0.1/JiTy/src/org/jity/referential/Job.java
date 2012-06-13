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
	 * Execution status of the job: "WAITING", "EXECUTING", "FAILED", "SUCCESS"
	 */
	private int status;
	private static final int WAITING_STATUS=1;
	private static final int EXECUTING_STATUS=2;
	private static final int FAILED_STATUS=3;
	private static final int SUCCESSED_STATUS=4;
	
	/**
	 * If false, the execution of this job is not permitted
	 */
	private boolean isActived = true;
	
	/**
	 * Execution constraints of this job: All must be validate for the job to start.
	 */
	private ArrayList<ExecConstraint> execConstraints = new ArrayList<ExecConstraint>();
	
	private JobContainer jobContainer;
	
	private ExecMachine execMachine;
	
	private ExecUser execUser;
}
