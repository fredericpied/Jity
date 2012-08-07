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

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.jity.server.database.DatabaseException;
import org.jity.server.database.DatabaseServer;
import org.jity.server.planifDaemon.PlanifDaemon;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Server implements Runnable {
	private static final Logger logger = Logger.getLogger(Server.class);

	private static Server instance = null;

	private ServerSocket listenSocket;

	private Thread daemon = null;

	private boolean shutdownAsked = false;
	
	private Date execDate;
	
	public static Server getInstance() {
		if (instance == null) {
			instance = new Server();
		}
		return instance;
	}

	/**
	 * Return true if JitY Server is running
	 * 
	 * @return
	 */
	public synchronized boolean isRunning() {
		if (this.daemon != null)
			return this.daemon.isAlive();
		else
			return false;
	}

	/**
	 * Start the server in a Thread.
	 * 
	 * @throws ServerException
	 */
	public synchronized void startServerDaemon() throws ServerException {
		if (daemon == null) {
			daemon = new Thread(this);
			daemon.start();
		}
	}

	/**
	 * Stop current server if running.
	 * 
	 * @throws ServerException
	 */
	public synchronized void stopServerDaemon() throws ServerException {
		if (daemon != null) {
			logger.info("Shutdown of server asked.");

			try {
				shutdownAsked = true;
				logger.info("Shutdowning planification daemon.");
				PlanifDaemon.getInstance().stopPlanifDaemon();
				
				logger.info("Closing Database server.");
				DatabaseServer.stopDatabaseServer();
				
				logger.info("Closing Network socket.");
				listenSocket.close();

				daemon.interrupt();
				daemon = null;

				logger.info("Server successfuly shutdowned");

			} catch (IOException e) {
				throw new ServerException("Shutdown of server failed.");
			}

		}
	}

	/**
	 * Loading the server config file
	 * 
	 * @throws ServerException
	 */
	private void loadConfigFile() throws ServerException {
		// Load config file
		try {
			ServerConfig serverConfig = ServerConfig.getInstance();
			logger.info("Reading configuration file.");
			serverConfig.initialize();
			logger.info("Configuration File successfully loaded.");
		} catch (IOException e) {
			throw new ServerException("Failed to read configuration file ("
					+ e.getMessage() + ").");
		}
	}

	public void run() {
		Socket client = null;

		logger.info("JiTy Server starting process.");

		// Loading config File
		try {
			this.loadConfigFile();
		} catch (ServerException e1) {
			logger.fatal(e1.getMessage());
			try {
				this.stopServerDaemon();
			} catch (ServerException e) {}
			System.exit(1);
		}

		try {
			
			DatabaseServer.startDatabaseServer();
			
			// Init database connection
			logger.info("Init of DB connection...");
			Session sess = DatabaseServer.getSession();
			sess.close();
			logger.info("Connection to database: OK");

		} catch (DatabaseException e) {
			logger.fatal(e.getMessage());

			DatabaseServer.stopDatabaseServer();
			
			System.exit(1);
		}

		
		try {
			String localHostname = java.net.InetAddress.getLocalHost().getHostName();
			
			logger.info("Starting the server on "+localHostname+"...");
			
		} catch (UnknownHostException e1) {
			logger.warn(e1.getMessage());
			logger.info("Starting the server...");
		}
		
		int serverPort = ServerConfig.getInstance().getSERVER_PORT();
		try {
			listenSocket = new ServerSocket(serverPort);
			logger.info("Server running on port : " + serverPort);
			logger.info("JiTy Server successfully started.");
		} catch (IOException e) {
			logger.fatal(e.getMessage());
			
			DatabaseServer.stopDatabaseServer();
			
			System.exit(1);
		}

		try {

			while (true) {
				client = listenSocket.accept();
				try {
					logger.info("New connection from "
							+ client.getInetAddress() + ".");
					new ServeOneUIClient(client);
				} catch (IOException e) {
					client.close();
				}
			}

		} catch (IOException e) {
			if (!shutdownAsked) logger.warn("Problem during client connection.");
			logger.debug(e.getMessage());
		} finally {
			try {
				if (listenSocket != null)
					listenSocket.close();
			} catch (IOException e) {
				logger.warn("Failed to close client connection.");
				logger.debug(e.getMessage());
			}
			logger.info("JiTy Server shutdowned correctly.");
		}
	}

	private void initializeExecDate() {
		Calendar cal = new GregorianCalendar();
		this.execDate = cal.getTime();
	}
	
	public Date getExecDate() {
		return execDate;
	}

	public void setExecDate(Date execDate) {
		this.execDate = execDate;
	}
	
	

}
