package org.jity.server;

import org.jity.UIClient.UIClientConfig;
import org.jity.UIClient.UIClientException;
import org.jity.common.TimeUtil;
import org.jity.protocol.JityRequest;
import org.jity.protocol.JityResponse;
import org.jity.protocol.RequestSender;
import org.jity.server.planifDaemon.PlanifDaemon;

public class ShutdownServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			JityRequest request = new JityRequest();
			request.setInstructionName("SHUTDOWNSERVER");

			// Load config file
			UIClientConfig clientConfig = UIClientConfig.getInstance();
			clientConfig.initialize();
			
			RequestSender requestSender = new RequestSender();
			requestSender.openConnection(clientConfig.getSERVER_HOSTNAME(), clientConfig.getSERVER_PORT());
			JityResponse response = requestSender.sendRequest(request);
			requestSender.closeConnection();
			
			if (!response.isInstructionResultOK()) {
				throw new Exception(response.getExceptionMessage());
			}

		} catch (UIClientException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
