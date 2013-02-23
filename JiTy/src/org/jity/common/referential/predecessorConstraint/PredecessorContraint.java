package org.jity.common.referential.predecessorConstraint;

import org.jity.common.referential.Job;

public class PredecessorContraint {
	private Job predecessorJob;
	private int constraintType;
	
	public static final int PREDECESSOR_OK = 1;
	public static final int PREDECESSOR_KO = 2;
}
