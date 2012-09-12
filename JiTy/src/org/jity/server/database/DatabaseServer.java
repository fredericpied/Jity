package org.jity.server.database;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseServer {
	private static final Logger logger = Logger.getLogger(DatabaseServer.class);
	
	private static DatabaseServer instance = null;
	
	private static SessionFactory sessionFactory;
	
	private static org.h2.tools.Server H2DBServer = null;;
	
	public static DatabaseServer getInstance() {
		if (instance == null) {
			instance = new DatabaseServer();
		}
		return instance;
	}
	
	/**
	 * Start embeded H2 Database server
	 * @throws DatabaseException
	 */
	public void start() throws DatabaseException {
		// start the TCP Server
		try {
			H2DBServer = org.h2.tools.Server.createTcpServer().start();
			logger.info(H2DBServer.getStatus());
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	/**
	 * Stop H2 Database server
	 */
	public void stop() {
		
		// stop sessionFactory
		if (sessionFactory != null) sessionFactory.close();
		sessionFactory = null;
		
		// stop the TCP Server
		if (H2DBServer != null) H2DBServer.stop();
		
	}
	
	/**
	 * Open and return a new H2 Database session
	 * @return Session
	 * @throws DatabaseException
	 */
	@SuppressWarnings("deprecation")
	public Session getSession() throws DatabaseException {
		
		if (H2DBServer == null) {
			throw new DatabaseException("Database server is not running.");
		}
		
		try {
			if (sessionFactory == null) {
				
				sessionFactory = new Configuration().configure().buildSessionFactory(); 
			}
		
			return sessionFactory.openSession();
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
}
