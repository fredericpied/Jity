package org.jity.agent;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jity.agent.commandExecutor.CommandExecutor;
import org.jity.common.referential.ExecTask;
import org.jity.common.util.TimeUtil;

/**
 * Pool queue to launch executions
 * @author fred
 *
 */
public class AgentQueueManager implements Runnable {
	private static final Logger logger = Logger.getLogger(AgentQueueManager.class);

	private static AgentQueueManager instance = null;
	
    private Thread daemon = null;

    private boolean shutdownAsked = false;

    /**
     * Return current instance
     * @return AgentQueueManager
     */
	public static AgentQueueManager getInstance() {
		if (instance == null) {
			instance = new AgentQueueManager();
		}
		return instance;
	}
	
	/**
	 * Return true if daemon is running
	 * @return boolean
	 */
	public synchronized boolean isRunning() {
		if (this.daemon != null)
			return this.daemon.isAlive();
		else
			return false;
	}
	
    /**
     * Start the daemon
     */
    public synchronized void start() {
        if (daemon == null) {
            daemon = new Thread(this);
            daemon.start();
        }
    }

    /**
     * Stop current daemon
     */
    public synchronized void stop() {
        if (daemon != null) {
        	logger.info("Shutdown of "+this.getClass().getSimpleName()+" asked.");
            shutdownAsked = true;
            daemon.interrupt();
            daemon = null;
			logger.info(this.getClass().getSimpleName()+" successfuly shutdowned");
        }
    }
      
    /**
     * Analyze task queue to execute jobs
     * @throws AgentException
     * @throws IOException
     */
    public void taskQueueAnalyze() throws AgentException, IOException {

		// Checking concurrent max jobs nummber
    	int maxConcurrentJob = AgentConfig.getInstance().getMAX_CONCURRENT_JOBS();
    	
    	if (Agent.getInstance().getCurrentJobsExecution() <= maxConcurrentJob) {
    	
	    	synchronized(Agent.getInstance().getTaskQueue()) {
	    		Iterator<ExecTask> iterTask = Agent.getInstance().getTaskQueue().iterator();
	    		while (iterTask.hasNext()) {
	    			ExecTask task = iterTask.next();

	    			// If task not in the IN_QUEUE state, continue
	    			if (task.getStatus() != ExecTask.IN_QUEUE) continue;
	    			
	    			// Launch command in a new thread
	    			CommandExecutor commandExecutor = new CommandExecutor();
	    			commandExecutor.execute(task);
	    		}

	    	} // synchronized(taskQueueSynchro) {
    	} else {
    		logger.debug("Max concurent jobs reached ("+maxConcurrentJob+")");
    	}
    }
    
    /**
     * Launch queue analyze each X seconds.
     */
    public void run() {
        int cycle = AgentConfig.getInstance().getAGENT_POOLING_CYCLE();
		
        logger.info(this.getClass().getSimpleName() +" started.");
		
        while (!shutdownAsked) {

            try {
            	taskQueueAnalyze(); 
            	TimeUtil.waiting(cycle);
            } catch (InterruptedException ex) {
            	if (!shutdownAsked) logger.warn(this.getClass().getSimpleName() +" is stopped.");
            	if (!shutdownAsked) logger.debug(ex.toString());
            } catch (Exception ex) {
                logger.fatal("Error during queue analyse: " + ex.getClass().getSimpleName()+": "+ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }
        
    }
	
}
