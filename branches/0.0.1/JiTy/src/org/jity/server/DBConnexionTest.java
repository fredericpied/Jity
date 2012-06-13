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
