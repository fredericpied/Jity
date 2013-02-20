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
import org.hibernate.Transaction;
import org.jity.common.protocol.RequestReceiver;
import org.jity.common.referential.UserGroup;
import org.jity.common.referential.User;
import org.jity.common.util.StringCrypter;
import org.jity.server.database.DataNotFoundDBException;
import org.jity.server.database.HibernateSessionFactory;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Server {
	private static final Logger logger = Logger.getLogger(Server.class);

	private static Server instance = null;

	private ServerSocket listenSocket;

	private boolean isRunning = false;
	
	private boolean shutdowning = false;
	
	private Date execDate;
	
	private org.h2.tools.Server H2DBServer = null;
	
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

		// Start H2 Database server
		try {
			
			this.H2DBServer = org.h2.tools.Server.createTcpServer().start();
			logger.info(H2DBServer.getStatus());
			
			// test database connection
			logger.info("Init of DB connection...");
			Session sess = HibernateSessionFactory.getInstance().getSession();
			sess.close();
			logger.info("Connection to database: OK");

		} catch (SQLException e) {
			logger.fatal(e.getMessage());

			this.H2DBServer.stop();
			
			System.exit(1);
		}

		// Initialize Administrator user if not exist
		try {
			createAdminUser();
		} catch (ServerException e2) {
			logger.fatal(e2.getMessage());

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

	/**
	 * Create Admin User in DB if not exist
	 * @throws ServerException 
	 */
	private void createAdminUser() throws ServerException  {

		Session session = HibernateSessionFactory.getInstance().getSession();

		String queryFindUser = "select user from org.jity.common.referential.User user" +
		" where user.login='admjity'";
		
		List listUser = session.createQuery(queryFindUser).list();

		
		// If User does not existe whith this login, create
		if (listUser.size() == 0) {
			try {
				
				String queryFindGroup = "select usergroup from org.jity.common.referential.UserGroup usergroup" +
				" where usergroup.name='administrators'";
				
				UserGroup groupAdm = null;
				List listGroup = session.createQuery(queryFindGroup).list();
				if (listGroup.size() == 0) {
					
					// If Group dos not exist, create
					groupAdm = new UserGroup();
					groupAdm.setName("administrators");
					groupAdm.setDescription("Group for JiTy administrators");
					Transaction transaction = session.beginTransaction();
					session.save(groupAdm);
					transaction.commit();

				} else {
					groupAdm = (UserGroup)listGroup.get(0);
				}
				
				User admUser = new User();
				admUser.setLogin("admjity");
				String encryptedPassword = StringCrypter.encrypt("admjity", "JiTyCedricFred13");
				admUser.setPassword(encryptedPassword);
				admUser.setName("JiTy Administrator");
				admUser.setGroup(groupAdm);
				
				Transaction transaction = session.beginTransaction();
				session.save(admUser);
				transaction.commit();
				session.close();
				logger.info("Default JiTy Administrator user \"admjity\" succesfully created");
				
			} catch (InvalidKeyException e) {
				session.close();
				throw new ServerException("Failed to create JiTy Administrator user in database ("
						+ e.getMessage() + ").");
			} catch (NoSuchAlgorithmException e) {
				session.close();
				throw new ServerException("Failed to create JiTy Administrator user in database ("
						+ e.getMessage() + ").");
			} catch (NoSuchPaddingException e) {
				session.close();
				throw new ServerException("Failed to create JiTy Administrator user in database ("
						+ e.getMessage() + ").");
			} catch (IllegalBlockSizeException e) {
				session.close();
				throw new ServerException("Failed to create JiTy Administrator user in database ("
						+ e.getMessage() + ").");
			} catch (BadPaddingException e) {
				session.close();
				throw new ServerException("Failed to create JiTy Administrator user in database ("
						+ e.getMessage() + ").");
			}

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
