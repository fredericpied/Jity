package org.jity.tests;

import org.apache.log4j.Logger;
import org.jity.UIClient.UIClientException;
import org.jity.agent.Agent;
import org.jity.agent.AgentException;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.RequestSender;
import org.jity.common.util.TimeUtil;

import junit.framework.TestCase;

public class TestAgent extends TestCase {
	private static final Logger logger = Logger.getLogger(TestAgent.class);
	
	private void startAgentInThread() throws InterruptedException {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Agent.getInstance().start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();

		TimeUtil.waiting(3);
	}
	
	public void testStartAgentDaemon() {
		try {

			startAgentInThread();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testIsRunning() {
		assertTrue(Agent.getInstance().isRunning());
	}
	
	
	public void testStopAgentDaemon() {
		try {
			
			Agent.getInstance().stop();
			
		} catch (AgentException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testIsNotRunning() {
		assertFalse(Agent.getInstance().isRunning());
	}
	
	
	public void testStartAgentDaemon2() {
		try {

			startAgentInThread();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testServerShutdownAgent() {
		try {
			
			JityRequest request = new JityRequest();
			request.setInstructionName("SHUTDOWNAGENT");
			
			RequestSender requestLauncher = new RequestSender();
			requestLauncher.openConnection("localhost", 2611);
			JityResponse response = requestLauncher.sendRequest(request);
			requestLauncher.closeConnection();
			
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
		
}
