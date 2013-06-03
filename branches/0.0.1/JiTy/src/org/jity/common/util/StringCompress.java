package org.jity.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * Abstract utility classe to compress String
 * @author 09344a
 *
 */
public abstract class StringCompress {
	private static final Logger logger = Logger.getLogger(StringCompress.class);
	
	/**
	 * Compress (GZIP) input String
	 * @param inputString
	 * @return String
	 * @throws IOException
	 */
	public static String compress(String inputString) throws IOException {

		// If inputString is empty, return empty
		if (inputString == null || inputString.length() == 0) {
			return inputString;
		}
		
		logger.trace("Input String length : " + inputString.length());
				
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(inputString.getBytes());
		gzip.close();
		
		String compressedString = new Base64().encodeToString(obj.toByteArray());
		
		obj.close();
		
		logger.trace("Output Compressed String length : " + compressedString.length());
		
		return compressedString;
	}
	
	/**
	 * Decompress (GZIP) input String
	 * @param compressedString
	 * @return
	 * @throws IOException
	 */
	public static String decompress(String compressedString) throws IOException {
		
		// If empty, return empty
		if (compressedString == null || compressedString.length() == 0) {
			return compressedString;
		}
		
		logger.trace("Input Compressed String length : " + compressedString.length());

		byte[] decordedValue = new Base64().decode(compressedString);
		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(decordedValue));
		BufferedReader bf = new BufferedReader(new InputStreamReader(gis));
		
		String outStr = "";
		String line;
				
		while ((line = bf.readLine()) != null) {
			outStr += line;
		}
		
		gis.close();
		bf.close();
		
		logger.trace("Output String lenght : " + outStr.length());
		
		return outStr;
	}
		
	public static void main(String[] args) throws Exception {

        String stringToCompress ="<org.jity.common.protocol.JityRequest>  <instructionName>ADDTASKINQUEUE</instructionName>  " +
		"<xmlInputData>&lt;org.jity.common.referential.ExecTask&gt;  &lt;id&gt;1&lt;/id&gt;  &lt;execDate&gt" +
		";2013-05-29 22:00:00.0 UTC&lt;/execDate&gt;  &lt;status&gt;2&lt;/status&gt;  &lt;job&gt;    &lt;id&" +
		"gt;1&lt;/id&gt;    &lt;name&gt;JOB1&lt;/name&gt;" +
		"    &lt;hostName&gt;localhost&lt;/hostName&gt;    &lt;hostPort&gt;2611&lt;/hostPort&gt;    &lt;comman" +
		"dPath&gt;d:\temp\test.bat&lt;/commandPath&gt;    &lt;isEnable&gt;true&lt;/isEnable&gt;    &lt;dateCon" +
		"straint class=&quot;org.jity.common.referential.dateConstraint.DateConstraint_$$_javassist_1&quot; r" +
		"esolves-to=&quot;org.jity.common.referential.dateConstraint.DateConstraint&quot;&gt;      &lt;id&gt;" +
		"1&lt;/id&gt;      &lt;planifRule&gt;not_3_open_day_week&lt;/planifRule&gt;      &lt;persCalendar cl" +
		"ass=&quot;org.jity.common.referential.dateConstraint.PersonnalCalendar_$$_javassist_4&quot; resolves" +
		"-to=&quot;org.jity.common.referential.dateConstraint.PersonnalCalendar&quot;&gt;        &lt;id&gt;2&lt;/id&gt;" +
		"        &lt;name&gt;Cal6OpenDays2013&lt;/name&gt;        &lt;year&gt;2013&lt;/year&gt;        &lt;days&gt;" +
		"COOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCCOOOOOCOOOOOOC" +
		"OOOOOOCOOOOOOCOOCOOOCOOCCOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOO" +
		"OCOOOOOOCOOOOOOCOOOCOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOCOCOOO" +
		"OOOCCOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOOOOOCOOCOOOCOO&lt;/days&gt;      &lt;/persCalendar&gt;    &lt;" +
		"/dateConstraint&gt;  &lt;/job&gt;&lt;/org.jity.common.referential.ExecTask&gt;</xmlInputData></org.jity" +
		".common.protocol.JityRequest>";
 


		System.out.println("String to compress length: "+stringToCompress.length());
		String compressed = compress(stringToCompress); 
		System.out.println("Compressed string length: "+compressed.length());
		String decompressed = decompress(compressed);
		System.out.println("decompressed string length: "+decompressed.length());
		System.out.println("Egality: "+stringToCompress.equals(decompressed));
		
                
    }

	
	
	
}
