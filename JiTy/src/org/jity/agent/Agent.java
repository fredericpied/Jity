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
package org.jity.agent;

import org.apache.log4j.Logger;
import org.jity.common.protocol.RequestReceiver;

import java.io.*;
import java.net.*;

/**
 * The JiTy Agent run on host to execute task
 * @author Fred
 *
 */
public class Agent {
	private static final Logger logger = Logger.getLogger(Agent.class);

	private static Agent instance = null;

	private ServerSocket listenSocket;
	
	private boolean isRunning = false;
	
	private boolean shutdownning = false;

	/**
	 * Return the current instance of Agent (if none, create one)
	 * @return Agent
	 */
	public static Agent getInstance() {
		if (instance == null) {
			instance = new Agent();
		}
		return instance;
	}

	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * Start the agent
	 * 
	 */
	public void startAgent() {
		Socket socket = null;
		
		shutdownning = false;
		
		logger.info("JiTy Agent starting process.");

		// Loading config File
		try {
			this.loadConfigFile();
		} catch (AgentException e1) {
			logger.fatal(e1.getMessage());
			System.exit(1);
		}

		this.showConfig();
		
		try {
			String localHostname = java.net.InetAddress.getLocalHost().getHostName();
			
			logger.info("Starting agent on "+localHostname+" ("+this.getOSName()+")...");
			
		} catch (UnknownHostException e1) {
			logger.warn(e1.getMessage());
			logger.info("Starting agent...");
		}


		int agentPort = AgentConfig.getInstance().getAGENT_PORT();
		try {
			listenSocket = new ServerSocket(agentPort);
			logger.info("Agent running on port " + agentPort);
			logger.info("JiTy Agent successfully started.");
		} catch (IOException e) {
			logger.fatal(e.getMessage());
			System.exit(1);
		}

		try {

			isRunning = true;
			
			while (true) {
				socket = listenSocket.accept();
				try {
					String serverHostname = socket.getInetAddress().getHostName();
					
					logger.info("New connection from "+serverHostname+" ("
							+ socket.getInetAddress() + ").");

					//new ServeOneServerRequest(socket);
					new RequestReceiver(socket, AgentConfig.getInstance().getHOSTNAME_LIST());
				} catch (IOException e) {
					socket.close();
				}
			}

		} catch (IOException e) {
			if (!shutdownning) logger.warn("Problem during client connection.");
			logger.debug(e.getMessage());
		} finally {
			isRunning = false;
			try {
				if (listenSocket != null)
					listenSocket.close();
			} catch (IOException e) {
				logger.warn("Failed to close client connection.");
				logger.debug(e.getMessage());
			}
			logger.info("JiTy Agent shutdowned correctly.");
		}
	}

	/**
	 * Stop current agent if running.
	 * 
	 * @throws AgentException
	 */
	public void stopAgent() throws AgentException {
		if (this.isRunning) {
			logger.info("Shutdown of agent asked.");
			shutdownning = true;
			
			try {
				logger.info("Closing Network socket.");

				listenSocket.close();

				isRunning = false;
				
				logger.info("Agent successfuly shutdowned.");

			} catch (IOException e) {
				throw new AgentException("Shutdown of agent failed.");
			}
		} else {
			throw new AgentException("Agent is not running");
		}

	}

	/**
	 * Print current Agent configuration
	 */
	public void showConfig() {
		AgentConfig agentConfig = AgentConfig.getInstance();
		logger.info("AGENT_PORT = "+agentConfig.getAGENT_PORT());
		logger.info("AGENT_DESC = "+agentConfig.getAGENT_DESC());
		logger.info("HOSTNAME_LIST = "+agentConfig.getHOSTNAME_LIST());
		logger.info("JOBS_LOGS_DIR = "+agentConfig.getJOBS_LOGS_DIR());
	}
	
	/**
	 * return type of OS supporting the agent
	 * @return String
	 */
	public String getOSName() {
		return System.getProperty("os.name");
	}
	
	/**
	 * Loading the agent config file
	 * 
	 * @throws AgentException
	 */
	private void loadConfigFile() throws AgentException {
		try {
			AgentConfig agentConfig = AgentConfig.getInstance();
			logger.info("Reading configuration file.");
			agentConfig.initialize();
			logger.info("Configuration File successfully loaded.");
		} catch (IOException e) {
			throw new AgentException("Failed to read configuration file ("
					+ e.getMessage() + ").");
		}
	}

}
