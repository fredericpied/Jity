package org.jity.tests;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jity.referential.Calendar;
import org.jity.referential.DateConstraint;
import org.jity.referential.Job;
import org.jity.server.database.DatabaseException;
import org.jity.server.database.DatabaseServer;

public class AddDBDataForTest {

	public static void main(String[] args) {

		try {
			DatabaseServer.startDatabaseServer();
			Session session = DatabaseServer.getSession();
			
			// Calendars
			Calendar Cal5OpenDays2012 = new Calendar("Cal5OpenDays2012", 
					"5 open days 2012", 2012, "OOOOOCCOOOOOCCOOOOOCCOOOOOCC" +
							"OOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOO" +
							"OCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCO" +
							"OOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOO" +
							"CCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCC"
							+ "OOOOOCCOOOOOCC");

			Calendar Cal6OpenDays2012 = new Calendar("Cal6OpenDays2012", 
					"6 open days 2012", 2012, "OOOOOCCOOOOOCCOOOOOCCOOOOOCC" +
							"OOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOO" +
							"OCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCO" +
							"OOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOO" +
							"CCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCC"
							+ "OOOOOCCOOOOOCC");
			
			Calendar Cal7OpenDays2012 = new Calendar("Cal7OpenDays2012", 
					"7 open days 2012", 2012, "OOOOOCCOOOOOCCOOOOOCCOOOOOCC" +
							"OOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOO" +
							"OCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCO" +
							"OOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOO" +
							"CCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCC"
							+ "OOOOOCCOOOOOCC");
						
			Transaction transaction = session.beginTransaction();
			session.save(Cal5OpenDays2012);
			session.save(Cal6OpenDays2012);
			session.save(Cal7OpenDays2012);
			transaction.commit();

			DateConstraint dateConstraint1 = new DateConstraint();
			dateConstraint1.setCalendar(Cal5OpenDays2012);
			dateConstraint1.setPlanifRule("ALLDAYS");
 			
			DateConstraint dateConstraint2 = new DateConstraint();
			dateConstraint2.setCalendar(Cal6OpenDays2012);
			dateConstraint2.setPlanifRule("ALLDAYS");
			
			transaction = session.beginTransaction();
			session.save(dateConstraint1);
			session.save(dateConstraint2);
			transaction.commit();
			
			for (int i=1;i<150;i++) {
				Job job = new Job();
				job.setName("JOB"+i);
				job.setHostName("localhost");
				job.setCommandPath("d:\\temp\test.bat");
				job.setIsActived(true);
				job.setDateConstraint(dateConstraint1);
				
				transaction = session.beginTransaction();
				session.save(job);
				transaction.commit();
			}
			
			session.close();
			DatabaseServer.stopDatabaseServer();
		
		} catch (DatabaseException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}