package org.jity.server.database;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.h2.tools.Server;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseServer {
	private static final Logger logger = Logger.getLogger(DatabaseServer.class);
	
	private static SessionFactory sessionFactory;
	
	private static Server databaseServer = null;;
	
	public static void startDatabaseServer() throws DatabaseException {
		// start the TCP Server
		try {
			databaseServer = org.h2.tools.Server.createTcpServer().start();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	public static void stopDatabaseServer() {

		terminateSessionFactory();
		
		// stop the TCP Server
		if (databaseServer != null) databaseServer.stop();
		
	}
	
	public static Session getSession() throws DatabaseException {
		
		if (databaseServer == null) {
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
	
	private static void terminateSessionFactory() {
		if (sessionFactory != null) sessionFactory.close();
		sessionFactory = null;
	}
}
