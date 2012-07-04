package org.jity.ui.commandLine;

import org.jity.common.XMLUtil;
import org.jity.referential.persistent.Calendar;
import org.jity.server.ServerSideClient;
import org.jity.server.ServerSideClientException;
import org.jity.server.instructions.InstructionException;
import org.jity.server.protocol.JityRequest;
import org.jity.server.protocol.JityResponse;

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
	
	private	int launch(String[] args) throws BadArgCLException, ServerSideClientException, InstructionException {
		int returnCode = 1;
		
		this.loadArguments(args);
		
		this.testRequiredArg("-n");
		this.testRequiredArg("-y");
		
		this.calendarName = readStringArguments("-n");
		this.calendarDescription = readStringArguments("-d");
		this.calendarYear = readIntArguments("-y");
		
		System.out.println(this.getBanner());
		
		Calendar calendar = new Calendar();
		calendar.setName(this.calendarName);
		calendar.setDescription(this.calendarDescription);
		calendar.setYear(this.calendarYear);
		calendar
				.setOpenDays("OOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCCOOOOOCC"
						+ "OOOOOCCOOOOOCC");
		
		String xmlCalendar = XMLUtil.objectToXMLString(calendar);
		
		JityRequest request = new JityRequest();
		request.setInstructionName("ADDCALENDAR");
		request.setXmlInputData(xmlCalendar);
		
		ServerSideClient client = new ServerSideClient();
		JityResponse response = client.sendRequest(request);
		
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
