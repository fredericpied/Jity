package org.jity.tests;

import org.apache.log4j.Logger;
import org.jity.agent.Agent;
import org.jity.agent.AgentException;
import org.jity.common.TimeUtil;

import junit.framework.TestCase;

public class TestAgent extends TestCase {
	private static final Logger logger = Logger.getLogger(TestAgent.class);
		
	public void testStartAgentDaemon() {
		try {

			Agent.getInstance().startAgentDaemon();

			logger.info("Waiting 15 sec");
			TimeUtil.waiting(15);
			
		} catch (AgentException e) {
			e.printStackTrace();
			System.exit(1);
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
			Agent.getInstance().stopAgentDaemon();
			
			logger.info("Waiting 5 sec");
			TimeUtil.waiting(5);
			
		} catch (AgentException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testIsNotRunning() {
		assertFalse(Agent.getInstance().isRunning());
	}
	
}
