package org.jity.tests;

import org.apache.log4j.Logger;
import org.jity.common.TimeUtil;
import org.jity.server.Server;
import org.jity.server.ServerException;

import junit.framework.TestCase;

public class TestServer extends TestCase {
	private static final Logger logger = Logger.getLogger(TestServer.class);
		
	public void testStartServerDaemon() {
		try {

			Server.getInstance().startServerDaemon();

			logger.info("Waiting 25 sec");
			TimeUtil.waiting(25);
			
		} catch (ServerException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testIsRunning() {
		assertTrue(Server.getInstance().isRunning());
	}
	
	
	public void testStopServerDaemon() {
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
	
	public void testIsNotRunning() {
		assertFalse(Server.getInstance().isRunning());
	}
	
}
