package org.jity.server.instructions.admin;

import java.util.ArrayList;

import org.jity.server.Server;
import org.jity.server.ServerException;
import org.jity.server.protocol.Response;

/**
 * Server command to shutdown the OJS server
 * @author 09344A
 *
 */
public abstract class ShutdownServer {

	public static Response launch(ArrayList<String> parameters) {
		Response response = new Response();
		
		try {
			Server.getInstance().shutdownServer();
			response.setInstructionResult("OK");

		} catch (ServerException e) {
			response.parseException(e);
		}

		return response;
	}
	

}
