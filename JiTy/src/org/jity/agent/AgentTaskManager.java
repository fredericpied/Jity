package org.jity.agent;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.jity.agent.commandExecutor.CommandExecutor;
import org.jity.agent.commandExecutor.ErrorOutputLogger;
import org.jity.agent.commandExecutor.StandardOutputLogger;
import org.jity.common.referential.ExecTask;
import org.jity.common.referential.Job;
import org.jity.common.util.DateUtil;
import org.jity.common.util.TimeUtil;

/**
 * Launch execution regarding to task in taskQueue
 * @author 09344a
 *
 */
public class AgentTaskManager implements Runnable {
	private static final Logger logger = Logger.getLogger(AgentTaskManager.class);

	private static AgentTaskManager instance = null;
	
    private Thread daemon = null;

    private boolean shutdownAsked = false;

    /**
     * taskQueue (Synchronized)
     */
    private List<ExecTask> taskQueue = Collections.synchronizedList(new ArrayList());
    
    //private int maxNumTaskInQueue = 50;
    
 //   private int maxNumTaskExecution = 10;
    private int currentJobsExection = 0;
    
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
    
	public static AgentTaskManager getInstance() {
		if (instance == null) {
			instance = new AgentTaskManager();
		}
		return instance;
	}
	
	/**
	 * Return true if AgentTaskManager is running
	 * 
	 * @return boolean
	 */
	public synchronized boolean isRunning() {
		if (this.daemon != null)
			return this.daemon.isAlive();
		else
			return false;
	}
	
    /**
     * Start the AgentTaskManager in a Thread.
     */
    public synchronized void startTaskManager() {
        if (daemon == null) {
            daemon = new Thread(this);
            daemon.start();
        }
    }

    /**
     * Stop current TaskManager if running.
     */
    public synchronized void stopTaskManager() {
        if (daemon != null) {
        	logger.info("Shutdown of AgentTaskManager asked.");
            shutdownAsked = true;
            daemon.interrupt();
            daemon = null;
			logger.info("AgentTaskManager successfuly shutdowned");
        }
    }
    
    /**
     * Add a task to execute in queue
     * @param ExecTask
     * @throws AgentException 
     */
    public void addTaskInQueue(ExecTask execTask) throws AgentException {
    			
		int maxJobsInQueue = AgentConfig.getInstance().getMAX_JOBS_IN_QUEUE();
		
		if (AgentTaskManager.getInstance().getCurrentNumTaskInQueue() >=
			maxJobsInQueue) {
			throw new AgentException("Max number of tasks in queue reached ("+
					maxJobsInQueue+")");
		}
    	
    	logger.debug("Adding job "+execTask.getJob().getName()+" to execution queue");

    	synchronized(this.taskQueue) {
    		this.taskQueue.add(execTask);
    	}
    }
    
    /**
     * Analyse task queue to execute jobs
     * @throws AgentException
     * @throws IOException
     */
    public void taskQueueAnalyze() throws AgentException, IOException {

		// Checking concurrent jobs
    	int maxConcurrentJob = AgentConfig.getInstance().getMAX_CONCURRENT_JOBS();
    	
    	if (this.currentJobsExection <= maxConcurrentJob) {
    	
	    	synchronized(this.taskQueue) {
	    		//TODO chaque execution bloque la boucle.
	    		Iterator<ExecTask> iterTask = this.taskQueue.iterator();
	    		while (iterTask.hasNext()) {
	    			ExecTask task = iterTask.next();
	
	    			if (task.getStatus() != ExecTask.IN_QUEUE) continue;
	    			
	    			Job job = task.getJob();
	
	    			// Initializing exitStatus
	    			int exitStatus = -1;
	
	    			// Initializing log file name whith timestamp
	    			File logDir = new File(AgentConfig.getInstance().getJOBS_LOGS_DIR());
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
	    			try {
	    				this.currentJobsExection++;

	    				exitStatus = cmdExecutor.runCommand(job.getCommandPath());
	
	    				logger.info("End of "+job.getName()+"(exit status: "+exitStatus+")");
	
	    				// Setting execStatus
	    				if (exitStatus == 0) {
	    					task.setStatus(ExecTask.OK);
	    				} else {
	    					task.setStatus(ExecTask.KO);
	    				}
	    				task.setStatusMessage("Command exit status = "+exitStatus);
	    				task.setLogFile(jobLogFile.getAbsolutePath());
	    				
	    				this.currentJobsExection--;
	    			
	    			} catch (Exception e) {
	    				logger.info("Exception "+e.getClass().getName()+
	    						" while executing "+job.getName()+"("+e.getMessage()+")");
	
	    				// Setting execStatus
	    				task.setStatus(ExecTask.KO);
	    				task.setStatusMessage("Exception: "+e.getMessage());
	    				task.setLogFile(jobLogFile.getAbsolutePath());
	
	    			}
	
	    		}
	    	} //     	  synchronized(taskQueueSynchro) {
    	} else {
    		logger.warn("Max concurent job reached");
    	}
    }
    
    /**
     * Launch queue analyse each X seconds.
     */
    public void run() {
        int cycle = 10; //TODO Add to AgentConfig
		
        logger.info("AgentTaskManager started.");
		
        while (!shutdownAsked) {

            try {
            	taskQueueAnalyze(); 
            	TimeUtil.waiting(cycle);
            } catch (InterruptedException ex) {
            	if (!shutdownAsked) logger.warn("AgentTaskManager is stopped.");
            	if (!shutdownAsked) logger.debug(ex.toString());
            } catch (Exception ex) {
                logger.fatal("Error during queue analyse: " + ex.getClass().getSimpleName()+": "+ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }
        
    }
	
}
