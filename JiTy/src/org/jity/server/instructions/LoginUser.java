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
package org.jity.server.instructions;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.jity.common.protocol.Instruction;
import org.jity.common.protocol.JityResponse;
import org.jity.common.referential.User;
import org.jity.common.util.XMLUtil;
import org.jity.server.ServerException;
import org.jity.server.database.DataNotFoundDBException;
import org.jity.server.database.HibernateSessionFactory;


/**
 * Server command to list Users existing in DB
 * 
 * @author 09344A
 * 
 */
public class LoginUser implements Instruction {
	private static final Logger logger = Logger.getLogger(LoginUser.class);
	
	public JityResponse launch(String xmlInputData) {
		JityResponse response = new JityResponse();

		try {
			
			User inUser = (User)XMLUtil.XMLStringToObject(xmlInputData);
			
			String queryFind = "select user from org.jity.common.referential.User user" +
					" where user.login='"+inUser.getLogin()+"'";
			
			Session session = HibernateSessionFactory.getInstance().getSession();

			List list = session.createQuery(queryFind).list();

			session.close();

			// If User does not existe whith this login
			if (list.size() == 0) throw new DataNotFoundDBException("DataNotFoundDBException :"+queryFind);
			
			// Compare cryped password
	        User readUser = (User)list.get(0);
	        
	        if (readUser.getPassword().compareTo(inUser.getPassword()) != 0) {
	        	throw new ServerException("Incorrect password for user "+inUser.getLogin());
	        } else {
	        	response.setXmlOutputData(XMLUtil.objectToXMLString(readUser));
	        	response.setInstructionResultOK(true);
	        }
			
		} catch (Exception e) {
			response.setException(e);
		}

		return response;
	}

}
