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
import org.jity.common.protocol.Instruction;
import org.jity.common.protocol.JityResponse;
import org.jity.common.util.XMLUtil;
import org.jity.server.database.DataNotFoundDBException;
import org.jity.server.database.HibernateSessionFactory;
import org.jity.server.database.TooMuchDataDBException;

/**
 * Server command to create � new calendar
 * 
 * @author 09344A
 * 
 */
public class GetJob implements Instruction {

	public JityResponse launch(String xmlInputData) {
		JityResponse response = new JityResponse();

		try {

			Long id = (Long)XMLUtil.XMLStringToObject(xmlInputData);
			String queryFind = "select job from org.jity.common.referential.Job job"
	                + " where job.id = " + id;

			Session session = HibernateSessionFactory.getInstance().getSession();

			List list = session.createQuery(queryFind).list();
	        if (list.size() == 0) throw new DataNotFoundDBException("DataNotFoundDBException :"+queryFind);
			if (list.size() > 1) throw new TooMuchDataDBException("TooMuchDataDBException :"+queryFind);
			
			response.setXmlOutputData(XMLUtil.objectToXMLString(list));
			response.setInstructionResultOK(true);

			session.close();

		} catch (Exception e) {
			response.setException(e);
		}

		return response;
	}

}
