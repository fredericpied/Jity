package org.jity.UIClient.swt;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Text;
import org.jity.UIClient.UIClientConfig;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.RequestSender;
import org.jity.common.referential.Job;
import org.jity.common.util.XMLUtil;
import org.jity.server.Main;
import org.jity.server.instructions.InstructionException;
import org.jity.server.instructions.referential.ListJobs;

public class JobsCRUD {
	private static final Logger logger = Logger.getLogger(JobsCRUD.class);
	
	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Group groupJobDetails = null;
	private Button buttonCreate = null;
	private Table tableJobs = null;
	private Button buttonCreate1 = null;
	private Button buttonUpdate = null;
	private Button buttonDelete = null;
	private Group groupJobDetails1 = null;
	private Label labelName = null;
	private Text textName = null;
	private Label labelDescription = null;
	private Text textDescription = null;
	private Label labelHostName = null;
	private Text textHostName = null;
	private Label labelHostPort = null;
	private Label label = null;
	private Text textHostPort = null;
	private Label labelHostUserName = null;
	private Text textHostUserName = null;
	private Label labelCommandPath = null;
	private Text textCommandPath = null;
	private Label labelEnable = null;
	private Button checkBoxEnabled = null;
	private Label labelStartTime = null;
	private Text textStartTime = null;
	private Button buttonOK = null;
	private Button buttonCancel = null;
	private Label label1 = null;
	private Text textNbJobs = null;
	
	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
	
