package org.jity.tests;

import java.util.Iterator;

import org.hibernate.Session;
import org.jity.referential.persistent.Calendar;
import org.jity.server.database.Database;

import junit.framework.TestCase;

public class TestDatabase extends TestCase{

	public TestDatabase(String name) {
		super(name);
	}

	public void setUp() {
		
	}

	public void tearDown() {
		
	}
	
	public void testDBConnexion() {
		Session session = Database.getSessionFactory().openSession();
		session.close();
		Database.terminateSessionFactory();
	}

	public void testDBSelect() {

		Session session = Database.getSessionFactory().openSession();
		session.beginTransaction();
		Iterator<Calendar> iterCalendar = session.createQuery("from Calendar").iterate();

		while (iterCalendar.hasNext()) {
			Calendar calendar = iterCalendar.next();
			System.out.println("Calendar(" + calendar.getId() + ") : "
					+ calendar.getName() + " " + calendar.getDescription());
		}

		session.getTransaction().commit();

		session.close();

		Database.terminateSessionFactory();
	}

	
	
}
