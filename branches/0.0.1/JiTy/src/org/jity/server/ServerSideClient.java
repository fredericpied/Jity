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
package org.jity.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.jity.common.XMLUtil;
import org.jity.server.protocol.JityRequest;
import org.jity.server.protocol.JityResponse;

public class ServerSideClient {
	private static final Logger logger = Logger
			.getLogger(ServerSideClient.class);

	// Socket used to dialog with the server
	Socket sock = null;
	BufferedReader sin;
	PrintWriter sout;

	ServerSideClient instance = null;

	public ServerSideClient getInstance() throws ServerSideClientException {
		if (instance == null)
			instance = new ServerSideClient();
		return instance;
	}

	private void openConnexion(String host, int port)
			throws UnknownHostException, IOException {
		// open socket
		sock = new Socket(host, port);
		// Init reader and writer
		sin = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		sout = new PrintWriter(sock.getOutputStream());
	}

	public void closeConnexion() throws IOException {
		if (this.sock != null)
			this.sock.close();
	}

	public JityResponse sendRequest(JityRequest request) {
		String xmlResult = null;
		this.sout.println(request.toXML());
		this.sout.flush();
		JityResponse response = null;
		
		try {
			xmlResult = this.sin.readLine();

			response = (JityResponse) XMLUtil.XMLStringToObject(xmlResult);

		} catch (IOException e) {
			if (response != null) response.setException(e);
		}
		
		return response;
	}

	public ServerSideClient() throws ServerSideClientException {

		// Load config file
		try {
			ServerConfig serverConfig = ServerConfig.getInstance();
			logger.info("Reading configuration file.");
			serverConfig.initialize();
			logger.info("Configuration File successfully loaded.");
		} catch (IOException e) {
			throw new ServerSideClientException(
					"Failed to read configuration file (" + e.getMessage()
							+ ").");
		}

		try {
			this.openConnexion("localhost", ServerConfig.getInstance()
					.getSERVER_PORT());

		} catch (UnknownHostException e) {
			throw new ServerSideClientException(e.getMessage());
		} catch (IOException e) {
			throw new ServerSideClientException(e.getMessage());
		}

	}

}
