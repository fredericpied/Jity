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

import org.jity.common.ListUtil;
import org.jity.common.XMLUtil;
import org.jity.server.instructions.FindLaunching;
import org.jity.server.instructions.admin.ShutdownServer;
import org.jity.server.instructions.referential.AddCalendar;
import org.jity.server.instructions.referential.DeleteCalendar;
import org.jity.server.instructions.referential.GetCalendar;
import org.jity.server.instructions.referential.UpdateCalendar;

public abstract class Protocol {
	
	/**
	 * Parse XML request datagram, execute instructions and generate XML stream for response
	 * for the client.
	 * @param xml
	 * @return
	 * @throws ProtocolException
	 */
	public static String executeRequest(String xml) throws ProtocolException {
		
		JityRequest request = parseRequest(xml);
				
		JityResponse response = null;
		
		if (request.getInstructionName().equals("FINDLAUNCHING")) {
			response = new FindLaunching().launch(ListUtil.stringToArrayList(request.getInstructionParameters()));
		} else if (request.getInstructionName().equals("SHUTDOWNSERVER")) {
			response = new ShutdownServer().launch(ListUtil.stringToArrayList(request.getInstructionParameters()));
		} else if (request.getInstructionName().equals("ADDCALENDAR")) {
			response = new AddCalendar().launch(ListUtil.stringToArrayList(request.getInstructionParameters()));
		} else if (request.getInstructionName().equals("DELETECALENDAR")) {
			response = new DeleteCalendar().launch(ListUtil.stringToArrayList(request.getInstructionParameters()));
		} else if (request.getInstructionName().equals("UPDATECALENDAR")) {
			response = new UpdateCalendar().launch(ListUtil.stringToArrayList(request.getInstructionParameters()));
		} else if (request.getInstructionName().equals("GETCALENDAR")) {
			response = new GetCalendar().launch(ListUtil.stringToArrayList(request.getInstructionParameters()));
		} else {
			//throw new ProtocolException("Incorrect instruction name ("+request.getInstructionName()+")");
			response = new JityResponse();
			response.setInstructionResult("KO");
			response.setExceptionName("ProtocolException");
			response.setExceptionMessage("Incorrect instruction name ("+request.getInstructionName()+")");
		}

		return response.getXML();
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
