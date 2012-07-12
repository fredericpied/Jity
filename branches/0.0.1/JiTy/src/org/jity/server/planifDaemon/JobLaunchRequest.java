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
package org.jity.server.planifDaemon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.jity.common.XMLUtil;
import org.jity.protocol.JityRequest;
import org.jity.protocol.JityResponse;
import org.jity.referential.Job;

public class JobLaunchRequest {
	private static final Logger logger = Logger.getLogger(JobLaunchRequest.class);

	// Socket used to dialog with the agent
	private Socket sock = null;
	private BufferedReader sin;
	private PrintWriter sout;

	public void openAgentConnection(String host, int port)
			throws UnknownHostException, IOException {
		// open socket
		sock = new Socket(host, port);
		// Init reader and writer
		sin = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		sout = new PrintWriter(sock.getOutputStream());
	}

	/**
	 * Close the connection with the server
	 * @throws IOException
	 */
	public void closeAgentConnection() throws IOException {
		if (sock != null)
			sock.close();
	}


	/**
	 * Send a launch request to the agent
	 * @param request
	 * @return JiTyResponse
	 */
	public JityResponse sendLaunchRequest(Job job) {
		
		// Serialize job to XML
		String xmlJob = XMLUtil.objectToXMLString(job);
		
		// Construct Request
		JityRequest request = new JityRequest();
		request.setInstructionName("LAUNCHJOB");
		request.setXmlInputData(xmlJob);

		JityResponse response = null;
		
		try {
		
			logger.debug("Sending launch request for job "+job.getName()+" on "+job.getHostName());
	
			// Send request
			this.sout.println(request.toXML());
			this.sout.flush();
			
			// Reading response
			response = (JityResponse) XMLUtil.XMLStringToObject(this.sin.readLine());

			logger.debug("Return of the launch request for job "+job.getName()+" on "+job.getHostName());
			
		} catch (IOException e) {
			response = new JityResponse();
			response.setException(e);
		}
		
		return response;
	}
	

}
