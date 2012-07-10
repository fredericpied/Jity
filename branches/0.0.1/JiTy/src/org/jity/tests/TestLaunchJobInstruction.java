package org.jity.tests;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.DataException;
import org.jity.UIClient.UIClient;
import org.jity.agent.Agent;
import org.jity.agent.AgentException;
import org.jity.common.TestUtil;
import org.jity.common.XMLUtil;
import org.jity.planifEngine.PlanifEngine;
import org.jity.referential.persistent.Calendar;
import org.jity.referential.persistent.Job;
import org.jity.server.Server;
import org.jity.server.ServerException;
import org.jity.server.database.Database;
import org.jity.server.protocol.JityRequest;
import org.jity.server.protocol.JityResponse;

import junit.framework.TestCase;

public class TestLaunchJobInstruction extends TestCase {
	private static final Logger logger = Logger.getLogger(TestLaunchJobInstruction.class);

	public void setUp() {
		try {

			Agent.getInstance().startAgentDaemon();

			TestUtil.waiting(5);
			
		} catch (AgentException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void tearDown() {

		try {
			Agent.getInstance().stopAgentDaemon();
			
			TestUtil.waiting(5);
			
		} catch (AgentException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void testLaunchJob() {
		
		try {
			
			Job job = new Job();
			job.setName("jobtest");
			job.setCommandPath("d:\\temp\\test.bat");
			
			String xmlJob = XMLUtil.objectToXMLString(job);
			
			JityRequest request = new JityRequest();
			request.setInstructionName("LAUNCHJOB");
			request.setXmlInputData(xmlJob);
			
			PlanifEngine planifEngine = PlanifEngine.getInstance();
			JityResponse response = planifEngine.sendRequest(request);
			
			if (!response.isInstructionResultOK()) {
				throw new Exception(response.getExceptionMessage());
			} else {
				logger.info("Response: "+response.getXmlOutputData());
			}
			assertTrue(response.isInstructionResultOK());
	
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
}
