package org.jity.common;

import org.apache.log4j.Logger;

public abstract class TestUtil {
	private static final Logger logger = Logger.getLogger(TestUtil.class);
	
	/**
	 * Wait somes seconds
	 * @param seconds
	 */
	public static void waiting(int seconds) {
		logger.info("Waiting "+seconds+" seconds...");
		
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) { }
		
		logger.info("End of Wait");
	}
	
}
