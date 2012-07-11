/**
 *  JiTy : Open Job Scheduler
 *  Copyright (C) 2012 
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free
 *  Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA 02111-1307, USA
 *
 *  For questions, suggestions:
 *
 *  http://www.assembla.com/spaces/jity
 *
 */package org.jity.server.planifDaemon;

import java.io.IOException;
import java.util.Calendar;
import org.apache.log4j.Logger;
import org.jity.common.TimeUtil;
import org.jity.server.Server;
import org.jity.server.ServerConfig;
import org.jity.server.ServerException;
import org.jity.server.database.Database;

public class PlanifDaemon implements Runnable {
	private static final Logger logger = Logger.getLogger(PlanifDaemon.class);

	private static PlanifDaemon instance = null;
	
    private Thread daemon = null;

    volatile boolean shouldStop = false;
    
	public static PlanifDaemon getInstance() {
		if (instance == null) {
			instance = new PlanifDaemon();
		}
		return instance;
	}
	
	/**
	 * Return true if planification engine is running
	 * 
	 * @return
	 */
	public synchronized boolean isRunning() {
		if (this.daemon != null)
			return this.daemon.isAlive();
		else
			return false;
	}
	
    /**
     * Start the planifDaemon in a Thread.
     */
    public synchronized void startPlanifDaemon() {
        if (daemon == null) {
            daemon = new Thread(this);
            daemon.start();
        }
    }

    /**
     * Stop current planifDaemon if running.
     */
    public synchronized void stopPlanifDaemon() {
        if (daemon != null) {
        	logger.info("Shutdown of PlanifDaemon asked.");
            shouldStop = true;
            daemon.interrupt();
            daemon = null;
			logger.info("PlanifDaemon successfuly shutdowned");
        }
    }

    /**
     * Analyse all jobs in the referential to check constraints and manage their
     * state.
     */
    public void checkJobs() {
    	 logger.info("checkJobs executing");
    }

    /**
     * Launch jobs analyse each X seconds.
     */
    public void run() {
        int cycle = ServerConfig.getInstance().getSERVER_POOLING_CYCLE();
        while (!shouldStop) {
            try {
                logger.info("Start of job analyze.");
                checkJobs();
                logger.info("End of job analyze.");
                TimeUtil.waiting(cycle);
            } catch (InterruptedException ex) {
                logger.warn("PlanifDaemon is stopped.");
                logger.debug(ex.toString());
            } catch (Exception ex) {
                logger.warn("Unknow error: " + ex.getMessage());
                logger.debug(ex.toString());
            }
        }
    }
}