package org.jity.server;

import org.jity.common.util.TimeUtil;
import org.jity.server.planifDaemon.PlanifDaemon;

public class StartServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			Server.getInstance().startServerDaemon();

			TimeUtil.waiting(5);
			
			// Start the planification daemon
			if (Server.getInstance().isRunning()) PlanifDaemon.getInstance().startPlanifDaemon();
			
		} catch (ServerException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
