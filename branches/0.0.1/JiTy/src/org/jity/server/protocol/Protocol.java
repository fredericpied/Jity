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

public abstract class Protocol {
	
	/**
	 * Parse XML request datagram, execute instructions and generate XML stream for response
	 * for the client.
	 * @param xml
	 * @return
	 * @throws ProtocolException
	 */
	public static String executeRequest(String xml) throws ProtocolException {
		
		Request request = parseRequest(xml);
				
		Response response = null;
		
		if (request.getInstructionName().equals("FINDLAUNCHING")) {
			response = FindLaunching.launch(ListUtil.stringToArrayList(request.getInstructionParameters()));
		} else if (request.getInstructionName().equals("SHUTDOWNSERVER")) {
			response = ShutdownServer.launch(ListUtil.stringToArrayList(request.getInstructionParameters()));
		} else {
			throw new ProtocolException("Incorrect instruction name ("+request.getInstructionName()+")");
		}
						
		return response.getXML();
	}
	
	
	/**
	 * Parse XML stream to get OJSResponse object
	 * @param xml
	 * @return OJSResponse
	 * @throws ProtocolException
	 */
	public static Response parseResponse(String xml) throws ProtocolException {
		
		Response response = null;
		
		try {
			response = (Response)XMLUtil.XMLStringToObject(xml);
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
	public static Request parseRequest(String xml) throws ProtocolException {
		
		Request request = null;
		
		try {
			request = (Request)XMLUtil.XMLStringToObject(xml);
		} catch (Exception e){
			throw new ProtocolException(e.getMessage());
		}
		
		return request;
	}
	
	
	
	
}
