package org.jity.tests;

import org.apache.log4j.Logger;
import org.jity.server.Server;
import org.jity.server.ServerException;

import junit.framework.TestCase;

public class TestCalendarInstruction extends TestCase {
	private static final Logger logger = Logger.getLogger(Server.class);

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

	public void testAddCalendar() {
		logger.info("Start testAddCalendar");

		try {
			logger.info("Waiting 15 seconds");

			// Pause de 15 secondes
			Thread.sleep(15 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("Start testAddCalendar");
	}

}
