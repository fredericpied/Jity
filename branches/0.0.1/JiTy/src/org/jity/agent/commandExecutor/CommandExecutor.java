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
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Execute the job command path on the agent host and strean standard and error output 
 * @author Fred
 *
 */
public class CommandExecutor {
	private static final Logger logger = Logger.getLogger(CommandExecutor.class);
	
	private LogDevice outputLogDevice = null;
	private LogDevice errorLogDevice = null;
	
	private File workingDirectory = null;
	
	private ArrayList<EnvVar> envVarList = null;

	private StringBuffer cmdOutput = null;
	private StringBuffer cmdError = null;

	private AsynchronousOutputReader cmdOutputThread = null;
	private AsynchronousOutputReader cmdErrorThread = null;

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
	public int runCommand(String commandLine) throws Exception {
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
}
