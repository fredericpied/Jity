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
import java.util.Date;

import org.apache.log4j.Logger;
import org.jity.agent.Agent;
import org.jity.agent.AgentConfig;
import org.jity.agent.AgentException;
import org.jity.agent.taskManager.TaskManager;
import org.jity.common.protocol.Instruction;
import org.jity.common.protocol.JityResponse;
import org.jity.common.referential.ExecTask;
import org.jity.common.referential.Job;
import org.jity.common.util.DateUtil;
import org.jity.common.util.XMLUtil;

/**
 * 
 * @author 09344A
 *
 */
public class AddTaskInQueue implements Instruction {
	private static final Logger logger = Logger.getLogger(AddTaskInQueue.class);

	public JityResponse launch(String xmlInputData) {
		JityResponse response = new JityResponse();
		
		try {
		
			if (TaskManager.getInstance().getCurrentNumTaskInQueue() >=
				TaskManager.getInstance().getMaxNumTaskInQueue()) {
				throw new AgentException("Max number of tasks in queue reached ("+
						TaskManager.getInstance().getMaxNumTaskInQueue()+")");
			}
			
			// Initializing ExecTask
			ExecTask execTask = (ExecTask)XMLUtil.XMLStringToObject(xmlInputData);

			DateFormat dateFormat = new SimpleDateFormat(DateUtil.DEFAULT_DATETIME_FORMAT);
			String timestamp = dateFormat.format(new Date());
			execTask.setStatusMessage("Added to queue at "+timestamp);
			
			TaskManager.getInstance().addTaskInQueue(execTask);
			
			response.setInstructionResultOK(true);
			response.setXmlOutputData(XMLUtil.objectToXMLString(execTask));
		
		} catch (Exception e) {
			response.setException(e);
		}
		
		return response;
	}
	
}