		GridData gridData12 = new GridData();
		gridData12.horizontalAlignment = GridData.END;
		gridData12.widthHint = 150;
		gridData12.verticalAlignment = GridData.CENTER;
		GridData gridData31 = new GridData();
		gridData31.horizontalAlignment = GridData.BEGINNING;
		gridData31.verticalAlignment = GridData.CENTER;
		GridData gridData21 = new GridData();
		gridData21.horizontalAlignment = GridData.BEGINNING;
		gridData21.verticalAlignment = GridData.CENTER;
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.BEGINNING;
		gridData11.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.heightHint = 200;
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		sShell = new Shell();
		sShell.setText("Jobs");
		sShell.setLayout(gridLayout);
		sShell.setSize(new Point(649, 575));
		tableJobs = new Table(sShell, SWT.BORDER  | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableJobs.setHeaderVisible(true);
		tableJobs.setLinesVisible(true);
		tableJobs.setLayoutData(gridData);
		tableJobs.setBounds(new Rectangle(1, 1, 81, 41));

		tableJobs.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent e) {
				// Idem Update
				if (tableJobs.getSelectionCount() == 0) {
					SWTUtility.showErrorMessage(sShell, "Select item to update before");
				} else if (tableJobs.getSelectionCount() == 1) {
					try {
						updateFormsWhisSelection();
					} catch (IOException e1) {
						SWTUtility.showErrorMessage(sShell, e1.getClass().getSimpleName()+": "+e1.getMessage());
					}
				}
			}
		});
		Label filler14 = new Label(sShell, SWT.NONE);
		buttonCreate1 = new Button(sShell, SWT.NONE);
		buttonCreate1.setText("Create");
		buttonCreate1.setLayoutData(gridData31);
		buttonCreate1.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				setFormsEnabled(true);
			}
		});
		buttonUpdate = new Button(sShell, SWT.NONE);
		buttonUpdate.setText("Update");
		buttonUpdate.setLayoutData(gridData21);
		buttonUpdate.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (tableJobs.getSelectionCount() == 0) {
					SWTUtility.showErrorMessage(sShell, "Select item to update before");
				} else if (tableJobs.getSelectionCount() == 1) {
					try {
						updateFormsWhisSelection();
					} catch (IOException e1) {
						SWTUtility.showErrorMessage(sShell, e1.getClass().getSimpleName()+": "+e1.getMessage());
					}
				}
			}
		});
		buttonDelete = new Button(sShell, SWT.NONE);
		buttonDelete.setText("Delete");
		buttonDelete.setLayoutData(gridData11);
		textNbJobs = new Text(sShell, SWT.BACKGROUND | SWT.RIGHT);
		textNbJobs.setLayoutData(gridData12);
		buttonDelete.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (tableJobs.getSelectionCount() == 0) {
					SWTUtility.showErrorMessage(sShell, "Select item to delete before");
				}
			}
		});
		label1 = new Label(sShell, SWT.NONE);
		label1.setText("");
		Label filler25 = new Label(sShell, SWT.NONE);
		Label filler10 = new Label(sShell, SWT.NONE);
		Label filler11 = new Label(sShell, SWT.NONE);
		Label filler12 = new Label(sShell, SWT.NONE);
		Label filler13 = new Label(sShell, SWT.NONE);
		createGroupJobDetails1();
		TableColumn tableColumn21 = new TableColumn(tableJobs, SWT.NONE);
		tableColumn21.setWidth(0);
		tableColumn21.setText("ID");
		TableColumn tableColumn = new TableColumn(tableJobs, SWT.NONE);
		tableColumn.setWidth(100);
		tableColumn.setText("Name");
		TableColumn tableColumn1 = new TableColumn(tableJobs, SWT.NONE);
		tableColumn1.setWidth(360);
		tableColumn1.setText("Description");
		TableColumn tableColumn11 = new TableColumn(tableJobs, SWT.NONE);
		tableColumn11.setWidth(95);
		tableColumn11.setText("Host");
		TableColumn tableColumn2 = new TableColumn(tableJobs, SWT.NONE);
		tableColumn2.setWidth(55);
		tableColumn2.setText("Enable ?");
	}
	/**
	 * This method initializes groupJobDetails1	
	 *
	 */
	private void createGroupJobDetails1() {
		GridData gridData10 = new GridData();
		gridData10.horizontalAlignment = GridData.END;
		gridData10.verticalAlignment = GridData.CENTER;
		GridData gridData9 = new GridData();
		gridData9.horizontalAlignment = GridData.END;
		gridData9.verticalAlignment = GridData.CENTER;
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = GridData.BEGINNING;
		gridData8.verticalAlignment = GridData.CENTER;
		GridData gridData7 = new GridData();
		gridData7.horizontalAlignment = GridData.BEGINNING;
		gridData7.horizontalSpan = 2;
		gridData7.widthHint = 400;
		gridData7.verticalAlignment = GridData.CENTER;
		GridData gridData6 = new GridData();
		gridData6.widthHint = 160;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.BEGINNING;
		gridData5.widthHint = 60;
		gridData5.verticalAlignment = GridData.CENTER;
		GridData gridData4 = new GridData();
		gridData4.widthHint = 160;
		GridData gridData3 = new GridData();
		gridData3.widthHint = 160;
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.CENTER;
		gridData2.horizontalSpan = 2;
		gridData2.widthHint = 400;
		gridData2.horizontalAlignment = GridData.BEGINNING;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 4;
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 4;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.verticalAlignment = GridData.FILL;
		groupJobDetails1 = new Group(sShell, SWT.NONE);
		groupJobDetails1.setText("Jobs Details");
		groupJobDetails1.setLayout(gridLayout1);
		groupJobDetails1.setLayoutData(gridData1);
		labelName = new Label(groupJobDetails1, SWT.NONE);
		labelName.setText("Name:");
		labelName.setLayoutData(gridData8);
		textName = new Text(groupJobDetails1, SWT.BORDER);
		textName.setLayoutData(gridData3);
		Label filler1 = new Label(groupJobDetails1, SWT.NONE);
		Label filler23 = new Label(groupJobDetails1, SWT.NONE);
		labelDescription = new Label(groupJobDetails1, SWT.NONE);
		labelDescription.setText("Description:");
		textDescription = new Text(groupJobDetails1, SWT.BORDER);
		textDescription.setLayoutData(gridData2);
		Label filler9 = new Label(groupJobDetails1, SWT.NONE);
		labelHostName = new Label(groupJobDetails1, SWT.NONE);
		labelHostName.setText("Host Name:");
		textHostName = new Text(groupJobDetails1, SWT.BORDER);
		textHostName.setLayoutData(gridData4);
		Label filler = new Label(groupJobDetails1, SWT.NONE);
		Label filler21 = new Label(groupJobDetails1, SWT.NONE);
		labelHostPort = new Label(groupJobDetails1, SWT.NONE);
		labelHostPort.setText("Host Port:");
		textHostPort = new Text(groupJobDetails1, SWT.BORDER);
		textHostPort.setLayoutData(gridData5);
		Label filler2 = new Label(groupJobDetails1, SWT.NONE);
		Label filler20 = new Label(groupJobDetails1, SWT.NONE);
		labelHostUserName = new Label(groupJobDetails1, SWT.NONE);
		labelHostUserName.setText("User Name:");
		textHostUserName = new Text(groupJobDetails1, SWT.BORDER);
		textHostUserName.setLayoutData(gridData6);
		Label filler3 = new Label(groupJobDetails1, SWT.NONE);
		Label filler19 = new Label(groupJobDetails1, SWT.NONE);
		labelCommandPath = new Label(groupJobDetails1, SWT.NONE);
		labelCommandPath.setText("Command Path:");
		textCommandPath = new Text(groupJobDetails1, SWT.BORDER);
		textCommandPath.setLayoutData(gridData7);
		Label filler8 = new Label(groupJobDetails1, SWT.NONE);
		labelEnable = new Label(groupJobDetails1, SWT.NONE);
		labelEnable.setText("Is enable:");
		checkBoxEnabled = new Button(groupJobDetails1, SWT.CHECK);
		Label filler4 = new Label(groupJobDetails1, SWT.NONE);
		Label filler17 = new Label(groupJobDetails1, SWT.NONE);
		labelStartTime = new Label(groupJobDetails1, SWT.NONE);
		labelStartTime.setText("Start time:");
		textStartTime = new Text(groupJobDetails1, SWT.BORDER);
		Label filler5 = new Label(groupJobDetails1, SWT.NONE);
		Label filler16 = new Label(groupJobDetails1, SWT.NONE);
		Label filler7 = new Label(groupJobDetails1, SWT.NONE);
		Label filler6 = new Label(groupJobDetails1, SWT.NONE);
		buttonCancel = new Button(groupJobDetails1, SWT.NONE);
		buttonCancel.setText("Cancel");
		buttonCancel.setLayoutData(gridData9);
		buttonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				setFormsErased();
				setFormsEnabled(false);
			}
		});
		buttonOK = new Button(groupJobDetails1, SWT.NONE);
		buttonOK.setText("OK");
		buttonOK.setLayoutData(gridData10);
		buttonOK.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				System.out.println("widgetSelected()"); // TODO Auto-generated Event stub widgetSelected()
			}
		});
	}

	private void setFormsEnabled(boolean value) {
		textName.setEnabled(value);
		textDescription.setEnabled(value);
		textHostName.setEnabled(value);
		textHostPort.setEnabled(value);
		textHostUserName.setEnabled(value);
		textCommandPath.setEnabled(value);
		checkBoxEnabled.setEnabled(value);
		textStartTime.setEnabled(value);
		buttonCancel.setEnabled(value);
		buttonOK.setEnabled(value);
	}

	private void setFormsErased() {
		textName.setText("");
		textDescription.setText("");
		textHostName.setText("");
		textHostPort.setText("");
		textHostUserName.setText("");
		textCommandPath.setText("");
		checkBoxEnabled.setSelection(false);
		textStartTime.setText("");
	}
	
	
	private void updateJobsTable() throws IOException {
		JityRequest request = new JityRequest();
		request.setInstructionName("LISTJOBS");
		
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
			List listJob = (List)XMLUtil.XMLStringToObject(response.getXmlOutputData());
			this.tableJobs.removeAll();
			this.textNbJobs.setText("");
			Iterator iterListJob = listJob.iterator();
			while (iterListJob.hasNext()) {
				Job j = (Job)iterListJob.next();

				TableItem ligneJob = new TableItem(this.tableJobs, SWT.NONE);

				ligneJob.setText(new String[] {String.valueOf(j.getId()),
						j.getName(),j.getDescription(),
						j.getHostName(), String.valueOf(j.getIsEnable())});
			}
			this.textNbJobs.setText(listJob.size()+" jobs loaded");
		}
	}
	
	private void updateFormsWhisSelection() throws IOException {
		
		long selectedJobId = Long.parseLong(this.tableJobs.getSelection()[0].getText(0));
		
		JityRequest request = new JityRequest();
		request.setInstructionName("GETJOB");
		request.setXmlInputData(XMLUtil.objectToXMLString(selectedJobId));
		
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
			
			List listJobSelected = (List)XMLUtil.XMLStringToObject(response.getXmlOutputData());
			Job jobSelected = (Job)listJobSelected.get(0);
			this.setFormsEnabled(true);

			textName.setText(SWTUtility.nvl(jobSelected.getName()));
			textDescription.setText(SWTUtility.nvl(jobSelected.getDescription()));
			textHostName.setText(SWTUtility.nvl(jobSelected.getHostName()));
			textHostPort.setText(String.valueOf(jobSelected.getHostPort()));
			textHostUserName.setText(SWTUtility.nvl(jobSelected.getHostUserName()));
			textCommandPath.setText(SWTUtility.nvl(jobSelected.getCommandPath()));
			checkBoxEnabled.setSelection(jobSelected.getIsEnable());
			textStartTime.setText(SWTUtility.nvl(jobSelected.getStartTime()));
			
		}
	}
	
	public JobsCRUD() {
		createSShell();

		setFormsEnabled(false);
			try {
				updateJobsTable();
			} catch (IOException e) {
				SWTUtility.showErrorMessage(sShell, e.getClass().getSimpleName()+
						": "+e.getMessage());;
			}
		
		sShell.open();
		
	}
	
}
