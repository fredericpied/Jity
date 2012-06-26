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
package org.jity.server.instructions.referential;

import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jity.common.XMLUtil;
import org.jity.referential.persistent.Calendar;
import org.jity.server.Server;
import org.jity.server.ServerException;
import org.jity.server.database.Database;
import org.jity.server.instructions.Instruction;
import org.jity.server.protocol.JityResponse;

/**
 * Server command to create à new calendar
 * 
 * @author 09344A
 * 
 */
public class AddCalendar implements Instruction {

	public JityResponse launch(ArrayList<String> parameters) {
		JityResponse response = new JityResponse();

		try {

			Calendar calendar = (Calendar) XMLUtil.XMLStringToObject(parameters
					.get(0));

			Session session = Database.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();

			session.save(calendar);
			
			transaction.commit();
			session.close();

			response.setInstructionResult("OK");

		} catch (Exception e) {
			response.setException(e);
		}

		return response;
	}

}
