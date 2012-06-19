package org.jity.server.database;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jity.referential.persistent.Calendar;

public class Database {
	private static final Logger logger = Logger.getLogger(Database.class);
	
	private SessionFactory sessionFactory;

	private Session session;
		
	public Database() {
		// A SessionFactory is set up once for an application
		// configures settings from hibernate.cfg.xml
		this.sessionFactory = new Configuration().configure().buildSessionFactory();
	}
	
	public Session getSession() {
	  if (this.session == null) this.session = sessionFactory.openSession();
	  return this.session;
	}
	
	public void closeSession() {
		this.session.close();
	}
	
	public void terminateSession() {
		if (this.session != null && this.session.isOpen()) this.closeSession();
		this.sessionFactory.close();
	}
	
	public static void main(String[] args) {
		SessionFactory sessionFactory;

		// A SessionFactory is set up once for an application
		sessionFactory = new Configuration().configure().buildSessionFactory(); // configures
		// settings
		// from
		// hibernate.cfg.xml

		Session session = sessionFactory.openSession();
//		session.beginTransaction();
//		session.save(new Calendar("c1", "description c1", 2012, "blabla"));
//		session.save(new Calendar("c2", "description c2", 2013, "blabla"));
//		session.getTransaction().commit();
		session.close();

		session = sessionFactory.openSession();
		session.beginTransaction();
		Iterator<Calendar> iterCalendar = session.createQuery("from Calendar")
				.iterate();
		// List<Calendar> result = session.createQuery("from Calendar").list();

		// for (Calendar calendar : (List<Calendar>) result) {
		// System.out.println("Calendar(" + calendar.getId() + ") : "
		// + calendar.getName());
		// }

		while (iterCalendar.hasNext()) {
			Calendar calendar = iterCalendar.next();
			System.out.println("Calendar(" + calendar.getId() + ") : "
					+ calendar.getName() + " " + calendar.getDescription());
		}

		session.getTransaction().commit();

		session.close();

		sessionFactory.close();
	}

}
