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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.Protocol;
import org.jity.common.protocol.ProtocolException;
import org.jity.common.util.StringCompress;
import org.jity.common.util.StringCrypter;

/**
 * Receive a JiTy Request by network
 */
public class RequestReceiver extends Thread {
	private static final Logger logger = Logger.getLogger(RequestReceiver.class);
	
    private Socket socket;
    private BufferedReader networkReader;
    private PrintWriter networkWriter;
	
    /**
	 * Cryptage des communications
	 */
	private static boolean CRYPTED_MESSAGE = true;
	private static String KEY_STRING = "JiTyCedricFred13";

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
            		
            		if (CRYPTED_MESSAGE) {
                		xmlInputData = StringCrypter.decrypt(xmlInputData, KEY_STRING);
                		xmlInputData = StringCompress.decompress(xmlInputData);
            		}
            		
            		// Identifing remote IP
            		String remoteIP = socket.getInetAddress().getHostAddress();

            		xmlOutputData = Protocol.executeRequest(xmlInputData, remoteIP);
            		
                    logger.trace("Response send: " + xmlOutputData);
                    
            		if (CRYPTED_MESSAGE) {
                		xmlOutputData = StringCompress.compress(xmlOutputData);
            			xmlOutputData = StringCrypter.encrypt(xmlOutputData, KEY_STRING);
            		}
            		            		
            		// Send the response to server
                	networkWriter.println(xmlOutputData);
                    networkWriter.flush();


            	 } catch (ProtocolException e) {
                     logger.warn("Bad input data: " + xmlInputData);
         			logger.error("Exception: " + e.getClass().getSimpleName()+":"+e.getMessage());
            	 } catch (InvalidKeyException e) {
                    logger.warn("Bad input data: " + xmlInputData);
         			logger.error("Exception: " + e.getClass().getSimpleName()+":"+e.getMessage());
				} catch (NoSuchAlgorithmException e) {
                    logger.warn("Bad input data: " + xmlInputData);
        			logger.error("Exception: " + e.getClass().getSimpleName()+":"+e.getMessage());
				} catch (NoSuchPaddingException e) {
                    logger.warn("Bad input data: " + xmlInputData);
        			logger.error("Exception: " + e.getClass().getSimpleName()+":"+e.getMessage());
				} catch (IllegalBlockSizeException e) {
                    logger.warn("Bad input data: " + xmlInputData);
        			logger.error("Exception: " + e.getClass().getSimpleName()+":"+e.getMessage());
				} catch (BadPaddingException e) {
                    logger.warn("Bad input data: " + xmlInputData);
        			logger.error("Exception: " + e.getClass().getSimpleName()+":"+e.getMessage());
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