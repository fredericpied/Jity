/**
 *  JiTy : Open Job Scheduler
 *  Copyright (C) 2012 
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free
 *  Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA 02111-1307, USA
 *
 *  For questions, suggestions:
 *
 *  http://www.assembla.com/spaces/jity
 *
 */
package org.jity.server.instructions;

import java.util.ArrayList;

import org.jity.common.protocol.Instruction;
import org.jity.common.protocol.JityResponse;
import org.jity.server.Server;
import org.jity.server.ServerException;

/**
 * Server command to shutdown the JiTy server
 * @author 09344A
 *
 */
public class ShutdownServer implements Instruction {

	public JityResponse launch(String xmlInputData) {
		JityResponse response = new JityResponse();
		
		try {
			Server.getInstance().stop();
			response.setInstructionResultOK(true);

		} catch (ServerException e) {
			response.setException(e);
		}

		return response;
	}
	

}
