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
	public static final String XML_FILE_NAME = "conf/AgentConfig.xml";

	/**
	 * Agent logical name
	 */
	public String AGENT_NAME;
	
	/**
	 * Listening port (default 2611)
	 */
	public int AGENT_PORT = 2611;
	
	/**
	 * Agent description
	 */
	public String AGENT_DESC;
	
	/**
	 * Task queue pooling cycle in second (default 10)
	 */
	public int AGENT_POOLING_CYCLE = 10;
	
	/**
	 * List of the server hostname who are autorized to launch task
	 * on this agent
	 */
	public ArrayList<String> HOSTNAME_LIST;
	
	/**
	 * Number max of jobs in queue (default 50)
	 */
	public int MAX_JOBS_IN_QUEUE = 50;
	
	/**
	 * Number max of jobs executing at same time (default 10)
	 */
	public int MAX_CONCURRENT_JOBS = 50;
	
	/**
	 * Root directory of jobs log file
	 */
	public String JOBS_LOGS_DIR;
	

	public void showConfig() {
		
		for (int i=0;i<this.getClass().getDeclaredFields().length;i++) {
			String fieldName = this.getClass().getDeclaredFields()[i].getName();
			String fieldType = this.getClass().getDeclaredFields()[i].getType().getSimpleName();
			try {
				if (fieldName.equals("logger") || fieldName.equals("instance")) continue;
				
				if (fieldType.equals("String")) {
					logger.info(fieldName+"="+this.getClass().getField(fieldName).get(this));
				} else if (fieldType.equals("int")) {
					logger.info(fieldName+"="+String.valueOf(this.getClass().getField(fieldName).get(this)));
				}
			} catch (IllegalArgumentException e) {
				logger.warn(fieldName+" can't access to field value");
			} catch (SecurityException e) {
				logger.warn(fieldName+" can't access to field value");
			} catch (IllegalAccessException e) {
				logger.warn(fieldName+" can't access to field value");
			} catch (NoSuchFieldException e) {
				logger.warn(fieldName+" can't access to field value");
			}

		}
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

	public int getAGENT_PORT() {
		return AGENT_PORT;
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

}
