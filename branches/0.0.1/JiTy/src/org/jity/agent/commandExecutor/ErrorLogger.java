package org.jity.agent.commandExecutor;

import org.apache.log4j.Logger;


public class ErrorLogger implements ILogDevice {
	private static final Logger logger = Logger.getLogger(ErrorLogger.class);
	
	public void log(String str) {
		logger.error(str);
	}

}
