package org.jity.agent.taskManager;

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
import org.jity.agent.AgentConfig;
import org.jity.agent.AgentException;
import org.jity.agent.commandExecutor.CommandExecutor;
import org.jity.agent.commandExecutor.ErrorOutputLogger;
import org.jity.agent.commandExecutor.StandardOutputLogger;
import org.jity.common.referential.ExecTask;
import org.jity.common.referential.Job;
import org.jity.common.util.DateUtil;
import org.jity.common.util.TimeUtil;
import org.jity.common.util.XMLUtil;

public class TaskManager implements Runnable {
	private static final Logger logger = Logger.getLogger(TaskManager.class);

	private static TaskManager instance = null;
	
    private Thread daemon = null;

    private boolean shutdownAsked = false;
    
    //private ArrayList<ExecTask> taskQueue = new ArrayList<ExecTask>();
    private List<ExecTask> taskQueue = Collections.synchronizedList(new ArrayList());
    
    private int maxNumTaskInQueue = 50;
    
    private int maxNumTaskExecution = 10;
    private int currentNumTaskExection = 0;
    
    public List<ExecTask> getTaskQueue() {
    	return this.taskQueue;
    }
    
    /**
     * Return an Iterator on taskQueue
     * @return Iterator <ExecTask>
     */
    public Iterator<ExecTask> getTaskQueueIterator() {
    	return this.taskQueue.iterator();
    }
    
    /**
     * Return current numbers of tasks in queue
     * @return
     */
    public int getCurrentNumTaskInQueue() {
    	return this.taskQueue.size();
    }
    
    /**
     * Return maximum number of tasks in queue
     * @return
     */
    public int getMaxNumTaskInQueue() {
    	return maxNumTaskInQueue;
    }
    
	public static TaskManager getInstance() {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}
	
	/**
	 * Return true if launchManger is running
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
     * Start the TaskManager in a Thread.
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
        	logger.info("Shutdown of TaskManager asked.");
            shutdownAsked = true;
            daemon.interrupt();
            daemon = null;
			logger.info("TaskManager successfuly shutdowned");
        }
    }
    
    /**
     * Add a ExecutionTask to execute in queue
     * @param task
     */
    public void addTaskInQueue(ExecTask execTask) {
    	logger.debug("Adding job "+execTask.getJob().getName()+" to execution Queue");

    	synchronized(this.taskQueue) {
    		this.taskQueue.add(execTask);
    	}
    }
    
    public void taskQueueAnalyze() throws AgentException, IOException {

    	synchronized(this.taskQueue) {

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
    				exitStatus = cmdExecutor.runCommand(job.getCommandPath());

    				logger.info("End of "+job.getName()+"(exit status: "+exitStatus+")");

    				// Setting execStatus
    				if (exitStatus == 0) task.setStatus(ExecTask.OK);
    				else task.setStatus(ExecTask.KO);
    				task.setLogFile(jobLogFile.getAbsolutePath());

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

    }
    
    /**
     * Launch queue analyse each X seconds.
     */
    public void run() {
        int cycle = 10;
		
        logger.info("TaskManager started.");
		
        while (!shutdownAsked) {

            try {
            	taskQueueAnalyze(); 
            	TimeUtil.waiting(cycle);
            } catch (InterruptedException ex) {
            	if (!shutdownAsked) logger.warn("TaskManager is stopped.");
            	if (!shutdownAsked) logger.debug(ex.toString());
            } catch (Exception ex) {
                logger.fatal("Error during queue analyse: " + ex.getClass().getSimpleName()+": "+ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }
        
    }
	
}
