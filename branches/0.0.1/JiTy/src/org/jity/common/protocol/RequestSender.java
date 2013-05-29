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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.util.StringCompress;
import org.jity.common.util.StringCrypter;
import org.jity.common.util.XMLUtil;

/**
 * Request Launcher is used to create a JiTyRequest, connect to remote host, submit the request
 * and read the response
 *
 */
public class RequestSender {
	private static final Logger logger = Logger.getLogger(RequestSender.class);

	// Socket used to dialog with the remote host
	private Socket sock = null;
	private BufferedReader sin;
	private PrintWriter sout;


	/**
	 * Cryptage des communications
	 */
	private static boolean CRYPTED_MESSAGE = true;
	private static String KEY_STRING = "JiTyCedricFred13";
	
	
	/**
	 * Open remote connection
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void openConnection(String host, int port)
			throws UnknownHostException, IOException {
		// open socket
		sock = new Socket(host, port);
		// Init reader and writer
		sin = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		sout = new PrintWriter(sock.getOutputStream());
	}

	/**
	 * Close the connection
	 * @throws IOException
	 */
	public void closeConnection() throws IOException {
		if (sock != null)
			sock.close();
	}

	/**
	 * Send Request to remote host
	 * @param request
	 * @return JityResponse
	 */
	public JityResponse sendRequest(JityRequest request) {

		JityResponse response = null;
		
		try {
	
			String message = request.toXML();
			
    		logger.trace("Request send: " + message);
			
    		if (CRYPTED_MESSAGE) {
    			message = StringCrypter.encrypt(message, KEY_STRING);
    			//message = StringCompress.compress(message);
    		}
    		
    		// Send request
			this.sout.println(message);
			this.sout.flush();
			
			// Reading response
			String returnedMessage = this.sin.readLine();
			
			if (CRYPTED_MESSAGE) {
				//returnedMessage = StringCompress.decompress(returnedMessage);
				returnedMessage = StringCrypter.decrypt(returnedMessage, KEY_STRING);
			}

			logger.trace("Response received: " + returnedMessage);
			
			response = (JityResponse) XMLUtil.XMLStringToObject(returnedMessage);
			
		} catch (IOException e) {
			response = new JityResponse();
			response.setException(e);
			logger.error("Exception: " + e.getClass().getSimpleName()+":"+e.getMessage());
		} catch (InvalidKeyException e) {
			response = new JityResponse();
			response.setException(e);
			logger.error("Exception: " + e.getClass().getSimpleName()+":"+e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			response = new JityResponse();
			response.setException(e);
			logger.error("Exception: " + e.getClass().getSimpleName()+":"+e.getMessage());
		} catch (NoSuchPaddingException e) {
			response = new JityResponse();
			response.setException(e);
			logger.error("Exception: " + e.getClass().getSimpleName()+":"+e.getMessage());
		} catch (IllegalBlockSizeException e) {
			response = new JityResponse();
			response.setException(e);
			logger.error("Exception: " + e.getClass().getSimpleName()+":"+e.getMessage());
		} catch (BadPaddingException e) {
			response = new JityResponse();
			response.setException(e);
			logger.error("Exception: " + e.getClass().getSimpleName()+":"+e.getMessage());
		}
		
		return response;
	}
}
