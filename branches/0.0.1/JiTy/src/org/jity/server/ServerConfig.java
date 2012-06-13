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

import org.jity.common.XMLUtil;

/**
 * Server configuration Class
 * 
 * @author 09344A
 * 
 */
public class ServerConfig {

	private static ServerConfig instance = null;

	/**
	 * XML File
	 */
	private static final String XML_FILE_NAME = "conf/ServerConfig.xml";

	/**
	 * Listening port (default 2610)
	 */
	public int SERVER_PORT = 2610;

	/**
	 * Job constraints pooling cycle in second (default 10)
	 */
	public int SERVER_POOLING_CYCLE = 10;

	/**
	 * DB server hostname
	 */
	public String DB_SERVER_HOSTNAME;

	/**
	 * DB server port
	 */
	public int DB_SERVER_PORT;

	/**
	 * DB name
	 */
	public String DB_NAME;

	/**
	 * DB username
	 */
	public String DB_USERNAME;

	/**
	 * DB Password
	 */
	public String DB_PASSWORD;

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

	public int getSERVER_PORT() {
		return SERVER_PORT;
	}

	public int getSERVER_POOLING_CYCLE() {
		return SERVER_POOLING_CYCLE;
	}

	public static String getXmlFileName() {
		return XML_FILE_NAME;
	}

	public String getDB_SERVER_HOSTNAME() {
		return DB_SERVER_HOSTNAME;
	}

	public int getDB_SERVER_PORT() {
		return DB_SERVER_PORT;
	}

	public String getDB_NAME() {
		return DB_NAME;
	}

	public String getDB_USERNAME() {
		return DB_USERNAME;
	}

	public String getDB_PASSWORD() {
		return DB_PASSWORD;
	}

}
