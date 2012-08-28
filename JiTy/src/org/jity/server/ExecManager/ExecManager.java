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
 */package org.jity.server.ExecManager;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.jity.common.util.DateUtil;
import org.jity.common.util.TimeUtil;
import org.jity.common.util.XMLUtil;
import org.jity.server.Server;
import org.jity.server.ServerConfig;
import org.jity.server.ServerException;
import org.jity.server.database.DatabaseException;
import org.jity.server.database.DatabaseServer;

public class ExecManager implements Runnable {
	private static final Logger logger = Logger.getLogger(ExecManager.class);

	private static ExecManager instance = null;
	
	private Session databaseSession;
		
    private Thread daemon = null;

    private boolean shutdownAsked = false;

    private Date exploitDate;
    
    public Date getExploitDate() {
    	if (this.exploitDate == null) this.exploitDate = this.getCurrentDate();
    	return this.exploitDate;
    }
    
    /**
     * Return Date initilaze whith current day Date (whithout hour, minutes, seconds ans milliseconds)
     * @return Date
     */
    private static Date getCurrentDate() {

    	Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
    }
    
    
    
    
	public static ExecManager getInstance() {
		if (instance == null) {
			instance = new ExecManager();
		}
		return instance;
	}
	
	/**
	 * Return true if ExecManager is running
	 * 
	 * @return
	 */
	public synchronized boolean isRunning() {
		if (this.daemon != null)
			return this.daemon.isAlive();
		else
			return false;
	}
	
    /**
     * Start the ExecManager in a Thread.
     */
    public synchronized void startExecManager() {
        if (daemon == null) {
            daemon = new Thread(this);
            daemon.start();
        }
    }

    /**
     * Stop current ExecManager if running.
     */
    public synchronized void stopExecManager() {
        if (daemon != null) {
        	logger.info("Shutdown of ExecManager asked.");
            shutdownAsked = true;
    		this.databaseSession.close();
            daemon.interrupt();
            daemon = null;
			logger.info("ExecManager successfuly shutdowned");
        }
    }
    
    /**
     * Analyse all jobs in the referential to check constraints and manage their
     * state.
     * @throws DatabaseException 
     */
    private void analyzeJobsForExecute() throws DatabaseException {

		String queryFind = "select job from org.jity.common.referential.Job job"
			+ " where job.isActived = true";

		List<Job> jobList = this.databaseSession.createQuery(queryFind).list();
		
		logger.debug("Found "+jobList.size()+" activated jobs in database");
		
		Iterator<Job> iterJobList = jobList.iterator();
		while (iterJobList.hasNext()) {
			Job job = (Job) iterJobList.next();
//
//			String SQLQueryFindExecTask = "SELECT E.ID FROM EXEC_TASK E, JOB J" +
//					" WHERE E.JOB_ID = J.ID"
//					+" AND E.EXEC_DATE = TO_DATE("+this.getExploitDate();
//			
//			
//			SQLQuery s = this.databaseSession.createSQLQuery(SQLQueryFindExecTask)
//				.addEntity("execTask",ExecTask.class)
//				.addJoin("execTask","job");
//			
//			Iterator<ExecTask> iterExecTaskList = execTaskList.iterator();
//			while (iterExecTaskList.hasNext()) {
//				ExecTask e = (ExecTask)iterExecTaskList.next();
//				logger.debug(e.getJob().getName()+" "+e.getExecDate());
//			}
//			if (execTaskList.size() > 0) continue; // If exectask, exist
						
			try {
				if (job.getDateConstraint().isAValidDate(this.getExploitDate()))
					addOneJobForExecute(job);
			} catch (DateConstraintException e) {
				logger.warn("Job "+job.getName()+": "+e.getMessage());
			}			
		}
    }
    
