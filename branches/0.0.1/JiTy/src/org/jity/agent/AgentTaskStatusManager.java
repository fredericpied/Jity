package org.jity.agent;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jity.agent.commandExecutor.CommandExecutor;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.RequestSender;
import org.jity.common.referential.ExecTask;
import org.jity.common.util.TimeUtil;
import org.jity.common.util.XMLUtil;
import org.jity.server.ServerConfig;

/**
 * Pool queue to send status to server
 * @author fred
 *
 */
public class AgentTaskStatusManager implements Runnable {
	private static final Logger logger = Logger.getLogger(AgentTaskStatusManager.class);

	private static AgentTaskStatusManager instance = null;
	
    private Thread daemon = null;

    private boolean shutdownAsked = false;

    /**
     * Return current instance
     * @return AgentQueueManager
     */
	public static AgentTaskStatusManager getInstance() {
		if (instance == null) {
			instance = new AgentTaskStatusManager();
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
    public void taskQueueAnalyze() throws AgentException, IOException {

    	 //ArrayList<ExecTask> taskQueueExtract = new ArrayList<ExecTask>();
    	
	    	synchronized(Agent.getInstance().getTaskQueue()) {

	    		Iterator<ExecTask> iterTask = Agent.getInstance().getTaskQueue().iterator();
	    		while (iterTask.hasNext()) {
	    			ExecTask task = iterTask.next();

	    			// If task not in the IN_QUEUE state, continue
	    			if (task.getStatus() != ExecTask.IN_QUEUE) {
	    				
	    				sendTaskStatus(task);
	    					    				
	    			}
	    		}

	    	} // synchronized(taskQueueSynchro) {
	    	
    }
    
    
    private void sendTaskStatus(ExecTask task) {
    	
    	// Construct Request
		JityRequest request = new JityRequest();
		request.setInstructionName("UPDTASKSTATUS");
		request.setXmlInputData(XMLUtil.objectToXMLString(task));
		
		try {
			RequestSender requestLauncher = new RequestSender();

			requestLauncher.openConnection(task.getServerHost(),
					AgentConfig.getInstance().getSERVER_INPUT_PORT());

			// Send request to agent
			JityResponse response = requestLauncher.sendRequest(request);

			requestLauncher.closeConnection();

			if (! response.isInstructionResultOK()) {
				// If response is KO
				logger.warn("Cannot send task "+task.getId()+" status to server "+task.getServerHost()+" ("+response.getExceptionName()+":"+response.getExceptionMessage()+")");
				
			}
			
		} catch (UnknownHostException e) {
			logger.warn("Cannot send task "+task.getId()+" status to server "+task.getServerHost() + "("+e.getMessage()+")");
		} catch (IOException e) {
			logger.warn("Cannot send task "+task.getId()+" status to server "+task.getServerHost() + "("+e.getMessage()+")");
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
