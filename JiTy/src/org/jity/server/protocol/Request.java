package org.jity.server.protocol;

public class Request extends Datagram {
	
	/**
	 * Instruction to execute by the server side
	 */
	public String instructionName;
	
	/**
	 * Parameters for the instruction (values separates by coma)
	 */
	public String instructionParameters;
	
	
	
	public String getInstructionName() {
		return instructionName;
	}
	
	public String getInstructionParameters() {
		return instructionParameters;
	}

	public void setInstructionName(String instructionName) {
		this.instructionName = instructionName;
	}

	public void setInstructionParameters(String instructionParameters) {
		this.instructionParameters = instructionParameters;
	}
	
	
}
