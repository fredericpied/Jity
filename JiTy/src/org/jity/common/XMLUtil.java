package org.jity.common;

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
