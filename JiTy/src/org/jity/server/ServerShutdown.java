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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.jity.common.XMLUtil;
import org.jity.server.protocol.Request;

/**
 * Launch this on the server to shutdown it
 * @author 09344A
 *
 */
public class ServerShutdown {
	private static final Logger logger = Logger.getLogger(ServerShutdown.class);
	
	// Socket used to dialog with the server
	Socket sock = null;
	BufferedReader sin;
	PrintWriter sout;

	public void openConnection(String host, int port) throws UnknownHostException, IOException {
		// open socket
		sock = new Socket(host, port);
		// Init reader and writer
		sin = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		sout = new PrintWriter(sock.getOutputStream());
	}
	
    public void closeConnection() throws IOException {
       if (this.sock != null) this.sock.close();
     }

    public String sendRequest(Request request) {
        String result = null;
        this.sout.println(request.getXML());
        this.sout.flush();
        try {
            result = this.sin.readLine();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
	
	 public static void main(String[] args) {
		 
		 ServerShutdown sie = new ServerShutdown();
		 
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
		 
	        try {
	        	sie.openConnection("localhost", ServerConfig.getInstance().getSERVER_PORT());
	        	
	        	Request request = new Request();
	        	request.setInstructionName("SHUTDOWNSERVER");
	        	
	        	String response = sie.sendRequest(request);
	        	
	        	logger.info("Response: "+response);
	        	sie.closeConnection();
	        	
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        
	        
	 }
	

}