	private void addOneJobForExecute(Job job) {
		
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
		
		RequestSender requestLauncher = new RequestSender();

		try {
			requestLauncher.openConnection(job.getHostName(),
					ServerConfig.getInstance().getAGENT_PORT());

			JityResponse response = requestLauncher.sendRequest(request);

			requestLauncher.closeConnection();

			if (response.isInstructionResultOK()) {
				ExecTask receivedExecTask = (ExecTask)XMLUtil.XMLStringToObject(response.getXmlOutputData());
				execTask.setStatusMessage(receivedExecTask.getStatusMessage());
				execTask.setStatus(receivedExecTask.getStatus());
			} else {
				// If response is KO
				execTask.setBegin(new Date());
				execTask.setEnd(new Date());
				execTask.setStatus(ExecTask.KO);
				execTask.setStatusMessage("Cannot add in queue "+response.getExceptionMessage());
				logger.warn("Job "+job.getName()+" on "+job.getHostName()+": " +response.getExceptionMessage());
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
		
		// Saving ExecTask state
		transaction = this.databaseSession.beginTransaction();
		this.databaseSession.save(execTask);
		transaction.commit();
		
	}
	
	/**
	 * Update task status in DB
	 */
	public void updateTasksStatus() {
				
		ArrayList<String> hostnameList = new ArrayList<String>();

		// Finding ExecTask whith status IN_QUEUE
		String queryFind = "select execTask from org.jity.common.referential.ExecTask execTask"
			+ " where execTask.status = "+ExecTask.IN_QUEUE;

		List execTaskList = this.databaseSession.createQuery(queryFind).list();
		
		logger.debug("Found "+execTaskList.size()+" in_queue tasks in db");
		
		// Create hostnameList
		Iterator iterExecTaskList = execTaskList.iterator();
		while (iterExecTaskList.hasNext()) {
			ExecTask execTask = (ExecTask) iterExecTaskList.next();
			String hostname = execTask.getJob().getHostName();
			
			if (! hostnameList.contains(hostname)) hostnameList.add(hostname);
		}
		
		logger.debug("Found "+hostnameList.size()+" hostnames to pool");
		
		// For each hostname, getTasksStatus
		Iterator<String> iterHostnameList = hostnameList.iterator();
		while (iterHostnameList.hasNext()) {
			String hostname = iterHostnameList.next();
			
			// Construct Request
			JityRequest request = new JityRequest();
			request.setInstructionName("GETTASKSTATUS");
			
			logger.debug("Pooling "+hostname+" agent");
			
			RequestSender requestLauncher = new RequestSender();
			try {
				requestLauncher.openConnection(hostname,
						ServerConfig.getInstance().getAGENT_PORT());

				JityResponse response = requestLauncher.sendRequest(request);

				requestLauncher.closeConnection();
				
				// If response is OK
				if (response.isInstructionResultOK()) {

					ArrayList<ExecTask> taskQueueExtract =
						(ArrayList<ExecTask>)XMLUtil.XMLStringToObject(response.getXmlOutputData());
					Iterator<ExecTask> iterTaskQueueExtract = taskQueueExtract.iterator();
					while (iterHostnameList.hasNext()) {
						ExecTask receviedExecTask = (ExecTask) iterExecTaskList.next();
						
						// Saving ExecTask state
						Transaction transaction = this.databaseSession.beginTransaction();
						this.databaseSession.save(receviedExecTask);
						transaction.commit();
						
						logger.debug("Exec task "+receviedExecTask.getId()+" updated in db");
					}
					
				} else {
					logger.warn("Can't get tasks status for agent "+hostname);
				}
				
			} catch (UnknownHostException e) {
				logger.warn("Can't get tasks status for agent "+hostname);
			} catch (IOException e) {
				logger.warn("Can't get tasks status for agent "+hostname);
			}
		
		}
		
	}
	
	
    /**
     * Launch jobs analyse each X seconds.
     */
    public void run() {
        int cycle = ServerConfig.getInstance().getSERVER_POOLING_CYCLE();
        
        try {
			this.databaseSession = DatabaseServer.getInstance().getSession();
		} catch (DatabaseException e) {
            logger.fatal(e.getMessage());
		}
        
		logger.info("Starting ExecManager");
		
        while (!shutdownAsked) {
            try {
                analyzeJobsForExecute();
                TimeUtil.waiting(cycle/2);
                updateTasksStatus();
                TimeUtil.waiting(cycle/2);
            } catch (InterruptedException ex) {
            	if (!shutdownAsked)  {
            		logger.warn("ExecManager is stopped.");
            		logger.debug(ex.toString());
            	}
                
            } catch (Exception ex) {
                logger.fatal("Error during checkJobs: " + ex.getClass().getSimpleName()+": "+ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }
        
    }
}