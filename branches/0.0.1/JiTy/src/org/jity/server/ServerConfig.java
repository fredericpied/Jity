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

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jity.common.util.XMLUtil;

/**
 * Server configuration Class
 * 
 * @author 09344A
 * 
 */
public class ServerConfig {
	private static final Logger logger = Logger.getLogger(ServerConfig.class);
	
	private static ServerConfig instance = null;

	/**
	 * XML File
	 */
	public static final String XML_FILE_NAME = "conf/ServerConfig.xml";

	/**
	 * Server logical name
	 */
	public String SERVER_NAME;

	/**
	 * Server description
	 */
	public String SERVER_DESCRIPTION;
	
	/**
	 * Server listening port for UI (default 2610)
	 */
	public int SERVER_UI_INPUT_PORT = 2610;

	/**
	 * Server listening port for agent (default 2612)
	 */
	public int SERVER_INPUT_PORT = 2612;
	
	/**
	 * Job constraints pooling cycle in second (default 10)
	 */
	public int SERVER_POOLING_CYCLE = 10;

	/**
	 * Agent listening port (default 2611)
	 */
	public int AGENT_INPUT_PORT = 2611;

	
	public static ServerConfig getInstance() {
		if (instance == null)
			instance = new ServerConfig();
		return instance;
	}

	public void initialize() throws IOException {
		instance = (ServerConfig) XMLUtil.XMLFileToObject(new File(
				XML_FILE_NAME));
	}

	public void generate(File xmlFile) throws IOException {
		XMLUtil.objectToXMLFile(instance, xmlFile);
	}

	public void showConfig() {
		logger.info("SERVER_NAME="+ServerConfig.getInstance().getSERVER_NAME());
		logger.info("SERVER_DESCRIPTION="+ServerConfig.getInstance().getSERVER_DESCRIPTION());
		logger.info("SERVER_UI_INPUT_PORT="+ServerConfig.getInstance().getSERVER_UI_INPUT_PORT());
		logger.info("SERVER_INPUT_PORT="+ServerConfig.getInstance().getSERVER_INPUT_PORT());
		logger.info("SERVER_POOLING_CYCLE="+ServerConfig.getInstance().getSERVER_POOLING_CYCLE());
		logger.info("AGENT_INPUT_PORT="+ServerConfig.getInstance().getAGENT_INPUT_PORT());
	}
	
	public int getSERVER_UI_INPUT_PORT() {
		return SERVER_UI_INPUT_PORT;
	}

	public int getSERVER_POOLING_CYCLE() {
		return SERVER_POOLING_CYCLE;
	}

	public int getAGENT_INPUT_PORT() {
		return AGENT_INPUT_PORT;
	}

	public static String getXmlFileName() {
		return XML_FILE_NAME;
	}

	public String getSERVER_NAME() {
		return SERVER_NAME;
	}

	public String getSERVER_DESCRIPTION() {
		return SERVER_DESCRIPTION;
	}
	
	public int getSERVER_INPUT_PORT() {
		return SERVER_INPUT_PORT;
	}
	
	
}
