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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.jity.agent.commandExecutor.ErrorOutputLogger;
import org.jity.agent.commandExecutor.StandardOutputLogger;
import org.jity.agent.commandExecutor.CommandExecutor;
import org.jity.common.DateUtil;
import org.jity.common.XMLUtil;
import org.jity.protocol.JityResponse;
import org.jity.referential.Job;
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

			// Setting Job with XML flow
			Job job = (Job)XMLUtil.XMLStringToObject(xmlInputData);

			// TimeStamp for log files
			DateFormat dateFormat = new SimpleDateFormat(DateUtil.DEFAULT_TIMESTAMP_FORMAT);
			String timestamp = dateFormat.format(new Date());
			File jobLogFile = new File("d:\\temp\\"+job.getName()+"_"+timestamp+".log");
			
			Layout layout = new org.apache.log4j.PatternLayout("%d{yyyyMMdd HH:mm:ss} %-5p %c{1}: %m%n");
			Appender jobLogger = new org.apache.log4j.FileAppender(layout, jobLogFile.getAbsolutePath() , true); 

			try {
				cmdExecutor.setOutputLogDevice(new StandardOutputLogger(jobLogger));			
				cmdExecutor.setErrorLogDevice(new ErrorOutputLogger(jobLogger));
			} catch (IOException e) {
				response.setException(e);
			}
			
			
			String commandLine = job.getCommandPath();
			
			//cmdExecutor.setWorkingDirectory(workingDirectory);
			
			logger.info("Launching job "+job.getName()+" (job log file: "+jobLogFile.getAbsolutePath()+")");
			
			// Running command
			exitStatus = cmdExecutor.runCommand(commandLine);
			
			logger.info("End of "+job.getName()+"(exit status: "+exitStatus+")");

			response.setInstructionResultOK(true);
			response.setXmlOutputData(XMLUtil.objectToXMLString(exitStatus));
			
//			if (cmdExecutor.getCommandError().length() != 0) {
//				logger.info("-- BAD END OF JOB "+job.getName()+"(Error output not empty) --");
//				exitStatus = 1;
//			}
									


		} catch (Exception e) {
			response.setException(e);
		}
	
		return response;
	}
	
}
