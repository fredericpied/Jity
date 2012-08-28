package org.jity.tests;

import org.apache.log4j.Logger;
import org.jity.UIClient.UIClientConfig;
import org.jity.agent.Agent;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.RequestSender;
import org.jity.common.referential.PersonnalCalendarException;
import org.jity.common.util.TimeUtil;
import org.jity.server.Server;
import org.jity.server.database.DatabaseException;

import junit.framework.TestCase;

public class TestServer extends TestCase {
	private static final Logger logger = Logger.getLogger(TestServer.class);
		
	private void startServerInThread() throws InterruptedException {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Server.getInstance().startServer();
					TimeUtil.waiting(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();

		TimeUtil.waiting(3);
	}
	
	private void startAgentInThread() throws InterruptedException {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Agent.getInstance().startAgent();
					TimeUtil.waiting(2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();

		TimeUtil.waiting(3);
	}
	
	public void testStartServerDaemon() {
		try {

			startServerInThread();
			
			AddDBDataForTest.launch();

			TimeUtil.waiting(3);
			
			startAgentInThread();

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
			request.setInstructionName("STARTEXECMANAGER");
			
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
						

		
			assertTrue(response.isInstructionResultOK());

			TimeUtil.waiting(30);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testUIClientStopPlanifDaemon() {
		try {
			
			JityRequest request = new JityRequest();
			request.setInstructionName("SHUTDOWNEXECMANAGER");
			
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
