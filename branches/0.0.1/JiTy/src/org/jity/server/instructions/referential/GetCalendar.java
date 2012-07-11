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

import java.util.List;

import org.hibernate.Session;
import org.jity.common.XMLUtil;
import org.jity.protocol.JityResponse;
import org.jity.server.database.DataNotFoundDBException;
import org.jity.server.database.Database;
import org.jity.server.database.TooMuchDataDBException;
import org.jity.server.instructions.Instruction;

/**
 * Server command to create à new calendar
 * 
 * @author 09344A
 * 
 */
public class GetCalendar implements Instruction {

	public JityResponse launch(String xmlInputData) {
		JityResponse response = new JityResponse();

		try {

			Long id = (Long)XMLUtil.XMLStringToObject(xmlInputData);
			String queryFind = "select cal from org.jity.referential.persistent.Calendar cal"
	                + " where cal.id = '" + id + "'";

			Session session = Database.getSessionFactory().openSession();

			List list = session.createQuery(queryFind).list();
	        if (list.size() == 0) throw new DataNotFoundDBException(queryFind);
			if (list.size() > 1) throw new TooMuchDataDBException(queryFind);
			
			response.setXmlOutputData(XMLUtil.objectToXMLString(list));
			response.setInstructionResultOK(true);

			session.close();

		} catch (Exception e) {
			response.setException(e);
		}

		return response;
	}

}
