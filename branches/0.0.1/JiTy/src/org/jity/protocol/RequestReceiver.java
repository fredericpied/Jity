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
package org.jity.protocol;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jity.protocol.JityResponse;
import org.jity.protocol.Protocol;
import org.jity.protocol.ProtocolException;

/**
 * Execute request
 */
public class RequestReceiver extends Thread {
	private static final Logger logger = Logger.getLogger(RequestReceiver.class);
	
    private Socket socket;
    private BufferedReader networkReader;
    private PrintWriter networkWriter;
    private ArrayList<String> remoteHostnameList;

    /**
     * Initialize requestReceiver
     * @param socket
     * @throws IOException
     */
    public RequestReceiver(Socket socket) throws IOException {
    	this.socket = socket;
    	this.networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.networkWriter = new PrintWriter(socket.getOutputStream());
        start();
    }
   
    /**
     * Initialize requestReceiver whith an autorized hostnames List
     * @param socket
     * @throws IOException
     */
    public RequestReceiver(Socket socket, ArrayList<String> remoteHostnameList) throws IOException {
    	this.socket = socket;
    	this.networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.networkWriter = new PrintWriter(socket.getOutputStream());
        this.remoteHostnameList = remoteHostnameList;
        start();
    }
    
    public void run() {
        String xmlInputData = null;
        String xmlOutputData;

        try {
        
            	// Reading netword datagram
            	xmlInputData = networkReader.readLine();
           		
            	try {
            		logger.debug("Server data received: " + xmlInputData);

            		// Identifing server IP
            		String serverIP = socket.getInetAddress().getHostAddress();
            		
            		// if HostnameList is define for the Agent, use it
            		if (remoteHostnameList != null) {
            			// if HostanmeList contain server IP, execute the request
						if (remoteHostnameList.contains(serverIP)) {
		            		xmlOutputData = Protocol.executeRequest(xmlInputData);
						} else {
							// Else return exception
							JityResponse response = new JityResponse();
							response.setInstructionResultOK(false);
							response.setExceptionName("AgentException");
							response.setExceptionMessage("Server IP "+serverIP+" not allowed for this agent. Closing connection");
							xmlOutputData = response.toXML();
						}
					} else {
						// if HostnameList is not define, execute the request whithout control
	            		xmlOutputData = Protocol.executeRequest(xmlInputData);
					}
   
            		// Send the response to server
                	networkWriter.println(xmlOutputData);
                    networkWriter.flush();

                    logger.debug("Server data send: " + xmlOutputData);

            	 } catch (ProtocolException ex) {
                     logger.warn("Bad input data: " + xmlInputData);
                     logger.debug(ex.getMessage());
            	 }
 //           }
        } catch (IOException ex) {
            logger.warn("Server communication was ended abnormally.");
            logger.debug(ex.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                logger.warn("Failed to close server socket");
            }
        }
    }
}