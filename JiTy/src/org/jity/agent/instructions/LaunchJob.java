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
package org.jity.agent.instructions;

import java.io.File;

import org.apache.log4j.Logger;
import org.jity.agent.Agent;
import org.jity.agent.commandExecutor.ErrorOutputLogger;
import org.jity.agent.commandExecutor.StandardOutputLogger;
import org.jity.agent.commandExecutor.CommandExecutor;
import org.jity.common.XMLUtil;
import org.jity.protocol.JityResponse;
import org.jity.referential.persistent.Job;
import org.jity.server.instructions.Instruction;

/**
 * Server commande to find something to launch for a client
 * @author 09344A
 *
 */
public class LaunchJob implements Instruction {
	private static final Logger logger = Logger.getLogger(LaunchJob.class);
	
	public JityResponse launch(String xmlInputData) {
		JityResponse response = new JityResponse();
				
		try {
			int exitStatus = -1;
			
			CommandExecutor cmdExecutor = new CommandExecutor();
			cmdExecutor.setOutputLogDevice(new StandardOutputLogger());			
			cmdExecutor.setErrorLogDevice(new ErrorOutputLogger());
			
			// Setting Job with XML flow
			Job job = (Job)XMLUtil.XMLStringToObject(xmlInputData);
			
			String commandLine = job.getCommandPath();
			
			//cmdExecutor.setWorkingDirectory(workingDirectory);
			
			logger.info("-- START OF JOB "+job.getName()+" --");
			
			// Running command
			exitStatus = cmdExecutor.runCommand(commandLine);
			
			if (exitStatus != 0) {
				logger.info("-- BAD END OF JOB "+job.getName()+"(Return code <> 0) --");
				throw new Exception("Exit status = "+exitStatus);
			}
			
			if (cmdExecutor.getCommandError().length() != 0) {
				logger.info("-- BAD END OF JOB "+job.getName()+"(Error output not empty) --");
				throw new Exception(cmdExecutor.getCommandOutput());
			}
									
			logger.info("-- GOOD END OF JOB "+job.getName()+" --");
			if (exitStatus == 0) {
				response.setInstructionResultOK(true);
				response.setXmlOutputData(XMLUtil.objectToXMLString(exitStatus));
			}

		} catch (Exception e) {
			response.setException(e);
		}
	
		return response;
	}
	
}
