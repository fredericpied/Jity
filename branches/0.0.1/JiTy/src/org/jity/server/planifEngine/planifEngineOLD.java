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
 */package org.jity.server.planifEngine;

import java.util.Calendar;
import org.apache.log4j.Logger;
import org.jity.server.ServerConfig;

public class planifEngineOLD implements Runnable {

	private static final Logger logger = Logger.getLogger(planifEngineOLD.class);

    Thread runner = null;
    volatile boolean shouldStop = false;

    /**
     * Start the planifDaemon in a Thread.
     */
    public synchronized void startPlanifDaemon() {
        if (runner == null) {
            runner = new Thread(this);
            runner.start();
        }
    }

    /**
     * Stop current planifDaemon if running.
     */
    public synchronized void stopPlanifDaemon() {
        if (runner != null) {
            shouldStop = true;
            runner.interrupt();
            runner = null;
        }
    }

    /**
     * Analyse all jobs in the referential to check constraints and manage their
     * state.
     */
    public void checkProg() {
       
    }

    /**
     * Launch jobs analyse each X seconds.
     */
    public void run() {
        int cycle = ServerConfig.getInstance().getSERVER_POOLING_CYCLE();
        while (!shouldStop) {
            try {
                logger.info("Start of job analyze.");
                checkProg();
                Calendar end = Calendar.getInstance();
                logger.info("End of job analyze.");
                Thread.sleep(cycle);
            } catch (InterruptedException ex) {
                logger.warn("Engine is stopped.");
                logger.debug(ex.toString());
            } catch (Exception ex) {
                logger.warn("Unknow error: " + ex.getMessage());
                logger.debug(ex.toString());
            }
        }
    }
}