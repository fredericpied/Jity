/**
if *  JiTy : Open Job Scheduler
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
package org.jity.server.instructions.referential;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jity.common.protocol.Instruction;
import org.jity.common.protocol.JityResponse;
import org.jity.common.referential.ExecTask;
import org.jity.common.referential.dateConstraint.PersonnalCalendar;
import org.jity.common.util.XMLUtil;
import org.jity.server.Server;
import org.jity.server.ServerException;
import org.jity.server.ServerTaskManager;
import org.jity.server.database.DatabaseServer;

/**
 * Server command to update task status
 * @author 09344A
 *
 */
public class UpdateTaskStatus implements Instruction {
	private static final Logger logger = Logger.getLogger(UpdateTaskStatus.class);
	
	
	public JityResponse launch(String xmlInputData) {
		JityResponse response = new JityResponse();
		Session databaseSession = null;
		
		try {

			databaseSession = DatabaseServer.getInstance().getSession();
			
			ExecTask task = (ExecTask)XMLUtil.XMLStringToObject(xmlInputData);
					
			// Saving ExecTask state
			Transaction transaction = databaseSession.beginTransaction();
			databaseSession.merge(task);
			transaction.commit();
					
			logger.debug("Exec task "+task.getId()+" updated in db");

			databaseSession.close();
			
			response.setInstructionResultOK(true);

		} catch (Exception e) {
			response.setException(e);
			if (databaseSession != null) databaseSession.close();
		}

		return response;
	}

}
