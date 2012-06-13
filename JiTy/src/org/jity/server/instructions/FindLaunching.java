package org.jity.server.instructions;

import java.util.ArrayList;

import org.jity.server.protocol.Response;

/**
 * Server commande to find something to launch for a client
 * @author 09344A
 *
 */
public abstract class FindLaunching {

	public static Response launch(ArrayList<String> parameters) {
		Response response = new Response();
				
		try {
			
			// do the job
			
			response.setInstructionResult("OK");

		} catch (Exception e) {
			response.parseException(e);
		}
		
		
		return response;
	}
	
}
