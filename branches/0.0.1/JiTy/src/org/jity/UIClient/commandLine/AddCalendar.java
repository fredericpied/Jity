package org.jity.UIClient.commandLine;

import java.io.IOException;

import org.jity.UIClient.UIClientConfig;
import org.jity.UIClient.UIClientException;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.RequestSender;
import org.jity.common.referential.PersonnalCalendar;
import org.jity.common.referential.PersonnalCalendarException;
import org.jity.common.util.XMLUtil;
import org.jity.server.instructions.InstructionException;

/**
 * Command line for adding a new Calendar
 * @author fred
 *
 */
public class AddCalendar extends CommandLine {
	
	private String calendarName;
	private String calendarDescription;
	private int calendarYear;

	private AddCalendar() {
		this.setHelpMessage("This command create a new Calendar:" +
				"\n-n\tCalendar name" +
				"\n-d\tCalendar description" +
				"\n-y\tCalendar year");
		this.setUsage("addcalendar -n[ame] <Calendar name> -d[escription] <Calendar Description> -y[ear] <Calendar year>");
		this.setMinArgsNumber(6);
		this.setMaxArgsNumber(6);
	}
	
	private	int launch(String[] args) throws BadArgCLException, UIClientException, InstructionException, IOException, PersonnalCalendarException {
		int returnCode = 1;
		
		this.loadArguments(args);
		
		this.testRequiredArg("-n");
		this.testRequiredArg("-y");
		
		this.calendarName = readStringArguments("-n");
		this.calendarDescription = readStringArguments("-d");
		this.calendarYear = readIntArguments("-y");
		
		System.out.println(this.getBanner());
		
		PersonnalCalendar calendar = new PersonnalCalendar();
		calendar.setName(this.calendarName);
		calendar.setDescription(this.calendarDescription);
		calendar.setYear(this.calendarYear);
		calendar.initializeWithAllDaysOpen();
		
		String xmlCalendar = XMLUtil.objectToXMLString(calendar);
		
		JityRequest request = new JityRequest();
		request.setInstructionName("ADDCALENDAR");
		request.setXmlInputData(xmlCalendar);
		
		// Load config file
		UIClientConfig clientConfig = UIClientConfig.getInstance();
		clientConfig.initialize();
		
		RequestSender requestSender = new RequestSender();
		requestSender.openConnection(clientConfig.getSERVER_HOSTNAME(), clientConfig.getSERVER_PORT());
		JityResponse response = requestSender.sendRequest(request);
		requestSender.closeConnection();
		
		if (response.isInstructionResultOK()) {
			returnCode = 0;
		} else {
			throw new InstructionException(response.getExceptionName()+": "+response.getExceptionMessage());
		}
		
		return returnCode;
	}
	
	public static void main(String[] args) {

		AddCalendar addCalendar = new AddCalendar();
		
		try {

			int returnCode = addCalendar.launch(args);
			System.exit(returnCode);
			
		} catch (BadArgCLException e){
			addCalendar.showHelpMessage(e.getClass().getSimpleName()+": "+e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
}
