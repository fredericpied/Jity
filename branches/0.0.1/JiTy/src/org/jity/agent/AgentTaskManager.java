package org.jity.agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jity.agent.commandExecutor.CommandExecutor;
import org.jity.common.referential.ExecTask;
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
    
    public void incrementCurrentJobsExecution() {
    	this.currentJobsExection++;
    }
    
    public void decrementCurrentJobsExecution() {
    	this.currentJobsExection--;
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
	    		Iterator<ExecTask> iterTask = this.taskQueue.iterator();
	    		while (iterTask.hasNext()) {
	    			ExecTask task = iterTask.next();
	
	    			if (task.getStatus() != ExecTask.IN_QUEUE) continue;
	    			
	    			CommandExecutor commandExecutor = new CommandExecutor();
	    			commandExecutor.execute(task);
	    		}

	    	} // synchronized(taskQueueSynchro) {
    	} else {
    		logger.debug("Max concurent jobs reached ("+maxConcurrentJob+")");
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
