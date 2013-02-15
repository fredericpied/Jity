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
 */package org.jity.server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.RequestSender;
import org.jity.common.referential.ExecTask;
import org.jity.common.referential.Job;
import org.jity.common.referential.dateConstraint.DateConstraintException;
import org.jity.common.referential.timeConstraint.TimeConstraintException;
import org.jity.common.util.TimeUtil;
import org.jity.common.util.XMLUtil;
import org.jity.server.database.HibernateSessionFactory;

/**
 * Pool jobs constraints and create execTask for agents. Get task status to update state in DB
 *
 */
public class ServerTaskLauncherDaemon implements Runnable {
	private static final Logger logger = Logger.getLogger(ServerTaskLauncherDaemon.class);

	private static ServerTaskLauncherDaemon instance = null;
	
	private Session databaseSession;
		
    private Thread daemon = null;

    private boolean shutdownAsked = false;

    private Date exploitDate;
    
    /**
     * Return exploitation date current value
     * @return Date
     */
    public Date getExploitDate() {
    	return this.exploitDate;
    }
    
    /**
     * Initialize exploitDate with current Date
     */
    public void initializeExploitDateWithCurrentDate() {
    	this.exploitDate = getCurrentDate();
    }
    
    /**
     * Return current day Date (whithout hour, minutes, seconds ans milliseconds)
     * @return Date
     */
    private static Date getCurrentDate() {

    	Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
    }
    
    
    /**
     * Return current instance of ServerExecManager.
     * Create one if none
     * @return ServerExecManager
     */
	public static ServerTaskLauncherDaemon getInstance() {
		if (instance == null) {
			instance = new ServerTaskLauncherDaemon();
		}
		return instance;
	}
	
	/**
	 * Return true if ExecManager is running
	 * @return boolean
	 */
	public synchronized boolean isRunning() {
		if (this.daemon != null)
			return this.daemon.isAlive();
		else
			return false;
	}
	
    /**
     * Start the taskManager in a Thread.
     */
    public synchronized void start() {
        if (daemon == null) {
            daemon = new Thread(this);
            daemon.start();
        }
    }

    /**
     * Stop current ExecManager if running.
     */
    public synchronized void stop() {
        if (daemon != null) {
        	logger.info("Shutdown of "+this.getClass().getSimpleName()+" asked.");
            shutdownAsked = true;
    		this.databaseSession.close();
            daemon.interrupt();
            daemon = null;
			logger.info(this.getClass().getSimpleName()+" successfuly shutdowned");
        }
    }
    
    
    /**
     * Return a List of Job candidate for execute (actived = true and not allready in execution)
     * @return List<Job>
     */
    private List<Job> getJobsToExecuteList() {

    	SQLQuery query2 = this.databaseSession.createSQLQuery("SELECT JOB.* FROM JOB"
    			+" WHERE JOB.IS_ENABLE = TRUE"
    			+" AND JOB.ID NOT IN (SELECT JOB_ID FROM EXEC_TASK"
    			+" WHERE EXEC_TASK.STATUS IN (3,4,5,6))").addEntity(Job.class);
    	
    	List<Job> jobList = query2.list();
				
		logger.debug("Found "+jobList.size()+" jobs in database");

		return jobList;
    }
    
    
    /**
     * Analyse all jobs in the referential to check constraints and manage their
     * state.
     * @throws DatabaseException 
     */
    private void analyzeJobsForExecute() {
		
    	int jobLaunched = 0;
    	
    	// Get job list
		Iterator<Job> iterJobList = getJobsToExecuteList().iterator();
		while (iterJobList.hasNext()) {
			Job job = (Job) iterJobList.next();
						
			try {

				if (job.checkConstraint(this.exploitDate)) {
					submitJobToAgent(job);
					jobLaunched++;
				}
					
			} catch (DateConstraintException e) {
				logger.warn("Job "+job.getName()+": "+e.getMessage());
			} catch (TimeConstraintException e) {
				logger.warn("Job "+job.getName()+": "+e.getMessage());
			}			
		}
		
		logger.debug(jobLaunched +" jobs submits to agent");
		
    }
    
    
    /**
     * Submit a job to agent
     * @param job
     */
	private void submitJobToAgent(Job job) {
		
		// Initialize ExecTask object
		ExecTask execTask = new ExecTask();
		execTask.setJob(job);
		execTask.setExecDate(getCurrentDate());
		execTask.setStatus(ExecTask.PLANED);
		
		// Saving ExecTask state
		Transaction transaction = this.databaseSession.beginTransaction();
		this.databaseSession.save(execTask);
		transaction.commit();
		
		// Construct Request
		JityRequest request = new JityRequest();
		request.setInstructionName("ADDTASKINQUEUE");
		request.setXmlInputData(XMLUtil.objectToXMLString(execTask));

		try {
			RequestSender requestLauncher = new RequestSender();

			requestLauncher.openConnection(job.getHostName(),
					job.getHostPort());

			// Send request to agent
			JityResponse response = requestLauncher.sendRequest(request);

			requestLauncher.closeConnection();

			if (response.isInstructionResultOK()) {
				// If OK, update current ExecTask state
				ExecTask receivedExecTask = (ExecTask)XMLUtil.XMLStringToObject(response.getXmlOutputData());
				execTask.setStatusMessage(receivedExecTask.getStatusMessage());
				execTask.setStatus(receivedExecTask.getStatus());
			} else {
				// If response is KO
				execTask.setBegin(new Date());
				execTask.setEnd(new Date());
				execTask.setStatus(ExecTask.KO);
				execTask.setStatusMessage("Cannot add in queue "+response.getExceptionMessage());
			}
			
		} catch (UnknownHostException e) {
			logger.warn("Job "+job.getName()+" on "+job.getHostName()+": " +e.getMessage());
			execTask.setBegin(new Date());
			execTask.setEnd(new Date());
			execTask.setStatus(ExecTask.KO);
			execTask.setStatusMessage(e.getClass().getName()+": "+e.getMessage());
		} catch (IOException e) {
			logger.warn("Job "+job.getName()+" on "+job.getHostName()+": " +e.getMessage());
			execTask.setEnd(new Date());
			execTask.setStatus(ExecTask.KO);
			execTask.setStatusMessage(e.getClass().getName()+": "+e.getMessage());
		}

		logger.debug("Exec task "+execTask.getId()+" save to db");
		
		// Saving ExecTask state to DB
		transaction = this.databaseSession.beginTransaction();
		this.databaseSession.save(execTask);
		transaction.commit();
		
	}
	
