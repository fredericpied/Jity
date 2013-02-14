package org.jity.UIClient.swt;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.jity.UIClient.UIClientConfig;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.RequestSender;
import org.jity.common.referential.ExecTask;
import org.jity.common.referential.Job;
import org.jity.common.util.XMLUtil;

public class JobsStatusWindows {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Table tableExecTask = null;

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.verticalSpan = 60;
		gridData.horizontalAlignment = GridData.FILL;
		sShell = new Shell();
		sShell.setText("Jobs status");
		sShell.setSize(new Point(748, 368));
		sShell.setLayout(new GridLayout());
		tableExecTask = new Table(sShell, SWT.NONE);
		tableExecTask.setHeaderVisible(true);
		tableExecTask.setLayoutData(gridData);
		tableExecTask.setLinesVisible(true);
		TableColumn tableColumn = new TableColumn(tableExecTask, SWT.NONE);
		tableColumn.setWidth(90);
		tableColumn.setText("Job");
		TableColumn tableColumn1 = new TableColumn(tableExecTask, SWT.NONE);
		tableColumn1.setWidth(70);
		tableColumn1.setText("Agent");
		TableColumn tableColumn2 = new TableColumn(tableExecTask, SWT.NONE);
		tableColumn2.setWidth(30);
		tableColumn2.setText("Status");
		TableColumn tableColumn3 = new TableColumn(tableExecTask, SWT.NONE);
		tableColumn3.setWidth(60);
		tableColumn3.setText("ExecDate");
		TableColumn tableColumn4 = new TableColumn(tableExecTask, SWT.NONE);
		tableColumn4.setWidth(60);
		tableColumn4.setText("Begin");
		TableColumn tableColumn5 = new TableColumn(tableExecTask, SWT.NONE);
		tableColumn5.setWidth(60);
		tableColumn5.setText("End");
		TableColumn tableColumn6 = new TableColumn(tableExecTask, SWT.NONE);
		tableColumn6.setWidth(260);
		tableColumn6.setText("Message");
	}

	private void updateExecTaskTable() throws IOException {
		JityRequest request = new JityRequest();
		request.setInstructionName("LISTEXECTASK");
		
		// Load config file
		UIClientConfig clientConfig = UIClientConfig.getInstance();
		clientConfig.initialize();
		
		RequestSender requestSender = new RequestSender();
		requestSender.openConnection(clientConfig.getSERVER_HOSTNAME(),
				clientConfig.getSERVER_PORT());
		JityResponse response = requestSender.sendRequest(request);
		requestSender.closeConnection();
		
		if (!response.isInstructionResultOK()) {
			SWTUtility.showErrorMessage(sShell, response.getExceptionName()+
					": "+response.getExceptionMessage());
		} else {
			List listExecTask = (List)XMLUtil.XMLStringToObject(response.getXmlOutputData());
			this.tableExecTask.removeAll();

			Iterator iterListExecTask = listExecTask.iterator();
			while (iterListExecTask.hasNext()) {
				ExecTask e = (ExecTask)iterListExecTask.next();

				TableItem ligneExecTask = new TableItem(this.tableExecTask, SWT.NONE);

				ligneExecTask.setText(new String[] {String.valueOf(e.getJob().getName()),
						e.getJob().getHostName(), String.valueOf(e.getStatus()),
						String.valueOf(e.getExecDate()), String.valueOf(e.getBegin()),
						String.valueOf(e.getEnd()), String.valueOf(e.getStatusMessage())});
			}
		}
	}
	
	public JobsStatusWindows() {
		createSShell();

		try {
			updateExecTaskTable();
		} catch (IOException e) {
			SWTUtility.showErrorMessage(sShell, e.getClass().getSimpleName()+
					": "+e.getMessage());;
		}
		
		sShell.open();
		
	}
	
}
