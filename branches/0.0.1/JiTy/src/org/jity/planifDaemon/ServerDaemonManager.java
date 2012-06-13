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
 */package org.jity.planifDaemon;

import org.apache.log4j.Logger;


public class ServerDaemonManager extends Thread {
	private static final Logger logger = Logger.getLogger(ServerDaemonManager.class);
	
    private static ServerDaemonManager instance = null;
    private ServerDaemon daemon;
    
    private boolean engineRunning = false;
    private String askShutdown = "";

    /**
     * Private Contrustor.
     */
    private ServerDaemonManager() {
    }

    /**
     * Get the instance of Engine Manage.
     */
    public static ServerDaemonManager getInstance() {
        if (instance == null) {
            instance = new ServerDaemonManager();
        }
        return instance;
    }

    /**
     * Ask the launch of the server engine.
     */
    public void startEngine() {
        if (!isEngineRunning()) {
            setDaemon(new ServerDaemon());
            getDaemon().startPlanifDaemon();
            setEngineRunning(true);
        }
    }

    /**
     * Ask the shutdown of the server engine.
     */
    public void shutdownEngine() {
        if (isEngineRunning()) {
            getDaemon().stopPlanifDaemon();
            setEngineRunning(false);
        }
    }

    public boolean isEngineRunning() {
        return engineRunning;
    }

    public void setEngineRunning(boolean engineRunning) {
        this.engineRunning = engineRunning;
    }

    public ServerDaemon getDaemon() {
        return daemon;
    }

    public void setDaemon(ServerDaemon daemon) {
        this.daemon = daemon;
    }

}