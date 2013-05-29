package org.jity.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class StringCompress {

	public static String compress(String inputString) throws IOException {

		if (inputString == null || inputString.length() == 0) {
			return inputString;
		}
		
		System.out.println("Input String length : " + inputString.length());
				
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(inputString.getBytes("ISO-8859-1"));
		gzip.close();

		String compressedString = obj.toString("ISO-8859-1");
				
		System.out.println("Output String length : " + compressedString.length());
		
		return compressedString;
	}

	public static String decompress(String compressedString) throws IOException {
		
		if (compressedString == null || compressedString.length() == 0) {
			return compressedString;
		}
		
		System.out.println("Input length : " + compressedString.length());
		
		byte[] bytes = compressedString.getBytes("ISO-8859-1");
				
		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
		BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));
		String outStr = "";
		String line;
		while ((line = bf.readLine()) != null) {
			outStr += line;
		}
		System.out.println("Output String lenght : " + outStr.length());
		return outStr;
	}

	public static void main(String[] args) throws Exception {
        String string = "I am what I am hhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
                + "bjggujhhhhhhhhh"
                + "rggggggggggggggggggggggggg"
                + "esfffffffffffffffffffffffffffffff"
                + "esffffffffffffffffffffffffffffffff"
                + "esfekfgy enter code here`etd`enter code here wdd"
                + "heljwidgutwdbwdq8d"
                + "skdfgysrdsdnjsvfyekbdsgcu"
                +"jbujsbjvugsduddbdj";

        System.out.println("after compress:");
       	String compressed = compress(string); //In the main method
        //System.out.println(compressed);
        System.out.println("after decompress:");
        String decomp = decompress(compressed);
        System.out.println(decomp);
    }

	
	
	
}
