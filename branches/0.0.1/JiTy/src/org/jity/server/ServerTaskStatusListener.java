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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.RequestReceiver;
import org.jity.common.protocol.RequestSender;
import org.jity.common.referential.ExecTask;
import org.jity.common.util.XMLUtil;
import org.jity.server.database.DatabaseException;
import org.jity.server.database.DatabaseServer;

/**
 * Listen task status to update state in DB
 *
 */
public class ServerTaskStatusListener implements Runnable {
	private static final Logger logger = Logger.getLogger(ServerTaskStatusListener.class);

	private static ServerTaskStatusListener instance = null;
	
	private ServerSocket listenSocket;
	
	private Session databaseSession;
		
    private Thread daemon = null;

    private boolean shutdownAsked = false;
 
    
    /**
     * Return current instance of ServerTaskStatusListener.
     * Create one if none
     * @return ServerTaskStatusListener
     */
	public static ServerTaskStatusListener getInstance() {
		if (instance == null) {
			instance = new ServerTaskStatusListener();
		}
		return instance;
	}
	
	/**
	 * Return true if ServerTaskStatusListener is running
	 * @return boolean
	 */
	public synchronized boolean isRunning() {
		if (this.daemon != null)
			return this.daemon.isAlive();
		else
			return false;
	}
	
    /**
     * Start the ServerTaskStatusListener in a Thread.
     */
    public synchronized void startTaskStatusListener() {
        if (daemon == null) {
            daemon = new Thread(this);
            daemon.start();
        }
    }

    /**
     * Stop current ServerTaskStatusListener if running.
     */
    public synchronized void stopTaskStatusListener() {
        if (daemon != null) {
        	logger.info("Shutdown of ServerTaskStatusListener asked.");
            shutdownAsked = true;
    		this.databaseSession.close();
            daemon.interrupt();
            daemon = null;
			logger.info("ServerTaskStatusListener successfuly shutdowned");
        }
    }
        
   	
	/**
	 * Update tasks status in DB by pooling agent
	 */
	public void updateTasksStatusForDMZ() {
				
		ArrayList<String> hostnameList = new ArrayList<String>();

		// Finding ExecTask whith status IN_QUEUE
		String queryFind = "select execTask from org.jity.common.referential.ExecTask execTask"
			+ " where execTask.status = "+ExecTask.IN_QUEUE;

		List<ExecTask> execTaskList = this.databaseSession.createQuery(queryFind).list();
		
		logger.debug("Found "+execTaskList.size()+" in_queue tasks");
		
		// Create hostnameList to pool for concerned ExecTask
		Iterator<ExecTask> iterExecTaskList = execTaskList.iterator();
		while (iterExecTaskList.hasNext()) {
			ExecTask execTask = iterExecTaskList.next();
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
						ServerConfig.getInstance().getAGENT_INPUT_PORT());

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
					logger.warn("Can't get tasks status for agent "+hostname);
					//TODO must update ExecTask
					
				}
				
			} catch (UnknownHostException e) {
				logger.warn("Can't get tasks status for agent "+hostname);
			} catch (IOException e) {
				logger.warn("Can't get tasks status for agent "+hostname);
			}
		
		}
		
	}
	
	
    /**
     * Launch ServerTaskStatusListener
     */
    public void run() {
		Socket socket = null;
		
        try {
			this.databaseSession = DatabaseServer.getInstance().getSession();
		} catch (DatabaseException e) {
            logger.fatal(e.getMessage());
		}
        
		logger.info("Starting ServerTaskStatusListener...");

		int serverPort = ServerConfig.getInstance().getSERVER_INPUT_PORT();
		try {
			listenSocket = new ServerSocket(serverPort);
			logger.info("ServerTaskStatusListener running on port " + serverPort);

		} catch (IOException e) {
			logger.fatal(e.getMessage());
			System.exit(1);
		}
		
	
		try {
			
			logger.info("ServerTaskStatusListener successfully started.");
			
			while (true) {
				socket = listenSocket.accept();
				try {
					String serverHostname = socket.getInetAddress().getHostName();
					
					logger.info("New connection from "+serverHostname+" ("
							+ socket.getInetAddress() + ").");

					new RequestReceiver(socket);
					
				} catch (IOException e) {
					socket.close();
				}
			}

		} catch (IOException e) {
			if (!shutdownAsked) logger.warn("Problem during agent connection.");
			logger.debug(e.getMessage());
		} finally {
			try {
				if (listenSocket != null)
					listenSocket.close();
			} catch (IOException e) {
				logger.warn("Failed to close agent connection.");
				logger.debug(e.getMessage());
			}
			logger.info("ServerTaskStatusListener shutdowned correctly.");
		}
        
    }
}