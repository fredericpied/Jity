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
import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.jity.common.util.XMLUtil;
import org.jity.tests.TestServer;

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
	 * Listening port (default 2610)
	 */
	public int SERVER_UI_PORT = 2610;
	
	/**
	 * Job constraints pooling cycle in second (default 10)
	 */
	public int SERVER_POOLING_CYCLE = 10;

	/**
	 * Port to pool agent (default 2611)
	 */
	public int AGENT_PORT = 2611;

	
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
	
	public int getSERVER_UI_PORT() {
		return SERVER_UI_PORT;
	}

	public int getSERVER_POOLING_CYCLE() {
		return SERVER_POOLING_CYCLE;
	}

	public int getAGENT_PORT() {
		return AGENT_PORT;
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

	
	
}
