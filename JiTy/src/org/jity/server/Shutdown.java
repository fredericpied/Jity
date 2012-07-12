package org.jity.server;

import org.jity.UIClient.UIClient;
import org.jity.UIClient.UIClientException;
import org.jity.common.TimeUtil;
import org.jity.protocol.JityRequest;
import org.jity.protocol.JityResponse;
import org.jity.server.planifDaemon.PlanifDaemon;

public class Shutdown {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			JityRequest request = new JityRequest();
			request.setInstructionName("SHUTDOWNSERVER");

			UIClient client = UIClient.getInstance();

			JityResponse response = client.sendRequest(request);
			
			client.closeConnection();
			
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
