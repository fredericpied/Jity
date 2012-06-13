/**
 * JiTy : Job Scheduler Copyright (C) 2004
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA
 * 
 * For questions, suggestions:
 * 
 * cdrik13@sourceforge.net
 *  
 */

package org.jity.server;


import java.io.*;
import java.net.*;
import org.apache.log4j.Logger;
import org.jity.server.protocol.Protocol;
import org.jity.server.protocol.ProtocolException;

public class ServeOneClient extends Thread {
	private static final Logger logger = Logger.getLogger(ServeOneClient.class);
	
    private Socket socket;
    private BufferedReader lectureReseau;
    private PrintWriter ecritureReseau;


    public ServeOneClient(Socket s) throws IOException {
        this.socket = s;
        this.lectureReseau = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.ecritureReseau = new PrintWriter(socket.getOutputStream());
        start();
    }

    public void run() {
        String xmlInputData = null;
        String xmlOutputData;

        try {
            while (true) {
            	xmlInputData = lectureReseau.readLine();
                
            	if (xmlInputData == null) break;
                
            	try {
            		
            		logger.debug("Client data received: " + xmlInputData);
            		
            		xmlOutputData = Protocol.executeRequest(xmlInputData);
                    
                	ecritureReseau.println(xmlOutputData);
                    ecritureReseau.flush();

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