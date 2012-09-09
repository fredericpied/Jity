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
package org.jity.agent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jity.common.util.ListUtil;
import org.jity.common.util.XMLUtil;
import org.jity.server.ServerConfig;

/**
 * Agent configuration Class
 * @author Fred
 * 
 */
public class AgentConfig {
	private static final Logger logger = Logger.getLogger(AgentConfig.class);
	
	private static AgentConfig instance = null;

	/**
	 * XML File
	 */
	private static final String XML_FILE_NAME = "conf/AgentConfig.xml";

	/**
	 * Agent logical name
	 */
	private String AGENT_NAME;
	
	/**
	 * Listening port (default 2611)
	 */
	private int AGENT_INPUT_PORT = 2611;
	
	/**
	 * Server listening port for agent (default 2612)
	 */
	public int SERVER_INPUT_PORT = 2612;

	/**
	 * Agent description
	 */
	private String AGENT_DESC;
	
	/**
	 * Task queue pooling cycle in second (default 10)
	 */
	private int AGENT_POOLING_CYCLE = 10;
	
	/**
	 * List of the server hostname who are autorized to launch task
	 * on this agent
	 */
	private ArrayList<String> HOSTNAME_LIST = new ArrayList<String>();
	
	/**
	 * Number max of jobs in queue (default 50)
	 */
	private int MAX_JOBS_IN_QUEUE = 50;
	
	/**
	 * Number max of jobs executing at same time (default 10)
	 */
	private int MAX_CONCURRENT_JOBS = 50;
	
	/**
	 * Root directory of jobs log file
	 */
	private String JOBS_LOGS_DIR;
	

	public void showConfig() {
		logger.info("XML_FILE_NAME="+AgentConfig.getInstance().getXmlFileName());
		logger.info("AGENT_NAME="+AgentConfig.getInstance().getAGENT_NAME());
		logger.info("AGENT_DESC="+AgentConfig.getInstance().getAGENT_DESC());
		logger.info("AGENT_INPUT_PORT="+AgentConfig.getInstance().getAGENT_INPUT_PORT());
		logger.info("SERVER_INPUT_PORT="+AgentConfig.getInstance().getSERVER_INPUT_PORT());
		logger.info("AGENT_POOLING_CYCLE="+AgentConfig.getInstance().getAGENT_POOLING_CYCLE());
		logger.info("HOSTNAME_LIST="+ListUtil.ArrayListToString(AgentConfig.getInstance().getHOSTNAME_LIST()));
		logger.info("JOBS_LOGS_DIR="+AgentConfig.getInstance().getJOBS_LOGS_DIR());
		logger.info("MAX_CONCURRENT_JOBS="+AgentConfig.getInstance().getMAX_CONCURRENT_JOBS());
		logger.info("MAX_JOBS_IN_QUEUE="+AgentConfig.getInstance().getMAX_JOBS_IN_QUEUE()); 
	}
	
	public String getAGENT_NAME() {
		return AGENT_NAME;
	}

	public boolean hostnameListSet() {
		if (this.HOSTNAME_LIST ==  null) {
			return false;
		} else if (this.HOSTNAME_LIST.size() == 0) {
			return false;
		} else if (this.HOSTNAME_LIST.size() > 0) {
			return true;
		}
		return false;
	}
	
	public static AgentConfig getInstance() {
		if (instance == null)
			instance = new AgentConfig();
		return instance;
	}

	public void initialize() throws IOException {
		instance = (AgentConfig) XMLUtil.XMLFileToObject(new File(
				XML_FILE_NAME));
	}

	public void generate(File xmlFile) throws IOException {
		XMLUtil.objectToXMLFile(instance, xmlFile);
	}

	public int getAGENT_INPUT_PORT() {
		return AGENT_INPUT_PORT;
	}

	public static String getXmlFileName() {
		return XML_FILE_NAME;
	}

	public String getAGENT_DESC() {
		return AGENT_DESC;
	}

	public ArrayList<String> getHOSTNAME_LIST() {
		return HOSTNAME_LIST;
	}

	public String getJOBS_LOGS_DIR() {
		return JOBS_LOGS_DIR;
	}
	
	public int getAGENT_POOLING_CYCLE() {
		return AGENT_POOLING_CYCLE;
	}

	public int getMAX_JOBS_IN_QUEUE() {
		return MAX_JOBS_IN_QUEUE;
	}

	public int getMAX_CONCURRENT_JOBS() {
		return MAX_CONCURRENT_JOBS;
	}
	
	public int getSERVER_INPUT_PORT() {
		return SERVER_INPUT_PORT;
	}
	
}