	/**
	 * Update tasks status in DB by pooling agent
	 */
	public void updateTasksStatusForDMZ() {
				
		ArrayList<AgentHostToPool> agentsList = new ArrayList<AgentHostToPool>();

		// Finding ExecTask whith status IN_QUEUE
		String queryFind = "select execTask from org.jity.common.referential.ExecTask execTask"
			+ " where execTask.status = "+ExecTask.IN_QUEUE;

		List<ExecTask> execTaskList = this.databaseSession.createQuery(queryFind).list();
		
		logger.debug("Found "+execTaskList.size()+" in_queue tasks");
		
		// Create hostnameList to pool for concerned ExecTask
		Iterator<ExecTask> iterExecTaskList = execTaskList.iterator();
		while (iterExecTaskList.hasNext()) {
			ExecTask execTask = iterExecTaskList.next();
			
			AgentHostToPool agent = new AgentHostToPool();
			agent.hostName = execTask.getJob().getHostName();
			agent.hostPort = execTask.getJob().getHostPort();
			
			if (! agentsList.contains(agent)) agentsList.add(agent);
		}
		
		logger.debug("Found "+agentsList.size()+" agents to pool");
		
		// For each hostname, getTasksStatus
		Iterator<AgentHostToPool> iterAgentsList = agentsList.iterator();
		while (iterAgentsList.hasNext()) {
			AgentHostToPool agent = iterAgentsList.next();
			
			// Construct Request
			JityRequest request = new JityRequest();
			request.setInstructionName("GETTASKSTATUS");
			
			logger.debug("Pooling "+agent.hostName+" ("+agent.hostPort+")...");
			
			RequestSender requestLauncher = new RequestSender();
			
			try {
				requestLauncher.openConnection(agent.hostName, agent.hostPort);

				JityResponse response = requestLauncher.sendRequest(request);

				requestLauncher.closeConnection();

				logger.debug("GETTASKSTATUS response: "+response.getXmlOutputData());
				
				// If response is OK
				if (response.isInstructionResultOK()) {
					
					ArrayList<ExecTask> taskQueueExtract =
						(ArrayList<ExecTask>)XMLUtil.XMLStringToObject(response.getXmlOutputData());

					logger.debug("execTasks to get for this agent: "+taskQueueExtract.size());
					
					Iterator<ExecTask> iterTaskQueueExtract = taskQueueExtract.iterator();
					while (iterTaskQueueExtract.hasNext()) {
						ExecTask receviedExecTask = iterTaskQueueExtract.next();
						
						// Saving ExecTask state
						Transaction transaction = this.databaseSession.beginTransaction();
						this.databaseSession.merge(receviedExecTask);
						transaction.commit();
						
						logger.debug("Exec task "+receviedExecTask.getId()+" updated in db");
					}
					
				} else {
					logger.warn("Can't get tasks status for agent "+agent.hostName+" ("+agent.hostPort+")");
					//TODO must update ExecTask
					
				}
				
			} catch (UnknownHostException e) {
				logger.warn("Can't get tasks status for agent "+agent.hostName+" ("+agent.hostPort+")");
			} catch (IOException e) {
				logger.warn("Can't get tasks status for agent "+agent.hostName+" ("+agent.hostPort+")");
			}
		
		}
		
	}
	
	
    /**
     * Launch jobs analyse and tasks status update each X seconds.
     */
    public void run() {
        int cycle = ServerConfig.getInstance().getSERVER_POOLING_CYCLE();

        this.databaseSession = HibernateSessionFactory.getInstance().getSession();
        
		logger.info("Starting "+this.getClass().getSimpleName());
		
		if (this.getExploitDate() == null) this.initializeExploitDateWithCurrentDate();
		
        while (!shutdownAsked) {
            try {
                analyzeJobsForExecute();
                TimeUtil.waiting(cycle);
                //TimeUtil.waiting(cycle/2);
                //updateTasksStatusForDMZ();
                //TimeUtil.waiting(cycle/2);
            } catch (InterruptedException ex) {
            	if (!shutdownAsked)  {
            		logger.warn(this.getClass().getSimpleName()+" is stopped.");
            		logger.debug(ex.toString());
            	}
                
            } catch (Exception ex) {
                logger.fatal("Error during checkJobs: " + ex.getClass().getSimpleName()+": "+ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }
        
    }
    
    private class AgentHostToPool {
    	String hostName;
    	int hostPort;
    }
    
}