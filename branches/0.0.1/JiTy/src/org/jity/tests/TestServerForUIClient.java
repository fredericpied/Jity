package org.jity.tests;

import org.apache.log4j.Logger;
import org.jity.UIClient.UIClientConfig;
import org.jity.UIClient.UIClientException;
import org.jity.UIClient.swt.MainWindows;
import org.jity.agent.Agent;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.RequestSender;
import org.jity.common.referential.dateConstraint.DateConstraintException;
import org.jity.common.util.TimeUtil;
import org.jity.server.Server;

import junit.framework.TestCase;

public class TestServerForUIClient extends TestCase {
	private static final Logger logger = Logger.getLogger(TestServerForUIClient.class);

	private void startServerInThread() throws InterruptedException {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Server.getInstance().start();
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
					Agent.getInstance().start();
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

			TimeUtil.waiting(2);

			new MainWindows().launch();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (DateConstraintException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void testIsRunning() {
		assertTrue(Server.getInstance().isRunning());
	}
	
	public void testIsNotRunning() {
		assertFalse(Server.getInstance().isRunning());
	}
	
}
