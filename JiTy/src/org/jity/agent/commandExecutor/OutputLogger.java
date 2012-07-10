package org.jity.agent.commandExecutor;

import org.apache.log4j.Logger;


public class OutputLogger implements ILogDevice {
	private static final Logger logger = Logger.getLogger(OutputLogger.class);
	
	public void log(String str) {
		logger.info(str);
	}

}
