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
package org.jity.server.instructions.admin;

import org.jity.common.protocol.Instruction;
import org.jity.common.protocol.JityResponse;
import org.jity.server.ServerTaskLauncherDaemon;

/**
 * Server command to shutdown the Server Task Manager
 * @author Fred
 *
 */
public class ShutdownServerTaskLauncherDaemon implements Instruction {

	public JityResponse launch(String xmlInputData) {
		JityResponse response = new JityResponse();
		
		ServerTaskLauncherDaemon.getInstance().stop();
		response.setInstructionResultOK(true);

		return response;
	}
	

}