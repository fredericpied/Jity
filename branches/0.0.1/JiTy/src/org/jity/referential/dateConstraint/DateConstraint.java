package org.jity.referential.dateConstraint;

import java.util.Date;

import org.jity.referential.ExecConstraint;

/**
 * Represents an planification constraint. 
 * The constraint est validated when the planification rule is true and the minimal start time
 * in current valid day is over
 * @author 09344a
 *
 */
public class DateConstraint extends ExecConstraint {
	
	/**
	 * Planification rules
	 */
	private PlanifRule planifRule;
	
	/**
	 * Minimal start time in the day ("HH:MM")
	 */
	private String minStartTime;
	
}
