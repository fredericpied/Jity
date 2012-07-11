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
package org.jity.server.planifEngine;

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

public class PlanifEngine {
	private static final Logger logger = Logger
			.getLogger(PlanifEngine.class);

	// Socket used to dialog with the server
	private Socket sock = null;
	private BufferedReader sin;
	private PrintWriter sout;

	private static PlanifEngine instance = null;

	/**
	 * Return the current instance of Client and create one if it's the thirst call
	 * @return Client
	 * @throws PlanifEngineException
	 */
	public static PlanifEngine getInstance() throws PlanifEngineException {
		if (instance == null)
			instance = new PlanifEngine();
		return instance;
	}

	private void openConnection(String host, int port)
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
	public void closeConnection() throws IOException {
		if (sock != null)
			sock.close();
	}

	/**
	 * Send a request to the Server
	 * @param request
	 * @return JiTyResponse
	 */
	public JityResponse sendRequest(JityRequest request) {
		String xmlResult = null;
		this.sout.println(request.toXML());
		this.sout.flush();
		JityResponse response = null;
		
		try {
			xmlResult = this.sin.readLine();

			response = (JityResponse) XMLUtil.XMLStringToObject(xmlResult);

		} catch (IOException e) {
			response = new JityResponse();
			response.setException(e);
		}
		
		return response;
	}

	
	private PlanifEngine() throws PlanifEngineException {
				
		
		try {
			this.openConnection("localhost",
					2611);

		} catch (UnknownHostException e) {
			throw new PlanifEngineException(e.getMessage());
		} catch (IOException e) {
			throw new PlanifEngineException(e.getMessage());
		}

	}

}
