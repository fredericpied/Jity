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
import org.jity.common.protocol.RequestReceiver;
import org.jity.server.database.HibernateSessionFactory;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Server {
	private static final Logger logger = Logger.getLogger(Server.class);

	private static Server instance = null;

	private ServerSocket listenSocket;

	private boolean isRunning = false;
	
	private boolean shutdowning = false;
	
	private Date execDate;
	
	private static org.h2.tools.Server H2DBServer = null;
	
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
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Start the server.
	 */
	public void start() {
		Socket client = null;
		
		shutdowning = false;
		
		logger.info("JiTy Server starting process.");

		// Loading config File
		try {
			this.loadConfigFile();
		} catch (ServerException e1) {
			logger.fatal(e1.getMessage());
			try {
				this.stop();
			} catch (ServerException e) {}
			System.exit(1);
		}

		try {
			
			this.H2DBServer = org.h2.tools.Server.createTcpServer().start();
			logger.info(H2DBServer.getStatus());
			
			// Init database connection
			logger.info("Init of DB connection...");
			Session sess = HibernateSessionFactory.getInstance().getSession();
			sess.close();
			logger.info("Connection to database: OK");

		} catch (SQLException e) {
			logger.fatal(e.getMessage());

			this.H2DBServer.stop();
			
			System.exit(1);
		}
		
		
		try {
			String localHostname = java.net.InetAddress.getLocalHost().getHostName();
			
			logger.info("Starting the server on "+localHostname+"...");
			
		} catch (UnknownHostException e1) {
			logger.warn(e1.getMessage());
			logger.info("Starting the server...");
		}
				
		ServerTaskStatutManagerDaemon.getInstance().startTaskStatusListener();	
		
		int serverPort = ServerConfig.getInstance().getSERVER_UI_INPUT_PORT();
		try {
			listenSocket = new ServerSocket(serverPort);
			logger.info("Server running on port : " + serverPort);
			logger.info("JiTy Server successfully started.");
		} catch (IOException e) {
			logger.fatal(e.getMessage());
			
			this.H2DBServer.stop();
			
			System.exit(1);
		}

		try {
			isRunning = true;
			
			while (true) {
				client = listenSocket.accept();
				try {
					logger.info("New connection from "
							+ client.getInetAddress() + ".");
					//new ServeOneUIClientRequest(client);
					new RequestReceiver(client);
				} catch (IOException e) {
					client.close();
				}
			}

		} catch (IOException e) {
			if (!shutdowning) logger.warn("Problem during client connection.");
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
			logger.info("JiTy Server shutdowned correctly.");
		}
	}

	/**
	 * Stop current server if running.
	 * 
	 * @throws ServerException
	 */
	public void stop() throws ServerException {
		if (this.isRunning) {
			logger.info("Shutdown of server asked.");
			shutdowning = true;
			
			try {
				
				ServerTaskLauncherDaemon.getInstance().stop();
				
				ServerTaskStatutManagerDaemon.getInstance().stopTaskStatusListener();
				
				logger.info("Shutdowing H2 Database server.");
				this.H2DBServer.stop();
				
				logger.info("Closing Network socket.");
				listenSocket.close();

				isRunning = false;
				logger.info("Server successfuly shutdowned");

			} catch (IOException e) {
				throw new ServerException("Shutdown of server failed.");
			}
		} else {
			throw new ServerException("Server is not running");
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
			serverConfig.showConfig();
		} catch (IOException e) {
			throw new ServerException("Failed to read configuration file ("
					+ e.getMessage() + ").");
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
