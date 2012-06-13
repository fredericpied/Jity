package org.jity.referential.dateConstraint;

import java.util.Date;

import org.jity.referential.ExecConstraint;

public class DateConstraint extends ExecConstraint {
	
	private PlanifRule planifRule;

	
	/**
	 * Minimal start time in the day
	 */
	private Date minStartTime;
	
}
