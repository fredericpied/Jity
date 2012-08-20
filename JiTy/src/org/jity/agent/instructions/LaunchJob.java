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
import org.jity.agent.AgentConfig;
import org.jity.agent.AgentException;
import org.jity.agent.commandExecutor.ErrorOutputLogger;
import org.jity.agent.commandExecutor.StandardOutputLogger;
import org.jity.agent.commandExecutor.CommandExecutor;
import org.jity.common.DateUtil;
import org.jity.common.XMLUtil;
import org.jity.protocol.JityResponse;
import org.jity.referential.ExecStatus;
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
			// Initializing exitStatus
			int exitStatus = -1;

			// Setting Job with XML flow
			Job job = (Job)XMLUtil.XMLStringToObject(xmlInputData);

			// Get Log files directory from Agent Config
			if (AgentConfig.getInstance().getJOBS_LOGS_DIR().trim().length() == 0)
				throw new AgentException("Parameters \"JOBS_LOGS_DIR\" not set for this agent");
			
			// Can't acces to log files directory
			File logDir = new File(AgentConfig.getInstance().getJOBS_LOGS_DIR());
			if (! logDir.isDirectory() || ! logDir.exists())
				throw new AgentException("Unable to write job logs \"JOBS_LOGS_DIR\" ("+logDir.getAbsolutePath()+")");
			
			// Initializing log file name whith timestamp
			DateFormat dateFormat = new SimpleDateFormat(DateUtil.DEFAULT_TIMESTAMP_FORMAT);
			String timestamp = dateFormat.format(new Date());
			File jobLogFile = new File(logDir.getAbsolutePath()+File.separator+"LOG_"+job.getName()+"_"+timestamp+".log");
			
			// Create a specific log4j logger for this execution
			Layout layout = new org.apache.log4j.PatternLayout("%d{yyyyMMdd HH:mm:ss} %c{1}: %m%n");
			Appender jobLoggerAppender = new org.apache.log4j.FileAppender(layout, jobLogFile.getAbsolutePath() , true); 
			Logger jobLogger = Logger.getLogger(job.getName());
			jobLogger.setAdditivity(false);
			jobLogger.addAppender(jobLoggerAppender);
			
			CommandExecutor cmdExecutor = new CommandExecutor();

			// initializing output loggers
			cmdExecutor.setOutputLogDevice(new StandardOutputLogger(jobLogger));			
			cmdExecutor.setErrorLogDevice(new ErrorOutputLogger(jobLogger));
			
			//TODO cmdExecutor.setWorkingDirectory(workingDirectory);
			
			logger.info("Launching job "+job.getName()+" (job log file: "+jobLogFile.getAbsolutePath()+")");
			
			// Running command
			exitStatus = cmdExecutor.runCommand(job.getCommandPath());
			
			logger.info("End of "+job.getName()+"(exit status: "+exitStatus+")");

			// Setting execStatus for returning to Server
			ExecStatus execStatus = new ExecStatus();
			execStatus.setStatus(exitStatus);
			execStatus.setLogFile(jobLogFile.getAbsolutePath());
			
			response.setInstructionResultOK(true);
			response.setXmlOutputData(XMLUtil.objectToXMLString(execStatus));

		} catch (Exception e) {
			response.setException(e);
		}
	
		return response;
	}
	
}
