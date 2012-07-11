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
package org.jity.agent.commandExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * This class is used to log asynchronously error
 * output and standard output of the executed command
 * @author Fred
 */
public class AsynchronousStreamReader extends Thread {
	private static final Logger logger = Logger.getLogger(AsynchronousStreamReader.class);
	
	private StringBuffer fBuffer = null;
	private InputStream fInputStream = null;

	private boolean fStop = false;
	
	private LogDevice fLogDevice = null;

	private String fNewLine = null;

	/**
	 * Constructor of the asynchronous reader
	 * @param inputStream
	 * @param buffer
	 * @param logDevice
	 * @param threadId
	 */
	public AsynchronousStreamReader(InputStream inputStream, StringBuffer buffer,
			LogDevice logDevice, String threadId) {
		this.fInputStream = inputStream;
		this.fBuffer = buffer;
		this.fLogDevice = logDevice;
		this.fNewLine = System.getProperty("line.separator");
	}

	public String getBuffer() {
		return fBuffer.toString();
	}

	/**
	 * Launch reading
	 */
	public void run() {
		try {
			readCommandOutput();
		} catch (Exception ex) {
			logger.fatal(ex.getMessage());
		}
	}

	private void readCommandOutput() throws IOException {
		BufferedReader bufOut = new BufferedReader(new InputStreamReader(fInputStream));
		String line = null;
		while ((fStop == false) && ((line = bufOut.readLine()) != null)) {
			fBuffer.append(line + fNewLine);
			printToDisplayDevice(line);
		}
		bufOut.close();
	}

	/**
	 * Stop reading
	 */
	public void stopReading() {
		fStop = true;
	}

	private void printToDisplayDevice(String line) {
		if (fLogDevice != null)
			fLogDevice.log(line);
		else {
			logger.fatal("No logging device defined !!");
		}
	}

	private synchronized void printToConsole(String line) {
		System.out.println(line);
	}
}
