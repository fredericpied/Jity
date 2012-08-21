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
package org.jity.common.protocol;

public class JityResponse extends Datagram {

	/**
	 * result of the instruction execution true "OK" or false "KO"
	 */
	private boolean instructionResultOK;

	/**
	 * Exception Name (for use in case of instructionResult="KO")
	 */
	private String exceptionName;

	/**
	 * Exception message (for use in case of instructionResult="KO")
	 */
	private String exceptionMessage;

	/**
	 * Output data for the instruction
	 */
	private String xmlOutputData;

	public boolean isInstructionResultOK() {
		return instructionResultOK;
	}

	public void setInstructionResultOK(boolean instructionResultOK) {
		this.instructionResultOK = instructionResultOK;
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

	public String getXmlOutputData() {
		return xmlOutputData;
	}

	public void setXmlOutputData(String xmlOutputData) {
		this.xmlOutputData = xmlOutputData;
	}
	
	public void addXmlOutputData(String xmlOutputData) {
		this.xmlOutputData = this.xmlOutputData+xmlOutputData;
	}
	
	/**
	 * Parse an Exception to format Response
	 * 
	 * @param e
	 */
	public void setException(Exception e) {
		this.setInstructionResultOK(false);
		this.setExceptionName(e.getClass().getName());
		this.setExceptionMessage(e.getMessage());
	}

}
