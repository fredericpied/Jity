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

import org.jity.common.XMLUtil;

/**
 * Agent configuration Class
 * @author Fred
 * 
 */
public class AgentConfig {

	private static AgentConfig instance = null;

	/**
	 * XML File
	 */
	private static final String XML_FILE_NAME = "conf/AgentConfig.xml";

	/**
	 * Listening port (default 2611)
	 */
	public int AGENT_PORT = 2611;
	
	/**
	 * Agent description
	 */
	public String AGENT_DESC;
	
	/**
	 * List of the server hostname who are autorized to launch task
	 * on this agent
	 */
	public ArrayList<String> HOSTNAME_LIST;
	
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

	public void setAGENT_DESC(String aGENTDESC) {
		AGENT_DESC = aGENTDESC;
	}

	public ArrayList<String> getHOSTNAME_LIST() {
		return HOSTNAME_LIST;
	}

	public void setHOSTNAME_LIST(ArrayList<String> hOSTNAMELIST) {
		HOSTNAME_LIST = hOSTNAMELIST;
	}

	public void setAGENT_PORT(int aGENTPORT) {
		AGENT_PORT = aGENTPORT;
	}
	

}
