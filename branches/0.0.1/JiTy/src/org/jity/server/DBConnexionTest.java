package org.jity.server;

import java.io.IOException;

import org.apache.log4j.Logger;

public class DBConnexionTest {
	private static final Logger logger = Logger.getLogger(DBConnexionTest.class);
	
    public static void main(String[] args) {
    	
        Server server = Server.getInstance();
        
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

    
    
    
    
    }
	
}
