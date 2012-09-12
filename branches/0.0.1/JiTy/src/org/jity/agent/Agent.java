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
package org.jity.agent;

import org.apache.log4j.Logger;
import org.jity.common.protocol.RequestReceiver;
import org.jity.common.referential.ExecTask;
import org.jity.common.util.TimeUtil;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The JiTy Agent run on host to execute task
 * @author Fred
 *
 */
public class Agent {
	private static final Logger logger = Logger.getLogger(Agent.class);

	private static Agent instance = null;

	private ServerSocket listenSocket;
	
	private boolean isRunning = false;
	
	private boolean shutdownning = false;

	/**
     * taskQueue (Synchronized)
     */
    private List<ExecTask> taskQueue = Collections.synchronizedList(new ArrayList());
	
    private int currentJobsExection = 0;
    
	/**
	 * Return the current instance of Agent (if none, create one)
	 * @return Agent
	 */
	public static Agent getInstance() {
		if (instance == null) {
			instance = new Agent();
		}
		return instance;
	}
	
    /**
     * Return taskQueue
     * @return List<ExecTask>
     */
    public List<ExecTask> getTaskQueue() {
    	return this.taskQueue;
    }
     
    /**
     * Return current numbers of tasks in queue
     * @return int
     */
    public int getCurrentNumTaskInQueue() {
    	return this.taskQueue.size();
    }
	
    /**
     * Add a task to execute in queue
     * @param ExecTask
     * @throws AgentException 
     */
    public void addTaskInQueue(ExecTask execTask) throws AgentException {
    			
		int maxJobsInQueue = AgentConfig.getInstance().getMAX_JOBS_IN_QUEUE();
		
		if (Agent.getInstance().getCurrentNumTaskInQueue() >=
			maxJobsInQueue) {
			throw new AgentException("Max number of tasks in queue reached ("+
					maxJobsInQueue+")");
		}
    	
    	logger.debug("Adding job "+execTask.getJob().getName()+" to execution queue");

    	synchronized(Agent.getInstance().getTaskQueue()) {
    		Agent.getInstance().getTaskQueue().add(execTask);
    	}
    }
    
    public void incrementCurrentJobsExecution() {
    	this.currentJobsExection++;
    }
    
    public void decrementCurrentJobsExecution() {
    	this.currentJobsExection--;
    }
    
    public int getCurrentJobsExecution() {
    	return this.currentJobsExection;
    }
    
    
	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * Start the agent
	 * @throws InterruptedException 
	 * 
	 */
	public void start() {
		Socket socket = null;
		
		shutdownning = false;
		
		logger.info("JiTy Agent starting process.");

		// Loading config File
		logger.info("Loading Agent configuration file.");
		try {
			this.loadConfigFile();
		} catch (AgentException e1) {
			logger.fatal(e1.getMessage());
			System.exit(1);
		}
	
		try {
			String localHostname = java.net.InetAddress.getLocalHost().getHostName();
			
			logger.info("Starting agent on "+localHostname+" ("+this.getOSName()+")...");
			
		} catch (UnknownHostException e1) {
			logger.warn(e1.getMessage());
			logger.info("Starting agent...");
		}

		int agentPort = AgentConfig.getInstance().getAGENT_INPUT_PORT();
		try {
			listenSocket = new ServerSocket(agentPort);
			logger.info("Agent running on port " + agentPort);

		} catch (IOException e) {
			logger.fatal(e.getMessage());
			System.exit(1);
		}


		// Get Log files directory from Agent Config
		if (AgentConfig.getInstance().getJOBS_LOGS_DIR().trim().length() == 0) {
			logger.fatal("Parameters \"JOBS_LOGS_DIR\" not set for this agent");
			System.exit(1);
		}
		
		// Can't acces to log files directory
		File logDir = new File(AgentConfig.getInstance().getJOBS_LOGS_DIR());
		if (! logDir.isDirectory() || ! logDir.exists()) {
			logger.fatal("Unable to write job logs into \"JOBS_LOGS_DIR\" ("+logDir.getAbsolutePath()+")");
			System.exit(1);
		}

		AgentQueueManager.getInstance().start();
		try {
			TimeUtil.waiting(1);
		} catch (InterruptedException e1) { }

		AgentTaskStatusManager.getInstance().start();
		try {
			TimeUtil.waiting(1);
		} catch (InterruptedException e1) { }
		
		try {

			isRunning = true;
			
			logger.info("JiTy Agent successfully started.");
			
			while (true) {
				socket = listenSocket.accept();
				try {
					String serverHostname = socket.getInetAddress().getHostName();
					
					logger.debug("New connection from "+serverHostname+" ("
							+ socket.getInetAddress() + ").");

					new RequestReceiver(socket);
				} catch (IOException e) {
					socket.close();
				}
			}

		} catch (IOException e) {
			if (!shutdownning) logger.warn("Problem during client connection.");
			logger.debug(e.getMessage());
		} finally {
			isRunning = false;
			try {
				if (listenSocket != null)
					listenSocket.close();
			} catch (IOException e) {
				logger.warn("Failed to close client connection.");
				logger.debug(e.getMessage());
			}
			logger.info("JiTy Agent shutdowned correctly.");
		}
	}

	/**
	 * Stop current agent if running.
	 * 
	 * @throws AgentException
	 */
	public void stop() throws AgentException {
		if (this.isRunning) {
			logger.info("Shutdown of agent asked.");
			shutdownning = true;
			
			try {

				AgentQueueManager.getInstance().stop();
				
				AgentTaskStatusManager.getInstance().stop();
				
				logger.info("Closing Network socket.");

				listenSocket.close();

				isRunning = false;
				
				logger.info("Agent successfuly shutdowned.");

			} catch (IOException e) {
				throw new AgentException("Shutdown of agent failed.");
			}
		} else {
			throw new AgentException("Agent is not running");
		}

	}
		
	/**
	 * return type of OS supporting the agent
	 * @return String
	 */
	public String getOSName() {
		return System.getProperty("os.name");
	}
	
	/**
	 * Loading the agent config file
	 * 
	 * @throws AgentException
	 */
	private void loadConfigFile() throws AgentException {
		try {
			AgentConfig agentConfig = AgentConfig.getInstance();
			logger.info("Reading configuration file.");
			agentConfig.initialize();
			logger.info("Configuration File successfully loaded.");
			agentConfig.showConfig();
		} catch (IOException e) {
			throw new AgentException("Failed to read configuration file ("
					+ e.getMessage() + ").");
		}
	}

}
