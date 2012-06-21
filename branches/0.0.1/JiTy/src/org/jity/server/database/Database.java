package org.jity.server.database;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Database {
	private static final Logger logger = Logger.getLogger(Database.class);
	
	private static SessionFactory sessionFactory;
	
	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) sessionFactory = new Configuration().configure().buildSessionFactory(); 
		return sessionFactory;
	}
	
	public static void terminateSessionFactory() {
		sessionFactory.close();
	}

}
