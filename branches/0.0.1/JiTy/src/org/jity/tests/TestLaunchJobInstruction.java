package org.jity.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jity.agent.Agent;
import org.jity.agent.AgentConfig;
import org.jity.agent.AgentException;
import org.jity.common.TimeUtil;
import org.jity.common.XMLUtil;
import org.jity.protocol.JityRequest;
import org.jity.protocol.JityResponse;
import org.jity.protocol.RequestSender;
import org.jity.referential.Job;
import org.jity.server.ServerConfig;

import junit.framework.TestCase;

public class TestLaunchJobInstruction extends TestCase {
	private static final Logger logger = Logger.getLogger(TestLaunchJobInstruction.class);

	public void setUp() {
		try {

			Agent.getInstance().startAgentDaemon();
			
			logger.info("Waiting 5 sec");
			TimeUtil.waiting(5);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void tearDown() {

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

	public void testLaunchJob() {
		
		try {
			
			Job job = new Job();
			job.setName("jobtest");
			job.setCommandPath("d:\\temp\\test.bat");
			job.setHostName("localhost");
		
			// Construct Request
			JityRequest request = new JityRequest();
			request.setInstructionName("LAUNCHJOB");
			request.setXmlInputData(XMLUtil.objectToXMLString(job));
			
			RequestSender requestLauncher = new RequestSender();
			
			requestLauncher.openConnection(job.getHostName(), 
					ServerConfig.getInstance().getAGENT_PORT());
			
			JityResponse response = requestLauncher.sendRequest(request);
			
			requestLauncher.closeConnection();
			
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
