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

	/**
	 * Parse an Exception to format Response
	 * @param e
	 */
	public void setException(Exception e) {
		this.setInstructionResult("KO");
		this.setExceptionName(e.getClass().getName());
		this.setExceptionMessage(e.getMessage());
	}

}
