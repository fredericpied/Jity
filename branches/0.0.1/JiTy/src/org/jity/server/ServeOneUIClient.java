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

/**
 * Thread launch for every client connexion.
 */
import java.io.*;
import java.net.*;
import org.apache.log4j.Logger;
import org.jity.protocol.Protocol;
import org.jity.protocol.ProtocolException;

public class ServeOneUIClient extends Thread {
	private static final Logger logger = Logger.getLogger(ServeOneUIClient.class);
	
    private Socket socket;
    private BufferedReader networkReader;
    private PrintWriter networkWriter;


    /**
     * Construct the thread and launch it
     * @param s
     * @throws IOException
     */
    public ServeOneUIClient(Socket s) throws IOException {
        this.socket = s;
        this.networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.networkWriter = new PrintWriter(socket.getOutputStream());
        start();
    }

    
    public void run() {
        String xmlInputData = null;
        String xmlOutputData;

        try {
        	
            while (true) {
    
            	xmlInputData = networkReader.readLine();
                
            	if (xmlInputData == null) break;
                
            	try {
            		
            		logger.debug("Client data received: " + xmlInputData);
            		
            		xmlOutputData = Protocol.executeRequest(xmlInputData);
                    
                	networkWriter.println(xmlOutputData);
                    networkWriter.flush();

                    logger.debug("Client data send: " + xmlOutputData);

            	 } catch (ProtocolException ex) {
                     logger.warn("Bad input data: " + xmlInputData);
                     logger.debug(ex.getMessage());
            	 }
            }
        } catch (IOException ex) {
            logger.warn("Client communication was ended abnormally.");
            logger.debug(ex.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                logger.warn("Failed to close client socket");
            }
        }
    }
}