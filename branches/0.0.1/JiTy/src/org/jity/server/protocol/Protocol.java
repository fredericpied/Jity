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
