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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jity.agent.Agent;
import org.jity.agent.AgentQueueManagerDaemon;
import org.jity.common.protocol.Instruction;
import org.jity.common.protocol.JityResponse;
import org.jity.common.referential.ExecTask;
import org.jity.common.util.XMLUtil;

/**
 * 
 * @author 09344A
 *
 */
public class GetTaskStatus implements Instruction {
	private static final Logger logger = Logger.getLogger(GetTaskStatus.class);

	public JityResponse launch(String xmlInputData) {
		JityResponse response = new JityResponse();
		
		try {
			
			 ArrayList<ExecTask> taskQueueExtract = new ArrayList<ExecTask>();

		     synchronized(Agent.getInstance().getTaskQueue()) {
			 
				 // Select terminate tasks in queue
				Iterator<ExecTask> iterTask = Agent.getInstance().getTaskQueue().iterator();
		    	while (iterTask.hasNext()) {
		    		ExecTask task = iterTask.next();
		    		
		    		if (task.getStatus() != ExecTask.IN_QUEUE) {
		    			taskQueueExtract.add(task);
//		    			long id = task.getId();
//		    			int status = task.getStatus();
//		    			String statusMess = task.getStatusMessage();
//		    			String logFile = task.getLogFile();
//		    			response.setXmlOutputData(XMLUtil.objectToXMLString(id));
//		    			response.addXmlOutputData(XMLUtil.objectToXMLString(status));
//		    			response.addXmlOutputData(XMLUtil.objectToXMLString(statusMess));
//		    			response.addXmlOutputData(XMLUtil.objectToXMLString(logFile));
		    		}
		    	}
	    	}

			response.setXmlOutputData(XMLUtil.objectToXMLString(taskQueueExtract));
	    	response.setInstructionResultOK(true);
		
		} catch (Exception e) {
			response.setException(e);
		}
		
		return response;
	}
	
}
