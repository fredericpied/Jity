package org.jity.ui.commandLine;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommandLine {
	
	private static final String banner = "JiTy: The Opensource Job Scheduler - http://www.jity.org";
		
	/**
	 * Name of the Class wich support command line function
	 */
	private final String commandLineName = this.getClass().getSimpleName().toLowerCase()+" command line help";
	
	/**
	 * The line seperator character for the OS
	 */
	private final static String lineSeparator = System.getProperty("line.separator");
	
	/**
	 * Usage message
	 */
	private String usage = "Default usage message";
	
	/**
	 * Help message
	 */
	private String helpMessage = "Default help message";
	
	/**
	 * Command line minimum number of arguments
	 */
	private int minArgsNumber = 0;

	/**
	 * Command line maximum number of arguments
	 */
	private int maxArgsNumber = 0;

	/**
	 * HashMap to store command line couple argName/argValue
	 */
	private HashMap<String, String> hashMapArguments = new HashMap<String, String>();
	
	/**
	 * Loads arguments in the HaspMap
	 * @param args
	 * @throws BadArgCLException
	 */
	public void loadArguments(String args[]) throws BadArgCLException {
		
		// Test min. and max. args number
		testArgsNumber(args);
		
		int argIndex = 0;
		
		while (argIndex < args.length-1) {
			// Name of the arguments is index of the hashmap
			this.hashMapArguments.put(args[argIndex], args[argIndex+1]);
			argIndex = argIndex + 2;
		}
		
	}
	
	/**
	 * Test number of arguments
	 * @param args
	 * @throws BadArgCLException
	 */
	private void testArgsNumber(String args[]) throws BadArgCLException {
		if (args.length < minArgsNumber || args.length > maxArgsNumber) {
			if (minArgsNumber != maxArgsNumber) {
				throw new BadArgCLException("Incorrect arguments number (min. "
						+minArgsNumber/2+" and max. "+maxArgsNumber+" arguments)");
			} else {
				throw new BadArgCLException("Incorrect arguments number (require "
						+minArgsNumber/2+ " arguments)");
			}
		}
	}
	
	public void showHelpMessage() {
		showHelpMessage(null);
	}
	
	public void showHelpMessage(String commandLineError) {
		
		System.out.println(banner);
		
		System.out.println(commandLineName);
		for (int i=0;i<helpMessage.length();i++) {
			if (helpMessage.charAt(i) == '\n') {
				System.out.print(lineSeparator);
			} else {
				System.out.print(helpMessage.charAt(i));
			}
			
		}
		System.out.print(lineSeparator);
		
		System.out.println(usage);
		
		if (commandLineError != null && commandLineError.length() > 0) {
			System.err.println(commandLineError);
		}
		System.out.println();
	}

	public void showFileAccessError(File file) {
		showHelpMessage("ERROR: Cannot access to file "+file.getAbsolutePath()+".");
	}
	
	public File readFileToReadArguments(String argName) throws BadArgCLException {
		File file = new File(this.getHashMapArguments().get(argName));
		
		if (! file.canRead()) {
			throw new BadArgCLException(argName+": Cannot access "+file.getAbsolutePath());
		}
		
		return file;
	}
	
	public File readFileToWriteArguments(String argName) throws BadArgCLException {
		File file = new File(this.getHashMapArguments().get(argName));
		
		if (! file.exists() && file.getParentFile().canWrite()) {
			throw new BadArgCLException(argName+": Cannot create "+file.getAbsolutePath());
		}
		
		if (! file.canWrite()) {
			throw new BadArgCLException(argName+": Cannot access "+file.getAbsolutePath() + " in write mode");
		}
		
		
		
		return file;
	}
	
	public String readStringArguments(String argName) {
		return this.getHashMapArguments().get(argName);
	}
	
	public int readIntArguments(String argName) {
		return Integer.valueOf(this.getHashMapArguments().get(argName));
	}
	
	public void testRequiredArg(String argName) throws BadArgCLException {
		if (! this.getHashMapArguments().containsKey(argName)) {
			throw new BadArgCLException(argName+" is a required argument");
		}
	}
	
	public void showArgsValue() {

		Iterator<Map.Entry<String, String>> iterArgs = this.getHashMapArguments().entrySet().iterator();
		while (iterArgs.hasNext()) {
			Map.Entry<String, String> map = iterArgs.next();
			String argName = map.getKey();
			String argValue = map.getValue();
			
			System.out.println(argName+" = "+argValue);
		}
		
	}
	
	public String getBanner() {
		return banner;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getHelpMessage() {
		return helpMessage;
	}

	public void setHelpMessage(String helpMessage) {
		this.helpMessage = helpMessage;
	}

	public int getMinArgsNumber() {
		return minArgsNumber;
	}

	public void setMinArgsNumber(int minArgsNumber) {
		this.minArgsNumber = minArgsNumber;
	}

	public int getMaxArgsNumber() {
		return maxArgsNumber;
	}

	public void setMaxArgsNumber(int maxArgsNumber) {
		this.maxArgsNumber = maxArgsNumber;
	}

	public HashMap<String, String> getHashMapArguments() {
		return hashMapArguments;
	}

	
}
