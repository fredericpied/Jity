package org.jity.server.protocol;

import org.jity.common.XMLUtil;

public class Datagram {

	/**
	 * Datagram format version
	 */
	public static final int DATAGRAM_VERSION=1;
	
	public static int getDATAGRAM_VERSION() {
		return DATAGRAM_VERSION;
	}
	
	/**
	 * Return XML code of the datagram
	 * @return
	 */
	public String getXML() {
		return XMLUtil.objectToXMLString(this).replaceAll("\n", "");
	}
				
}
