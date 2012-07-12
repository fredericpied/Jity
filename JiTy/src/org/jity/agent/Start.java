package org.jity.agent;

public class Start {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			Agent.getInstance().startAgentDaemon();
			
		} catch (AgentException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}
