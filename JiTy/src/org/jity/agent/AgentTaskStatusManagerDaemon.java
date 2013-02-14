package org.jity.agent;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.RequestSender;
import org.jity.common.referential.ExecTask;
import org.jity.common.util.TimeUtil;
import org.jity.common.util.XMLUtil;

/**
 * Pool Agent Queue to send task status to server
 * @author fred
 *
 */
public class AgentTaskStatusManagerDaemon implements Runnable {
	
	private static final Logger logger = Logger.getLogger(AgentTaskStatusManagerDaemon.class);

	private static AgentTaskStatusManagerDaemon instance = null;
	
    private Thread daemon = null;

    private boolean shutdownAsked = false;

    /**
     * Return current instance
     * @return AgentQueueManager
     */
	public static AgentTaskStatusManagerDaemon getInstance() {
		if (instance == null) {
			instance = new AgentTaskStatusManagerDaemon();
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
     * Analyze task queue to pool task status to server
     * @throws AgentException
     * @throws IOException
     */
    private void taskQueueReading() throws AgentException, IOException {
    	
	    	synchronized(Agent.getInstance().getTaskQueue()) {

	    		Iterator<ExecTask> iterTask = Agent.getInstance().getTaskQueue().iterator();
	    		while (iterTask.hasNext()) {
	    			ExecTask task = iterTask.next();

	    			// If task not in the IN_QUEUE state, continue
	    			if (task.getStatus() != ExecTask.IN_QUEUE) {
	    				if ( (sendTaskStatus(task) == true) && 
	    					(task.getStatus() == ExecTask.OK) || (task.getStatus() == ExecTask.KO) ) {
	    					// if response is OK and exectask status = 5 (OK) ou  6 (KO), delete task from queue
	    					iterTask.remove();
	    					logger.debug("removing task "+task.getId()+" from agent queue");
	    				}
	    			}
	    		}

	    	} 
	    	
    }
    
    /**
     * Send one task status to JiTy Server
     * @param task
     */
    private boolean sendTaskStatus(ExecTask task) {
    	
    	// Construct Request
		JityRequest request = new JityRequest();
		request.setInstructionName("UPDTASKSTATUS");
		request.setXmlInputData(XMLUtil.objectToXMLString(task));
		
		try {
			RequestSender requestLauncher = new RequestSender();

			requestLauncher.openConnection(task.getServerIp(),
					AgentConfig.getInstance().getSERVER_INPUT_PORT());

			// Send request to agent
			JityResponse response = requestLauncher.sendRequest(request);

			requestLauncher.closeConnection();

			if (! response.isInstructionResultOK()) {
				// If response is KO
				logger.warn("Cannot send task "+task.getId()+" status to server "+task.getServerIp()+" ("+response.getExceptionName()+":"+response.getExceptionMessage()+")");
				return false;
			} else {
				return true;
			}
			
		} catch (UnknownHostException e) {
			logger.warn("Cannot send task "+task.getId()+" status to server "+task.getServerIp() + "("+e.getMessage()+")");
			return false;
		} catch (IOException e) {
			logger.warn("Cannot send task "+task.getId()+" status to server "+task.getServerIp() + "("+e.getMessage()+")");
			return false;
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
            	taskQueueReading(); 
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
