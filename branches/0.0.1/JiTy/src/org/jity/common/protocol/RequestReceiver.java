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
package org.jity.common.protocol;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.Protocol;
import org.jity.common.protocol.ProtocolException;

/**
 * Receive a JiTy Request by network
 */
public class RequestReceiver extends Thread {
	private static final Logger logger = Logger.getLogger(RequestReceiver.class);
	
    private Socket socket;
    private BufferedReader networkReader;
    private PrintWriter networkWriter;

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
     * Thread code
     */
    public void run() {
        String xmlInputData = null;
        String xmlOutputData;

        try {
        
            	// Reading netword datagram
            	xmlInputData = networkReader.readLine();
           		
            	try {
            		logger.trace("Request received: " + xmlInputData);

            		// Identifing remote IP
            		String remoteIP = socket.getInetAddress().getHostAddress();

            		xmlOutputData = Protocol.executeRequest(xmlInputData, remoteIP);
   
            		// Send the response to server
                	networkWriter.println(xmlOutputData);
                    networkWriter.flush();

                    logger.trace("Response send: " + xmlOutputData);

            	 } catch (ProtocolException ex) {
                     logger.warn("Bad input data: " + xmlInputData);
                     logger.debug(ex.getMessage());
            	 }

        } catch (IOException ex) {
            logger.warn("Communication was ended abnormally");
            logger.debug(ex.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                logger.warn("Failed to close socket");
            }
        }
    }
}