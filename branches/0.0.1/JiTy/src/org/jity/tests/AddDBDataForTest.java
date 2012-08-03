package org.jity.tests;

import java.util.Random;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jity.referential.PersonnalCalendar;
import org.jity.referential.Job;
import org.jity.referential.PersonnalCalendarException;
import org.jity.referential.dateConstraint.DateConstraint;
import org.jity.server.database.DatabaseException;
import org.jity.server.database.DatabaseServer;

public class AddDBDataForTest {

	public static void main(String[] args) {

		try {

			DatabaseServer.startDatabaseServer();
			Session session = DatabaseServer.getSession();
//			
//			String queryDelete = "delete job from org.jity.referential.Job job";
			
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
			
			Transaction transaction = session.beginTransaction();
			session.save(Cal5OpenDays2012);
			session.save(Cal6OpenDays2012);
			session.save(Cal7OpenDays2012);
			transaction.commit();

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
			DatabaseServer.stopDatabaseServer();
		
		} catch (DatabaseException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (PersonnalCalendarException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
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