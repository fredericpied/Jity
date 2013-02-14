package org.jity.server.database;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;

public class HibernateSessionFactory {
	
	private static HibernateSessionFactory instance = null;
	
	private SessionFactory sessionFactory;
	
	/**
	 * Return current instance of HibernateSessionFactory.
	 * Create one if none exist
	 * @return H2DatabaseServer
	 */
	public static HibernateSessionFactory getInstance() {
		if (instance == null) {
			instance = new HibernateSessionFactory();
		}
		return instance;
	}
		
	/**
	 * Open and return a new Hibernate session
	 * @return Session
	 * @throws DatabaseException
	 */
	@SuppressWarnings("deprecation")
	public Session getSession() {
		if (sessionFactory == null) {
			sessionFactory = new Configuration().configure().buildSessionFactory();
		}
		return sessionFactory.openSession();
	}
	
	
	
}
