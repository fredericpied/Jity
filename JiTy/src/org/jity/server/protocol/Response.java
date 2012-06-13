package org.jity.server.protocol;

public class Response extends Datagram {

	/**
	 * result of the instruction execution "OK" or "KO"
	 */
	public String instructionResult;

	/**
	 * Exception Name (for use in case of instructionResult="KO")
	 */
	public String exceptionName;

	/**
	 * Exception message
	 */
	public String exceptionMessage;

	public String getInstructionResult() {
		return instructionResult;
	}

	public void setInstructionResult(String instructionResult) {
		this.instructionResult = instructionResult;
	}

	public String getExceptionName() {
		return exceptionName;
	}

	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public void parseException(Exception e) {
		this.setInstructionResult("KO");
		this.setExceptionName(e.getClass().getName());
		this.setExceptionMessage(e.getMessage());
	}

}
