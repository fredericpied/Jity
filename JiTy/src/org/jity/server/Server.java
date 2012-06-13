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

import java.io.*;
import java.net.*;

/*
 * 
 */
public class Server {
	private static final Logger logger = Logger.getLogger(Server.class);

    private static Server instance = null;
    
    private ServerSocket listenSocket;
    
    private Server() { }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void startServer() throws ServerException {
        Socket client = null;

        int serverPort = ServerConfig.getInstance().getSERVER_PORT();
        try {
            listenSocket = new ServerSocket(serverPort);
            logger.info("Server running on port : " + serverPort);
            logger.info("OpenJobScheduler Server successfully started.");
        } catch (IOException e) {
            throw new ServerException("Port " + serverPort + " already in use.");
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

    public void shutdownServer() throws ServerException {
        logger.info("Shutdown of server asked.");
        
        try {
            listenSocket.close();
        } catch (IOException e) {
            throw new ServerException("Shutdown of server failed.");
        }
        logger.info("Server successfuly shutdowned");
    }
    

//    public static void initDbConnection() throws HibernateException {
//        Session sess = null;
//        logger.info("Init of DB connection...");
//        sess = HibernateUtil.getSession();
//        sess.close();
//        logger.info("Connection to database: OK");
//    }
    
    public static void main(String[] args) {
    	
//    	ServerConfig serverConfig2 = ServerConfig.getInstance();
//    	try {
//			serverConfig2.generate(new File("conf/ServerConfig.xml"));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//    	System.exit(1);
    	
    	
        Server server = Server.getInstance();
        
        logger.info("Open Job Scheduler Server starting process.");
        
        // Load config file
        try {
            ServerConfig serverConfig = ServerConfig.getInstance();
            logger.info("Reading configuration file.");
            serverConfig.initialize();
            logger.info("Configuration File successfully loaded.");
        } catch (IOException e) {
            logger.fatal("Failed to read configuration file ("+e.getMessage()+").");
            System.exit(2);
        }
            
            
//            
//        // Init database connection
//        try {
//            Main.initDbConnection();
//        } catch (HibernateException e) {
//            logger.error("Failed to init database.");
//            System.exit(2);
//        }
        
        logger.info("Starting the server ...");
        try {
        	server.startServer();
        } catch (ServerException e) {
            logger.fatal("Failed to start server: " + e.getMessage());
            System.exit(2);
        }
    }
    

}

