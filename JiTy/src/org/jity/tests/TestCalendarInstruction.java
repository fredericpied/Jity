package org.jity.tests;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.DataException;
import org.jity.UIClient.UIClientConfig;
import org.jity.common.TimeUtil;
import org.jity.common.XMLUtil;
import org.jity.protocol.JityRequest;
import org.jity.protocol.JityResponse;
import org.jity.protocol.RequestSender;
import org.jity.referential.PersonnalCalendar;
import org.jity.server.Server;
import org.jity.server.ServerException;
import org.jity.server.database.DatabaseServer;

import junit.framework.TestCase;

public class TestCalendarInstruction extends TestCase {
	private static final Logger logger = Logger.getLogger(TestCalendarInstruction.class);

	public void setUp() {
		try {
			Server.getInstance().startServerDaemon();
			
			logger.info("Waiting 10 sec");
			TimeUtil.waiting(10);
			
		} catch (ServerException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void tearDown() {

		try {
			
			Server.getInstance().stopServerDaemon();

			logger.info("Waiting 5 sec");
			TimeUtil.waiting(5);
			
		} catch (ServerException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void testAddCalendarDatabase() {

		try {

			PersonnalCalendar calendar = new PersonnalCalendar();
			calendar.setName("Calendar1");
			calendar.setDescription("Calendar1 description");
			calendar.setYear(2012);
			calendar.initializeWithAllDaysOpen();

			Session session = DatabaseServer.getSession();
			Transaction transaction = session.beginTransaction();

			session.save(calendar);
			
			transaction.commit();
			session.close();
			
			DatabaseServer.stopDatabaseServer();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	public void testAddCalendar() {

		try {

			PersonnalCalendar calendar = new PersonnalCalendar();
			calendar.setName("Calendar2");
			calendar.setDescription("Calendar2 description");
			calendar.setYear(2012);
			calendar.initializeWithAllDaysOpen();
			
			String xmlCalendar = XMLUtil.objectToXMLString(calendar);
			
			JityRequest request = new JityRequest();
			request.setInstructionName("ADDCALENDAR");
			request.setXmlInputData(xmlCalendar);
			
			// Load config file
			UIClientConfig clientConfig = UIClientConfig.getInstance();
			logger.info("Reading configuration file.");
			clientConfig.initialize();
			logger.info("Configuration File successfully loaded.");
			
			RequestSender requestSender = new RequestSender();
			requestSender.openConnection(clientConfig.getSERVER_HOSTNAME(), clientConfig.getSERVER_PORT());
			JityResponse response = requestSender.sendRequest(request);
			requestSender.closeConnection();
			
			assertTrue(response.isInstructionResultOK());

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	public void testGetCalendar() {

		try {
			
			Long idCalendar = Long.parseLong("1");
			
			JityRequest request = new JityRequest();
			request.setInstructionName("GETCALENDAR");
			request.setXmlInputData(XMLUtil.objectToXMLString(idCalendar));

			// Load config file
			UIClientConfig clientConfig = UIClientConfig.getInstance();
			logger.info("Reading configuration file.");
			clientConfig.initialize();
			logger.info("Configuration File successfully loaded.");
			
			RequestSender requestSender = new RequestSender();
			requestSender.openConnection(clientConfig.getSERVER_HOSTNAME(), clientConfig.getSERVER_PORT());
			JityResponse response = requestSender.sendRequest(request);
			requestSender.closeConnection();
			
			if (!response.isInstructionResultOK()) {
				throw new Exception(response.getExceptionMessage());
			} else {
				List list = (List)XMLUtil.XMLStringToObject(response.getXmlOutputData());
				PersonnalCalendar calendar = (PersonnalCalendar) list.get(0);
				logger.info(calendar.getName()+", "+calendar.getDescription());
			}
			
			assertTrue(response.isInstructionResultOK());

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	
	
}
