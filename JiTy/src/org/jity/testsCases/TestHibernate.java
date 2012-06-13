package org.jity.testsCases;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jity.referential.persistent.Calendar;

public class TestHibernate {

	 @SuppressWarnings("deprecation")
	public static void main(String[] args) {
		 
	SessionFactory sessionFactory;
	
		 
	 // A SessionFactory is set up once for an application
    sessionFactory = new Configuration().configure() // configures settings from hibernate.cfg.xml
            .buildSessionFactory();
	 
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    session.save( new Calendar("c1","description c1",2012,"blabla") );
    session.save( new Calendar("c2","description c2",2013,"blabla") );
    session.getTransaction().commit();
    session.close();
    
    sessionFactory.close();
	 
	 }
	
}
