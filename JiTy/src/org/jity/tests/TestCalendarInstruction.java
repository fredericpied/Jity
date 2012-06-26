package org.jity.tests;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.DataException;
import org.jity.common.XMLUtil;
import org.jity.referential.persistent.Calendar;
import org.jity.server.Server;
import org.jity.server.ServerException;
import org.jity.server.ServerSideClient;
import org.jity.server.database.Database;
import org.jity.server.protocol.JityRequest;
import org.jity.server.protocol.JityResponse;

import junit.framework.TestCase;

public class TestCalendarInstruction extends TestCase {
	private static final Logger logger = Logger.getLogger(TestCalendarInstruction.class);

	public void setUp() {
		try {
			Server.getInstance().startServerDaemon();
			
			TestUtil.waiting(15);
			
		} catch (ServerException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void tearDown() {

		try {
			
			Server.getInstance().stopServerDaemon();

			TestUtil.waiting(5);
			
		} catch (ServerException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void testAddCalendarDatabase() {

		try {

			Calendar calendar = new Calendar();
			calendar.setName("Calendar1");
			calendar.setDescription("Calendar1 description");
			calendar.setYear(2012);
			calendar.setOpenDays("OOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCC"
							+ "OOOOOCCOOOOOCC");

			Session session = Database.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			session.save(calendar);
			transaction.commit();
			session.close();
			
			Database.terminateSessionFactory();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	public void testAddCalendar() {

		try {

			Calendar calendar = new Calendar();
			calendar.setName("Calendar2");
			calendar.setDescription("Calendar2 description");
			calendar.setYear(2012);
			calendar
					.setOpenDays("OOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCC"
							+ "OOOOOCCOOOOOCC");
			
			String xmlCalendar = XMLUtil.objectToXMLString(calendar);
			
			JityRequest request = new JityRequest();
			request.setInstructionName("ADDCALENDAR");
			request.setInstructionParameters(xmlCalendar);
			
			ServerSideClient client = new ServerSideClient();
			JityResponse response = client.sendRequest(request);
			
			assertEquals(response.getInstructionResult(), "OK");

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	
	
}
