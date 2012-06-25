package org.jity.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.jity.common.XMLUtil;
import org.jity.server.protocol.Request;
import org.jity.server.protocol.Response;

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

	public Response sendRequest(Request request) {
		String xmlResult = null;
		this.sout.println(request.getXML());
		this.sout.flush();
		Response response = null;
		
		try {
			xmlResult = this.sin.readLine();

			response = (Response) XMLUtil.XMLStringToObject(xmlResult);

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
