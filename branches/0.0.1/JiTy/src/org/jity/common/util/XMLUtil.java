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
package org.jity.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public abstract class XMLUtil {

	/**
	 * Serialize Java Object to XML file
	 * 
	 * @param object
	 * @param xmlFile
	 * @throws IOException
	 */
	public static void objectToXMLFile(Object object, File xmlFile)
			throws IOException {

		// New XStream instance
		XStream xstream = new XStream(new DomDriver());

		xstream.autodetectAnnotations(true);

		FileOutputStream fos = new FileOutputStream(xmlFile);
		try {

			xstream.toXML(object, fos);

		} finally {

			fos.close();
		}
	}
	
	/**
	 * Serialize Java Object to a XML String
	 * @param object
	 * @return
	 */
	public static String objectToXMLString(Object object) {
		// New XStream instance
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(object);
	}
	
	
	/**
	 * Deserialize XML String to a Java Object
	 * @param object
	 * @return
	 */
	public static Object XMLStringToObject(String xml) {
		XStream xstream = new XStream(new DomDriver());
		return xstream.fromXML(xml);
	}
	
	/**
	 * Deserialize XML file to a Java Object 
	 * @param xmlFile
	 * @return
	 * @throws IOException
	 */
	public static Object XMLFileToObject(File xmlFile) throws IOException {
		XStream xstream = new XStream(new DomDriver());
		return xstream.fromXML(xmlFile);
	}
	
	
	
	
	

}
