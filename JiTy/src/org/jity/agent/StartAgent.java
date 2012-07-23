package org.jity.agent;

import org.jity.common.TimeUtil;

public class StartAgent {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			Agent.getInstance().startAgentDaemon();
			
			TimeUtil.waiting(15);
		
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}
