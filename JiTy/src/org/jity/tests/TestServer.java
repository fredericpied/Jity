package org.jity.tests;

import org.apache.log4j.Logger;
import org.jity.common.TestUtil;
import org.jity.server.Server;
import org.jity.server.ServerException;

import junit.framework.TestCase;

public class TestServer extends TestCase {
	private static final Logger logger = Logger.getLogger(TestServer.class);
		
	public void testStartServerDaemon() {
		try {

			Server.getInstance().startServerDaemon();

			TestUtil.waiting(15);
			
		} catch (ServerException e) {
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
			
			TestUtil.waiting(5);
			
		} catch (ServerException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testIsNotRunning() {
		assertFalse(Server.getInstance().isRunning());
	}
	
}
