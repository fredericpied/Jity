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
import org.jity.server.protocol.Request;
import org.jity.server.protocol.Response;

import junit.framework.TestCase;

public class TestCalendarInstruction extends TestCase {
	private static final Logger logger = Logger
			.getLogger(TestCalendarInstruction.class);

	public void setUp() {
		Server server = Server.getInstance();
		try {
			server.startServerDaemon();
		} catch (ServerException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void tearDown() {

		Server server = Server.getInstance();
		try {

			server.stopServerDaemon();

		} catch (ServerException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void testAddCalendarDatabase() {

		try {

			// Pause de 15 secondes
			// Thread.sleep(15 * 1000);

			Calendar calendar = new Calendar();
			calendar.setName("Calendar1");
			calendar.setDescription("Calendar1 descritpion");
			calendar.setYear(2012);
			calendar
					.setOpenDays("OOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCC"
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

			// Pause de 15 secondes
			// Thread.sleep(15 * 1000);

			Calendar calendar = new Calendar();
			calendar.setName("Calendar1");
			calendar.setDescription("Calendar1 descritpion");
			calendar.setYear(2012);
			calendar
					.setOpenDays("OOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCC"
							+ "OOOOOCCOOOOOCC");
			
			String xmlCalendar = XMLUtil.objectToXMLString(calendar);
			
			Request request = new Request();
			request.setInstructionName("ADDCALENDAR");
			request.setInstructionParameters(xmlCalendar);
			
			ServerSideClient client = new ServerSideClient();
			Response response = client.sendRequest(request);
			
			assertEquals(response.getInstructionResult(), "OK");

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	
	
}
