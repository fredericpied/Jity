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
package org.jity.agent.commandExecutor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.jity.agent.AgentConfig;
import org.jity.agent.AgentTaskManager;
import org.jity.common.referential.ExecTask;
import org.jity.common.referential.Job;
import org.jity.common.util.DateUtil;

/**
 * Execute the job command path on the agent host and strean standard and error output 
 * @author Fred
 *
 */
public class CommandExecutor implements Runnable {
	private static final Logger logger = Logger.getLogger(CommandExecutor.class);
	 
	private LogDevice outputLogDevice = null;
	private LogDevice errorLogDevice = null;
	
	private File workingDirectory = null;
	
	private ArrayList<EnvVar> envVarList = null;

	private StringBuffer cmdOutput = null;
	private StringBuffer cmdError = null;

	private AsynchronousOutputReader cmdOutputThread = null;
	private AsynchronousOutputReader cmdErrorThread = null;
	
    private Thread daemon = null;
    
    private ExecTask currentTask;
    
	public void setOutputLogDevice(LogDevice logDevice) {
		this.outputLogDevice = logDevice;
	}

	public void setErrorLogDevice(LogDevice logDevice) {
		this.errorLogDevice = logDevice;
	}

	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public void setEnvironmentVar(String name, String value) {
		if (this.envVarList == null)
			this.envVarList = new ArrayList<EnvVar>();

		this.envVarList.add(new EnvVar(name, value));
	}

	public String getCommandOutput() {
		return this.cmdOutput.toString();
	}

	public String getCommandError() {
		return this.cmdError.toString();
	}

	/**
	 * Launch the command process
	 * @param commandLine
	 * @return exitStatus
	 * @throws Exception
	 */
	private int runCommand(String commandLine) throws Exception {
		// run command
		Process process = runCommandHelper(commandLine);

		// start output and error read threads
		startOutputAndErrorReadThreads(process.getInputStream(), process.getErrorStream());

		// wait for command execution to terminate
		int exitStatus = -1;
		try {
			
			exitStatus = process.waitFor();

		} catch (Throwable ex) {
			throw new Exception(ex.getMessage());
		} finally {
			// notify output and error read threads to stop reading
			notifyOutputAndErrorReadThreadsToStopReading();
		}

		return exitStatus;
	}

	private Process runCommandHelper(String commandLine) throws IOException {
		Process process = null;
		if (this.workingDirectory == null)
			process = Runtime.getRuntime().exec(commandLine, getEnvTokens());
		else
			process = Runtime.getRuntime().exec(commandLine, getEnvTokens(),
					this.workingDirectory);

		return process;
	}

	private void startOutputAndErrorReadThreads(InputStream processOut, InputStream processErr) {
		this.cmdOutput = new StringBuffer();
		this.cmdOutputThread = new AsynchronousOutputReader(processOut, this.cmdOutput,
				this.outputLogDevice, "OUTPUT");
		this.cmdOutputThread.start();

		this.cmdError = new StringBuffer();
		this.cmdErrorThread = new AsynchronousOutputReader(processErr, this.cmdError, 
				this.errorLogDevice, "ERROR");
		this.cmdErrorThread.start();
	}

	private void notifyOutputAndErrorReadThreadsToStopReading() {
		this.cmdOutputThread.stopReading();
		this.cmdErrorThread.stopReading();
	}

	private String[] getEnvTokens() {
		if (this.envVarList == null)
			return null;

		String[] envTokenArray = new String[this.envVarList.size()];
		Iterator<EnvVar> envVarIter = this.envVarList.iterator();
		int nEnvVarIndex = 0;
		while (envVarIter.hasNext() == true) {
			EnvVar envVar = envVarIter.next();
			String envVarToken = envVar.getName() + "=" + envVar.getValue();
			logger.debug("Setting env. variable: "+envVarToken);
			envTokenArray[nEnvVarIndex++] = envVarToken;
		}

		return envTokenArray;
	}
	
	public void execute(ExecTask task) {
		daemon = new Thread(this);
		currentTask = task;
		daemon.start();
	}
	
	public void run() {
		
		Job job = currentTask.getJob();
		
		currentTask.setStatus(ExecTask.RUNNING);
		
		// Initializing exitStatus
		int exitStatus = -1;

		// Initializing log file name whith timestamp
		File logDir = new File(AgentConfig.getInstance().getJOBS_LOGS_DIR());
		DateFormat dateFormat = new SimpleDateFormat(DateUtil.DEFAULT_TIMESTAMP_FORMAT);
		String timestamp = dateFormat.format(new Date());
		File jobLogFile = new File(logDir.getAbsolutePath()+File.separator+"LOG_"+job.getName()+"_"+timestamp+".log");

		try {
		
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
	
			logger.info("Launching job "+job.getName()+" (job log file: "+jobLogFile.getAbsolutePath()+")");
	
			AgentTaskManager.getInstance().incrementCurrentJobsExecution();

			// Running command
			exitStatus = cmdExecutor.runCommand(job.getCommandPath());

			AgentTaskManager.getInstance().decrementCurrentJobsExecution();
			
			logger.info("End of "+job.getName()+"(exit status: "+exitStatus+")");

			// Setting execStatus
			if (exitStatus == 0) {
				currentTask.setStatus(ExecTask.OK);
			} else {
				currentTask.setStatus(ExecTask.KO);
			}
			currentTask.setStatusMessage("Command exit status = "+exitStatus);
			currentTask.setLogFile(jobLogFile.getAbsolutePath());
		
		} catch (Exception e) {
			logger.info("Exception "+e.getClass().getName()+
					" while executing "+job.getName()+"("+e.getMessage()+")");

			// Setting execStatus
			currentTask.setStatus(ExecTask.KO);
			currentTask.setStatusMessage("Exception: "+e.getMessage());
			currentTask.setLogFile(jobLogFile.getAbsolutePath());

		}
		
	}
	
}
