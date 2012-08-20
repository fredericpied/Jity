package org.jity.tests;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jity.UIClient.UIClientConfig;
import org.jity.UIClient.UIClientException;
import org.jity.common.TimeUtil;
import org.jity.protocol.JityRequest;
import org.jity.protocol.JityResponse;
import org.jity.protocol.RequestSender;
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
			
			// Load config file
			UIClientConfig clientConfig = UIClientConfig.getInstance();
			logger.info("Reading configuration file.");
			clientConfig.initialize();
			logger.info("Configuration File successfully loaded.");
			
			RequestSender requestSender = new RequestSender();
			requestSender.openConnection(clientConfig.getSERVER_HOSTNAME(), clientConfig.getSERVER_PORT());
			JityResponse response = requestSender.sendRequest(request);
			requestSender.closeConnection();
			
			if (!response.isInstructionResultOK())
				throw new Exception(response.getExceptionMessage());
						
			logger.info("Waiting 5 sec");
			TimeUtil.waiting(5);
		
			assertTrue(response.isInstructionResultOK());
						
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testUIClientStopPlanifDaemon() {
		try {
			
			JityRequest request = new JityRequest();
			request.setInstructionName("SHUTDOWNPLANIFDAEMON");
			
			// Load config file
			UIClientConfig clientConfig = UIClientConfig.getInstance();
			logger.info("Reading configuration file.");
			clientConfig.initialize();
			logger.info("Configuration File successfully loaded.");
			
			RequestSender requestSender = new RequestSender();
			requestSender.openConnection(clientConfig.getSERVER_HOSTNAME(), clientConfig.getSERVER_PORT());
			JityResponse response = requestSender.sendRequest(request);
			requestSender.closeConnection();
			
			if (!response.isInstructionResultOK())
				throw new Exception(response.getExceptionMessage());
						
			logger.info("Waiting 5 sec");
			TimeUtil.waiting(5);
		
			assertTrue(response.isInstructionResultOK());
						
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	public void testUIClientStopServerDaemon() {
		try {
			
			JityRequest request = new JityRequest();
			request.setInstructionName("SHUTDOWNSERVER");
			
			// Load config file
			UIClientConfig clientConfig = UIClientConfig.getInstance();
			logger.info("Reading configuration file.");
			clientConfig.initialize();
			logger.info("Configuration File successfully loaded.");
			
			RequestSender requestSender = new RequestSender();
			requestSender.openConnection(clientConfig.getSERVER_HOSTNAME(), clientConfig.getSERVER_PORT());
			JityResponse response = requestSender.sendRequest(request);
			requestSender.closeConnection();
			
			if (!response.isInstructionResultOK())
				throw new Exception(response.getExceptionMessage());
						
			logger.info("Waiting 5 sec");
			TimeUtil.waiting(5);
		
			assertTrue(response.isInstructionResultOK());
						
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testIsNotRunning() {
		assertFalse(Server.getInstance().isRunning());
	}
	
}
