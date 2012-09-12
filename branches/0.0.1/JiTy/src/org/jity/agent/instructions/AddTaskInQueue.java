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
 */
package org.jity.agent.instructions;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jity.agent.Agent;
import org.jity.agent.AgentConfig;
import org.jity.agent.AgentException;
import org.jity.agent.AgentQueueManager;
import org.jity.common.protocol.Instruction;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.Protocol;
import org.jity.common.referential.ExecTask;
import org.jity.common.referential.Job;
import org.jity.common.util.DateUtil;
import org.jity.common.util.XMLUtil;

/**
 * 
 * @author 09344A
 *
 */
public class AddTaskInQueue {
	private static final Logger logger = Logger.getLogger(AddTaskInQueue.class);

	public JityResponse launch(String xmlInputData, String remoteIP) {
		JityResponse response = new JityResponse();
		
		try {
			
			// if hostnameList is define, use it
    		if (AgentConfig.getInstance().getHOSTNAME_LIST() != null) {
    			
    			// if hostnameList not contain server IP, raise Exception
				if ( ! AgentConfig.getInstance().getHOSTNAME_LIST().contains(remoteIP)) {
					throw new AgentException("Server IP "+remoteIP+" not allowed to submit job to this agent. Closing connection");
				}

    		}
			
			// Initializing ExecTask
			ExecTask execTask = (ExecTask)XMLUtil.XMLStringToObject(xmlInputData);

			Agent.getInstance().addTaskInQueue(execTask);
		    
			DateFormat dateFormat = new SimpleDateFormat(DateUtil.DEFAULT_DATETIME_FORMAT);
			String timestamp = dateFormat.format(new Date());
			execTask.setStatusMessage("Added to queue at "+timestamp);
			execTask.setStatus(ExecTask.IN_QUEUE);
			execTask.setServerHost(remoteIP);
			
			response.setInstructionResultOK(true);
			response.setXmlOutputData(XMLUtil.objectToXMLString(execTask));
		
		} catch (Exception e) {
			logger.warn(e.getMessage());
			response.setException(e);
		}
		
		return response;
	}
	
}
