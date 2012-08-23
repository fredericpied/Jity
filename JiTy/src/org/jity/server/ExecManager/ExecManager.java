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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
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
    private void checkJobs() throws DatabaseException {

		String queryFind = "select job from org.jity.common.referential.Job job"
			+ " where job.isActived = true";

		List jobList = this.databaseSession.createQuery(queryFind).list();
		
		logger.info("Found "+jobList.size()+" activated jobs in database");
		
		Calendar cal = new GregorianCalendar();
		Date execDateValue = cal.getTime();
		
		Iterator iterJobList = jobList.iterator();
		while (iterJobList.hasNext()) {
			Job job = (Job) iterJobList.next();
//TODO ne pas lancer un job qui est déja en cours d'exécution
// il faudra affiner la requete en ajoutant un execStatus != RUNNING
// Mais l'execstatus n'est pas mis à jour avant le lancement de l'exécution
// à modifier certainnement
			try {
				if (job.getDateConstraint().isAValidDate(execDateValue))
					addingOnJobToExecute(job);
			} catch (DateConstraintException e) {
				logger.warn("Job "+job.getName()+": "+e.getMessage());
			}			
		}
		
    }
    
	private void addingOnJobToExecute(Job job) {
		
		logger.info("Adding Job "+job.getName()+ "("+job.getDateConstraint().getPlanifRule()+") to agent "+job.getHostName());
		
		ExecTask execTask = new ExecTask();
		execTask.setJob(job);
		execTask.setStatus(ExecTask.PLANED);
		
		// Construct Request
		JityRequest request = new JityRequest();
		request.setInstructionName("ADDTASKINQUEUE");
		request.setXmlInputData(XMLUtil.objectToXMLString(job));
		
		RequestSender requestLauncher = new RequestSender();

		try {
			requestLauncher.openConnection(job.getHostName(),
					ServerConfig.getInstance().getAGENT_PORT());

			JityResponse response = requestLauncher.sendRequest(request);

			requestLauncher.closeConnection();

			// If response is OK
			if (response.isInstructionResultOK()) {
				execTask.setStatus(ExecTask.IN_QUEUE);
			} else {
				// Is response is KO
				execTask.setStatus(ExecTask.KO);
				execTask.setStatusMessage("Cannot add in queue "+response.getExceptionMessage());
			}
			
		} catch (UnknownHostException e) {
			logger.warn("Job "+job.getName()+" on "+job.getHostName()+": " +e.getMessage());
			execTask.setEnd(new Date());
			execTask.setStatus(ExecTask.KO);
			execTask.setStatusMessage(e.getMessage());
		} catch (IOException e) {
			logger.warn("Job "+job.getName()+" on "+job.getHostName()+": " +e.getMessage());
			execTask.setEnd(new Date());
			execTask.setStatus(ExecTask.KO);
			execTask.setStatusMessage(e.getMessage());
		}
	}
	
    /**
     * Launch jobs analyse each X seconds.
     */
    public void run() {
        int cycle = ServerConfig.getInstance().getSERVER_POOLING_CYCLE();
        
        try {
			this.databaseSession = DatabaseServer.getSession();
		} catch (DatabaseException e) {
            logger.fatal(e.getMessage());
		}
        
		logger.info("Starting ExecManager");
		
        while (!shutdownAsked) {
            try {
                logger.info("Start of job analyze.");
                checkJobs();
                //testOneJob();
                logger.info("End of job analyze.");
                TimeUtil.waiting(cycle);
            } catch (InterruptedException ex) {
            	if (!shutdownAsked) logger.warn("ExecManager is stopped.");
                logger.debug(ex.toString());
            } catch (Exception ex) {
                logger.fatal("Error during checkJobs: " + ex.getClass().getSimpleName()+": "+ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }
        
    }
}