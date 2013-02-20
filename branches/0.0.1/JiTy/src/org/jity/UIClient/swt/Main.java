package org.jity.UIClient.swt;

import org.apache.log4j.Logger;

import org.jity.common.commandLine.CommandLine;

public class Main extends CommandLine {
	private static final Logger logger = Logger.getLogger(Main.class);
		
	private static int launch(String[] args) {

		MainWindows m = new MainWindows();
		m.launch();
		return 0;
		
		//this.loadArguments(args);
//
//		if (args.length != 1) {
//			throw new BadArgCLException("Incorrect arguments number (require 1 argument)");
//		}
//		
//		if (args[0].equals("-stop")) {
//
//			return stop();
//			
//		} else if (args[0].equals("-start")) {
//			
//			return start();
//			
//		} else {
//			throw new BadArgCLException(args[0]+" is not a valid parameter");
//		}
		
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		
		try {

			int returnCode = main.launch(args);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}
