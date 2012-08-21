package org.jity.common.util;

import org.apache.log4j.Logger;

public abstract class TimeUtil {
	private static final Logger logger = Logger.getLogger(TimeUtil.class);
	
	/**
	 * Wait somes seconds
	 * @param seconds
	 * @throws InterruptedException 
	 */
	public static void waiting(int seconds) throws InterruptedException {
		Thread.sleep(seconds * 1000);
	}
	
}
