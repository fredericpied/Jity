package org.jity.tests;

import org.apache.log4j.Logger;
import org.jity.UIClient.UIClient;
import org.jity.UIClient.UIClientException;
import org.jity.common.TimeUtil;
import org.jity.protocol.JityRequest;
import org.jity.protocol.JityResponse;
import org.jity.referential.PersonnalCalendarException;
import org.jity.server.Server;
import org.jity.server.ServerException;
import org.jity.server.database.DatabaseException;

import junit.framework.TestCase;

public class TestServer extends TestCase {
	private static final Logger logger = Logger.getLogger(TestServer.class);
		
	public void testStartServerDaemon() {
		try {

			Server.getInstance().startServerDaemon();

			logger.info("Waiting 5 sec");
			TimeUtil.waiting(5);
			
			AddDBDataForTest.launch();
			
			logger.info("Waiting 15 sec");
			TimeUtil.waiting(15);
			
		} catch (ServerException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (DatabaseException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (PersonnalCalendarException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testIsRunning() {
		assertTrue(Server.getInstance().isRunning());
	}
	
	public void testUIClientStartPlanifDaemon() {
		try {
			
			JityRequest request = new JityRequest();
			request.setInstructionName("STARTPLANIFDAEMON");
			
			UIClient client = UIClient.getInstance();
			JityResponse response = client.sendRequest(request);

			if (!response.isInstructionResultOK())
				throw new Exception(response.getExceptionMessage());
			
			
			logger.info("Waiting 15 sec");
			TimeUtil.waiting(15);
		
			assertTrue(response.isInstructionResultOK());
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UIClientException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testUIClientStopPlanifDaemon() {
		try {
			
			JityRequest request = new JityRequest();
			request.setInstructionName("SHUTDOWNPLANIFDAEMON");
			
			UIClient client = UIClient.getInstance();
			JityResponse response = client.sendRequest(request);

			if (!response.isInstructionResultOK())
				throw new Exception(response.getExceptionMessage());
						
			logger.info("Waiting 5 sec");
			TimeUtil.waiting(5);
		
			assertTrue(response.isInstructionResultOK());
						
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UIClientException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	public void testUIClientStopServerDaemon() {
		try {
			
			JityRequest request = new JityRequest();
			request.setInstructionName("SHUTDOWNSERVER");
			
			UIClient client = UIClient.getInstance();
			JityResponse response = client.sendRequest(request);


			if (!response.isInstructionResultOK())
				throw new Exception(response.getExceptionMessage());
		
			assertTrue(response.isInstructionResultOK());
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UIClientException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testIsNotRunning() {
		assertFalse(Server.getInstance().isRunning());
	}
	
}
