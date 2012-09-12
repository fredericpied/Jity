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

import org.jity.agent.instructions.AddTaskInQueue;
import org.jity.agent.instructions.GetTaskStatus;
import org.jity.agent.instructions.ShutdownAgent;
import org.jity.agent.instructions.PingAgent;
import org.jity.common.util.XMLUtil;
import org.jity.server.instructions.admin.ShutdownServerTaskManager;
import org.jity.server.instructions.admin.ShutdownServer;
import org.jity.server.instructions.admin.StartServerTaskManager;
import org.jity.server.instructions.referential.AddCalendar;
import org.jity.server.instructions.referential.DeleteCalendar;
import org.jity.server.instructions.referential.GetCalendar;
import org.jity.server.instructions.referential.UpdateCalendar;
import org.jity.server.instructions.referential.UpdateTaskStatus;

public abstract class Protocol {
	
	public static String executeRequest(String xml) throws ProtocolException {
		return executeRequest(xml, null);
	}
	
	/**
	 * Parse XML request datagram, execute instructions and generate XML stream for response
	 * for the client.
	 * @param xml
	 * @return
	 * @throws ProtocolException
	 */
	public static String executeRequest(String xml, String remoteIP) throws ProtocolException {
		
		JityRequest request = parseRequest(xml);
				
		JityResponse response = null;
		
		// Check if its same Datagram version
		int datagramLocalVersion = new Datagram().getFormatVersion();
		if (datagramLocalVersion != request.getFormatVersion()) {
			response = new JityResponse();
			response.setInstructionResultOK(false);
			response.setExceptionName("ProtocolException");
			response.setExceptionMessage("Datagram version are differents");
			return response.toXML();
		}
		
		// Server requests
		if (request.getInstructionName().equals("SHUTDOWNSERVER")) {
			response = new ShutdownServer().launch(request.getXmlInputData());
		} else if (request.getInstructionName().equals("SHUTDOWNEXECMANAGER")) {
			response = new ShutdownServerTaskManager().launch(request.getXmlInputData());
		} else if (request.getInstructionName().equals("STARTEXECMANAGER")) {
			response = new StartServerTaskManager().launch(request.getXmlInputData());
		} else if (request.getInstructionName().equals("ADDCALENDAR")) {
			response = new AddCalendar().launch(request.getXmlInputData());
		} else if (request.getInstructionName().equals("DELETECALENDAR")) {
			response = new DeleteCalendar().launch(request.getXmlInputData());
		} else if (request.getInstructionName().equals("UPDATECALENDAR")) {
			response = new UpdateCalendar().launch(request.getXmlInputData());
		} else if (request.getInstructionName().equals("GETCALENDAR")) {
			response = new GetCalendar().launch(request.getXmlInputData());
		} else if (request.getInstructionName().equals("UPDTASKSTATUS")) {
			response = new UpdateTaskStatus().launch(request.getXmlInputData());

		// Agent requests
		} else	if (request.getInstructionName().equals("ADDTASKINQUEUE")) {
			response = new AddTaskInQueue().launch(request.getXmlInputData(), remoteIP);
		} else	if (request.getInstructionName().equals("GETTASKSTATUS")) {
			response = new GetTaskStatus().launch(request.getXmlInputData());
		} else if (request.getInstructionName().equals("SHUTDOWNAGENT")) {
			response = new ShutdownAgent().launch(request.getXmlInputData());
		} else if (request.getInstructionName().equals("TESTAGENT")) {
			response = new PingAgent().launch(request.getXmlInputData());
			
		// Unknown request
		} else {
			response = new JityResponse();
			response.setInstructionResultOK(false);
			response.setExceptionName("ProtocolException");
			response.setExceptionMessage("Incorrect instruction name ("+request.getInstructionName()+")");
		}

		return response.toXML();
	}
	
	
	/**
	 * Parse XML stream to get OJSResponse object
	 * @param xml
	 * @return OJSResponse
	 * @throws ProtocolException
	 */
	public static JityResponse parseResponse(String xml) throws ProtocolException {
		
		JityResponse response = null;
		
		try {
			response = (JityResponse)XMLUtil.XMLStringToObject(xml);
		} catch (Exception e){
			throw new ProtocolException(e.getMessage());
		}
		
		return response;
	}

	/**
	 * Parse XML stream to get OJSRequest object
	 * @param xml
	 * @return OJSRequest
	 * @throws ProtocolException
	 */
	public static JityRequest parseRequest(String xml) throws ProtocolException {
		
		JityRequest request = null;
		
		try {
			request = (JityRequest)XMLUtil.XMLStringToObject(xml);
		} catch (Exception e){
			throw new ProtocolException(e.getMessage());
		}
		
		return request;
	}
	
		
}
