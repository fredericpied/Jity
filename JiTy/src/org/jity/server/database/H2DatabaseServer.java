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
package org.jity.server.database;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Wrapper for H2 Database Java Server instance
 * @author 09344a
 *
 */
public class H2DatabaseServer {
	private static final Logger logger = Logger.getLogger(H2DatabaseServer.class);
	
	private static H2DatabaseServer instance = null;
	
	private static SessionFactory sessionFactory;
	
	private static org.h2.tools.Server H2DBServer = null;;
	
	/**
	 * Return current instance of H2 Database Server.
	 * Create one if none exist
	 * @return H2DatabaseServer
	 */
	public static H2DatabaseServer getInstance() {
		if (instance == null) {
			instance = new H2DatabaseServer();
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
