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
 */
package org.jity.server;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.jity.server.database.Database;

import java.io.*;
import java.net.*;

public class Server implements Runnable {
	private static final Logger logger = Logger.getLogger(Server.class);

    private static Server instance = null;
    
    private ServerSocket listenSocket;
    
    private Thread daemon = null;

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    
    /**
     * Start the server in a Thread.
     * @throws ServerException 
     */
    public synchronized void startServerDaemon() throws ServerException {
        if (daemon == null) {
        	daemon = new Thread(this);
        	daemon.start();
        }
    }
    
    /**
     * Stop current server if running.
     * @throws ServerException 
     */
    public synchronized void stopServerDaemon() throws ServerException {
        if (daemon != null) {
        	this.shutdownServer();
            daemon.interrupt();
            daemon = null;
        }
    }
    
    /**
     * Loading the server config file
     * @throws ServerException
     */
    private void loadConfigFile() throws ServerException {
    	  // Load config file
        try {
            ServerConfig serverConfig = ServerConfig.getInstance();
            logger.info("Reading configuration file.");
            serverConfig.initialize();
            logger.info("Configuration File successfully loaded.");
        } catch (IOException e) {
        	throw new ServerException("Failed to read configuration file ("+e.getMessage()+").");
        }
    }

    public void run() {
        Socket client = null;
        
        logger.info("Open Job Scheduler Server starting process.");
        
        // Loading config File
        try {
			this.loadConfigFile();
		} catch (ServerException e1) {
			logger.fatal(e1.getMessage());
			System.exit(1);
		}
        
        // Init database connection
        this.initDbConnection();
        
        logger.info("Starting the server ...");
           
        int serverPort = ServerConfig.getInstance().getSERVER_PORT();
        try {
            listenSocket = new ServerSocket(serverPort);
            logger.info("Server running on port : " + serverPort);
            logger.info("OpenJobScheduler Server successfully started.");
        } catch (IOException e) {
			logger.fatal(e.getMessage());
			System.exit(1);
        }
        
        
        try {
        	
            while (true) {
                client = listenSocket.accept();
                try {
                    logger.info("New connection from " + client.getInetAddress() + ".");
                    new ServeOneClient(client);
                } catch (IOException e) {
                    client.close();
                }
            }
                        
        } catch (IOException e) {
            logger.warn("Problem during client connection.");
            logger.debug(e.getMessage());
        } finally {
            try {
                if (listenSocket != null)
                    listenSocket.close();
            } catch (IOException e) {
                logger.warn("Failed to close client connection.");
                logger.debug(e.getMessage());
            }
            logger.info("OpenJobScheduler Server shutdowned correctly.");
        }
    }

    private void shutdownServer() throws ServerException {
        logger.info("Shutdown of server asked.");
        
        try {
        	logger.info("Closing Database session.");
            Database.terminateSessionFactory();
        	logger.info("Closing Network socket.");
            listenSocket.close();

        } catch (IOException e) {
            throw new ServerException("Shutdown of server failed.");
        }
        logger.info("Server successfuly shutdowned");
    }
    

    private void initDbConnection() {
    	logger.info("Init of DB connection...");
    	Session sess = Database.getSessionFactory().openSession();
        sess.close();
        logger.info("Connection to database: OK");
    }
    
}

