package org.jity.tests;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jity.common.referential.Job;
import org.jity.common.referential.PersonnalCalendar;
import org.jity.common.referential.PersonnalCalendarException;
import org.jity.common.referential.dateConstraint.DateConstraint;
import org.jity.server.Server;
import org.jity.server.database.DatabaseException;
import org.jity.server.database.DatabaseServer;

public class AddDBDataForTest {
	private static final Logger logger = Logger.getLogger(AddDBDataForTest.class);
	
	public static void launch() throws DatabaseException, PersonnalCalendarException {

//		try {

			Session session = DatabaseServer.getSession();
						
			logger.info("Deleting Job, DateConstraint and PersonnalCalendar in DB...");
			
			// Delete all jobs in db
			String queryFindJob = "select job from org.jity.common.referential.Job job";
			List jobList = session.createQuery(queryFindJob).list();
			Iterator iterJobList = jobList.iterator();
			while (iterJobList.hasNext()) {
				Job job = (Job) iterJobList.next();
				session.delete(job);
			}
			
			// Delete all dateConstraint in db
			String queryFindDC = "select dateConstraint" +
					" from org.jity.common.referential.dateConstraint.DateConstraint dateConstraint";
			List dcList = session.createQuery(queryFindDC).list();
			Iterator iterDcList = dcList.iterator();
			while (iterDcList.hasNext()) {
				DateConstraint dc = (DateConstraint) iterDcList.next();
				session.delete(dc);
			}
			
			// Delete all PersonnalCalendar in db
			String queryFindPC = "select personnalCalendar" +
					" from org.jity.common.referential.PersonnalCalendar personnalCalendar";
			List pcList = session.createQuery(queryFindPC).list();
			Iterator iterPcList = pcList.iterator();
			while (iterPcList.hasNext()) {
				PersonnalCalendar pc = (PersonnalCalendar) iterPcList.next();
				session.delete(pc);
			}
			
			// Calendars
			PersonnalCalendar Cal5OpenDays2012 = new PersonnalCalendar();
			Cal5OpenDays2012.setName("Cal5OpenDays2012");
			Cal5OpenDays2012.setYear(2012);
			Cal5OpenDays2012.initializeWithAllDaysOpen();
			Cal5OpenDays2012.addFrenchHolydays();
			Cal5OpenDays2012.addClosedDayOfWeek(6);
			Cal5OpenDays2012.addClosedDayOfWeek(7);
			
			PersonnalCalendar Cal6OpenDays2012 = new PersonnalCalendar();
			Cal6OpenDays2012.setName("Cal6OpenDays2012");
			Cal6OpenDays2012.setYear(2012);
			Cal6OpenDays2012.initializeWithAllDaysOpen();
			Cal6OpenDays2012.addFrenchHolydays();
			Cal6OpenDays2012.addClosedDayOfWeek(7);
			
			PersonnalCalendar Cal7OpenDays2012 = new PersonnalCalendar();
			Cal7OpenDays2012.setName("Cal7OpenDays2012");
			Cal7OpenDays2012.setYear(2012);
			Cal7OpenDays2012.initializeWithAllDaysOpen();
			Cal7OpenDays2012.addFrenchHolydays();
							
			PersonnalCalendar[] tabPersonnalCalendar = new PersonnalCalendar[3];
			tabPersonnalCalendar[0] = Cal5OpenDays2012;
			tabPersonnalCalendar[1] = Cal6OpenDays2012;			
			tabPersonnalCalendar[2] = Cal7OpenDays2012;
			
			logger.info("Adding PersonnalCalendar");

			Transaction transaction = session.beginTransaction();
			session.save(Cal5OpenDays2012);
			session.save(Cal6OpenDays2012);
			session.save(Cal7OpenDays2012);
			transaction.commit();

			logger.info("Adding DateConstraints and Jobs");
			for (int i=1;i<150;i++) {
			
				DateConstraint dateConstraint1 = new DateConstraint();
				dateConstraint1.setCalendar(tabPersonnalCalendar[getRandomInt(0,2)]);
				dateConstraint1.setPlanifRule(planifRuleGenerator());
	 						
				transaction = session.beginTransaction();
				session.save(dateConstraint1);
				transaction.commit();

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
			logger.info("Finish");
			//DatabaseServer.stopDatabaseServer();
		
//		} catch (DatabaseException e) {
//			e.printStackTrace();
//			System.exit(1);
//		} catch (PersonnalCalendarException e) {
//			e.printStackTrace();
//			System.exit(1);
//		}

	}
	
	/**
	 * Generate random int
	 * @param min
	 * @param max
	 * @return
	 */
	private  static int getRandomInt(int min, int max) {
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		return rand.nextInt(max - min + 1) + min;
	}
	
	
	/**
	 * Generate random planification rules
	 * @return
	 */
	private static String planifRuleGenerator() {
		
		String planifRule;
		
		int operatorInd = getRandomInt(0, DateConstraint.OPERATOR_KEYWORDS.length-1);

		int dayTypeInd = getRandomInt(0, DateConstraint.DAY_TYPE_KEYWORDS.length-1);
		int dayNameInd = getRandomInt(0, DateConstraint.DAY_NAME_KEYWORDS.length-1);
		int periodInd = getRandomInt(0, DateConstraint.PERIOD_KEYWORDS.length-1);

		//int dayNumInd = getRandomInt(0, DateConstraint.DAY_NUM_KEYWORDS.length-1);
		
		int maxDayInPeriod = 0;
		if (DateConstraint.PERIOD_KEYWORDS[periodInd].equals("week")) {
			maxDayInPeriod = 7;
		} else if (DateConstraint.PERIOD_KEYWORDS[periodInd].equals("year")) {
			maxDayInPeriod = 365;
		} else {
			maxDayInPeriod = 30;
		}
		
		int dayNumInd = getRandomInt(1, maxDayInPeriod);
		
		if (getRandomInt(0, 1) == 1) {
			dayNumInd = dayNumInd * -1;
		}
		
		planifRule = DateConstraint.OPERATOR_KEYWORDS[operatorInd]+"_"+
			dayNumInd+"_"+
			DateConstraint.DAY_TYPE_KEYWORDS[dayTypeInd]+"_"+
			DateConstraint.DAY_NAME_KEYWORDS[dayNameInd]+"_"+
			DateConstraint.PERIOD_KEYWORDS[periodInd];
		
		return planifRule;
		
	}

	

}