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

/**
 * Classe principale du serveur jity.
 * 
 * Cette classe créer un processus qui traite les requetes émise par les UIClient.
 * 
 * Elle démarre deux autres processus:
 *  - Le "ServerTaskLauncher" qui scrute la base de données à la recherche de job a exécuter, créer les 
 *  taches d'exécution sur les agents concernés
 *  - Le "ServerTaskStatusManager" qui traite les requete de mise à jour du statut des taches
 *  d'exécution dans la base. Ces requete sont envoyées par les agents.
 * 
 * @author Fred
 *
 */
public class Server {
	private static final Logger logger = Logger.getLogger(Server.class);

	private static Server instance = null;

	private ServerSocket listenSocket;

	/**
	 * True if server is currently running
	 */
	private boolean isRunning = false;
	
	/**
	 * True if server is going to shutdown
	 */
	private boolean shutdowning = false;
	
	/**
	 * Current H2 Database server
	 */
	private org.h2.tools.Server H2DBServer = null;
	
	/**
	 * Return current instance of Server, create one if none exist.
	 * 
	 * @return Server
	 */
	public static Server getInstance() {
		if (instance == null) {
			instance = new Server();
		}
		return instance;
	}

	/**
	 * Return true if JitY Server is running.
	 * 
	 * @return boolean
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Start the JiTy Server process.
	 */
	public void start() {
		Socket client = null;
		
		shutdowning = false;
		
		// Find hostname to display it (display generic message if can't found)
		try {
			String localHostname = java.net.InetAddress.getLocalHost().getHostName();
			logger.info("Starting Jity Server on "+localHostname+"...");
		} catch (UnknownHostException e1) {
			logger.warn(e1.getMessage());
			logger.info("Starting Jity Server...");
		}

		// Loading ServerConfig.xml file
		try {
			ServerConfig serverConfig = ServerConfig.getInstance();
			logger.info("Reading configuration file "+serverConfig.getXmlFileName()+"...");
			serverConfig.initialize();
			serverConfig.showConfig();
		} catch (IOException e) {
			logger.fatal("Failed to read configuration file: "+e.getMessage());
			System.exit(1);
		}
		
		// Start H2 Database server
		try {
			logger.info("Starting H2 Database server...");
			this.H2DBServer = org.h2.tools.Server.createTcpServer().start();
			logger.info(H2DBServer.getStatus());
			
			// test database connection
			logger.info("Test database connection...");
			Session sess = HibernateSessionFactory.getInstance().getSession();
			sess.close();
			logger.info("Connection to database OK");

		} catch (SQLException e) {
			logger.fatal("Failed to start database server: "+e.getMessage());
			this.H2DBServer.stop();
			System.exit(1);
		}

		// Initialize Jity Administrator user and group if not exist in database
		try {
			initializeJityAdministrator();
		} catch (ServerException e2) {
			logger.fatal(e2.getMessage());
			this.H2DBServer.stop();
			System.exit(1);
		}
		
		// Starting Server Task Status Manager process
		ServerTaskStatusManagerDaemon.getInstance().startTaskStatusListener();	
		
		// Create listenning socket
		int serverPort = ServerConfig.getInstance().getSERVER_UI_INPUT_PORT();
		try {
			listenSocket = new ServerSocket(serverPort);
			logger.info("Server running on port " + serverPort);
			logger.info("JiTy Server successfully started.");
		} catch (IOException e) {
			logger.fatal(e.getMessage());
			this.H2DBServer.stop();
			System.exit(1);
		}
		
		try {
			isRunning = true;
			
			// Waiting client connect
			while (true) {
				client = listenSocket.accept();
				try {
					logger.debug("New connection from "
							+ client.getInetAddress() + ".");
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
				if (listenSocket != null) listenSocket.close();
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
			logger.info("Shutdown of server asked...");
			shutdowning = true;
			
			try {
				
				ServerTaskLauncherDaemon.getInstance().stop();
				
				ServerTaskStatusManagerDaemon.getInstance().stopTaskStatusListener();
				
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
	 * Return True if user exist in database
	 * @param userName
	 * @return boolean
	 */
	private boolean ifUserExist(String userName) {
		Session session = HibernateSessionFactory.getInstance().getSession();
		String queryFindUser = "select user from org.jity.common.referential.User user" +
		" where user.login='"+userName+"'";
		List listUser = session.createQuery(queryFindUser).list();
		session.close();
		
		// If User does not existe whith this login, return false
		if (listUser.size() == 0) return false;
		else return true;
		
	}
	
	/**
	 * Return True if group exist in database
	 * @param groupName
	 * @return boolean
	 */
	private boolean ifUserGroupExist(String groupName) {
		Session session = HibernateSessionFactory.getInstance().getSession();
		String queryFindGroup = "select usergroup from org.jity.common.referential.UserGroup usergroup" +
		" where usergroup.name='"+groupName+"'";
		List listGroup = session.createQuery(queryFindGroup).list();
		session.close();
		
		// If Group does not existe, return false
		if (listGroup.size() == 0) return false;
		else return true;
		
	}
	
	
	/**
	 * Create Admin User in DB if not exist
	 * @throws ServerException 
	 */
	private void initializeJityAdministrator() throws ServerException  {

		// test if user admjity exist in database
		if (! this.ifUserExist("admjity")) {
			
			// Create database session
			Session session = HibernateSessionFactory.getInstance().getSession();
			
			UserGroup groupAdm = null;
			
			// if user non exist, test if admnistrators group exist in databse
			if (! this.ifUserGroupExist("administrators")) {
				// if administrators group does not exist, create default one
				groupAdm = new UserGroup();
				groupAdm.setName("administrators");
				groupAdm.setDescription("Group for JiTy administrators");
				Transaction transaction = session.beginTransaction();
				session.save(groupAdm);
				transaction.commit();
			} else {
				// if administrator group exist, read it
				String queryFindGroup = "select usergroup from org.jity.common.referential.UserGroup usergroup" +
				" where usergroup.name='administrators'";
				List listGroup = session.createQuery(queryFindGroup).list();
				groupAdm = (UserGroup)listGroup.get(0);
			}

			try {
				logger.info("Create default Jity Administrator");
				
				// Create new User
				User admUser = new User();
				admUser.setLogin("admjity");
				admUser.setName("JiTy Administrator");
				
				// Encrypt default password
				String encryptedPassword = StringCrypter.encrypt("admjity", "JiTyCedricFred13");
				admUser.setPassword(encryptedPassword);
				
				// Set administrators group
				admUser.setGroup(groupAdm);
				
				Transaction transaction = session.beginTransaction();
				session.save(admUser);
				transaction.commit();

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
			
			session.close();
		}
		
	}


}
