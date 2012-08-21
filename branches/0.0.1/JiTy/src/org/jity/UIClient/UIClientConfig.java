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
package org.jity.UIClient;

import java.io.File;
import java.io.IOException;

import org.jity.common.util.XMLUtil;

/**
 * client configuration Class
 * 
 * @author 09344A
 * 
 */
public class UIClientConfig {

	private static UIClientConfig instance = null;

	/**
	 * XML File
	 */
	private static final String XML_FILE_NAME = "conf/UIClientConfig.xml";

	/**
	 * server hostname (default localhost)
	 */
	public String SERVER_HOSTNAME = "localhost";
	
	/**
	 * Listening port (default 2610)
	 */
	public int SERVER_PORT = 2610;
	
	/**
	 * Client name (default "unknown")
	 */
	public String CLIENT_NAME = "unknown";
	
	/** 
	 * Client description (default "no description")
	 */
	public String CLIENT_DESC = "no description";
	
	public static UIClientConfig getInstance() {
		if (instance == null)
			instance = new UIClientConfig();
		return instance;
	}

	
	public void initialize() throws IOException {
		instance = (UIClientConfig) XMLUtil.XMLFileToObject(new File(
				XML_FILE_NAME));
	}

	public void generate(File xmlFile) throws IOException {
		XMLUtil.objectToXMLFile(instance, xmlFile);
	}

	public int getSERVER_PORT() {
		return SERVER_PORT;
	}
	
	public String getSERVER_HOSTNAME() {
		return SERVER_HOSTNAME;
	}

	public static String getXmlFileName() {
		return XML_FILE_NAME;
	}

}
