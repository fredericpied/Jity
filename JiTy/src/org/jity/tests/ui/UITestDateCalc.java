package org.jity.tests.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jity.referential.DateConstraint;
import org.jity.referential.DateConstraintException;
import org.jity.referential.PersonnalCalendar;
import org.jity.referential.PersonnalCalendarException;
import org.jity.referential.dateCalc.DateException;

public class UITestDateCalc {
	private static final Logger logger = Logger.getLogger(UITestDateCalc.class);  //  @jve:decl-index=0:

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="29,12"
	private Label label = null;
	private Text text = null;
	private Button button = null;
	private Label label1 = null;
	private Label label2 = null;
	private Label label3 = null;
	private Text textYear = null;
	private Label label4 = null;
	private Label label5 = null;
	private Button checkBoxMonday = null;
	private Label label6 = null;
	private Button checkBoxTuesday = null;
	private Label label7 = null;
	private Button checkBoxWednesday = null;
	private Label label8 = null;
	private Button checkBoxThursday = null;
	private Label label9 = null;
	private Button checkBoxFriday = null;
	private Label label10 = null;
	private Button checkBoxSaturday = null;
	private Label label11 = null;
	private Button checkBoxSunday = null;
	private Table tableDays = null;
	private Button checkBoxHolidays = null;
	
	private Display display = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/* Before this is run, be sure to set up the launch configuration (Arguments->VM Arguments)
		 * for the correct SWT library path in order to run with the SWT dlls. 
		 * The dlls are located in the SWT plugin jar.  
		 * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
		 *       installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
		 */
		Display display = Display.getDefault();
		UITestDateCalc thisClass = new UITestDateCalc();
		thisClass.createSShell();
		centerOnDisplay(display, thisClass.sShell);
		thisClass.sShell.open();

		thisClass.display = display;
		
		while (!thisClass.sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.verticalAlignment = GridData.FILL;
		gridData6.grabExcessVerticalSpace = true;
		gridData6.horizontalSpan = 19;
		gridData6.grabExcessHorizontalSpace = true;
		GridData gridData5 = new GridData();
		gridData5.horizontalSpan = 4;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.BEGINNING;
		gridData4.verticalAlignment = GridData.CENTER;
		GridData gridData3 = new GridData();
		gridData3.horizontalSpan = 10;
		GridData gridData2 = new GridData();
		gridData2.widthHint = 50;
		gridData2.horizontalSpan = 3;
		GridData gridData1 = new GridData();
		gridData1.widthHint = 50;
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.horizontalSpan = 3;
		gridData1.grabExcessHorizontalSpace = false;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 14;
		gridData.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 19;
		sShell = new Shell();
		sShell.setText("DateContraint test");
		sShell.setLayout(gridLayout);
		sShell.setSize(new Point(690, 764));
		label1 = new Label(sShell, SWT.NONE);
		label1.setText("Calendar:");
		label3 = new Label(sShell, SWT.NONE);
		label3.setText("Year:");
		textYear = new Text(sShell, SWT.BORDER);
		textYear.setLayoutData(gridData2);
		textYear.setText(String.valueOf(getActualYear()));
		textYear.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				updateDisplay();
			}
		});
		label2 = new Label(sShell, SWT.NONE);
		label2.setText("French Holidays:");
		label2.setLayoutData(gridData5);
		checkBoxHolidays = new Button(sShell, SWT.CHECK);
		checkBoxHolidays.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				updateDisplay();			}
		});
		Label filler7 = new Label(sShell, SWT.NONE);
		Label filler14 = new Label(sShell, SWT.NONE);
		Label filler1 = new Label(sShell, SWT.NONE);
		Label filler11 = new Label(sShell, SWT.NONE);
		Label filler9 = new Label(sShell, SWT.NONE);
		Label filler71 = new Label(sShell, SWT.NONE);
		Label filler13 = new Label(sShell, SWT.NONE);
		Label filler131 = new Label(sShell, SWT.NONE);
		Label filler15 = new Label(sShell, SWT.NONE);
		Label filler18 = new Label(sShell, SWT.NONE);
		label4 = new Label(sShell, SWT.NONE);
		label4.setText("Closed week days:");
		label4.setLayoutData(gridData3);
		Label filler5 = new Label(sShell, SWT.NONE);
		Label filler6 = new Label(sShell, SWT.NONE);
		Label filler8 = new Label(sShell, SWT.NONE);
		Label filler4 = new Label(sShell, SWT.NONE);
		Label filler61 = new Label(sShell, SWT.NONE);
		Label filler12 = new Label(sShell, SWT.NONE);
		Label filler121 = new Label(sShell, SWT.NONE);
		Label filler3 = new Label(sShell, SWT.NONE);
		Label filler17 = new Label(sShell, SWT.NONE);
		label5 = new Label(sShell, SWT.NONE);
		label5.setText("Mon.");
		label5.setLayoutData(gridData4);
		checkBoxMonday = new Button(sShell, SWT.CHECK);
		checkBoxMonday.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				updateDisplay();
			}
		});
		label6 = new Label(sShell, SWT.NONE);
		label6.setText("Tue.");
		checkBoxTuesday = new Button(sShell, SWT.CHECK);
		checkBoxTuesday.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				updateDisplay();
			}
		});
		label7 = new Label(sShell, SWT.NONE);
		label7.setText("Wed.");
		checkBoxWednesday = new Button(sShell, SWT.CHECK);
		checkBoxWednesday.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				updateDisplay();
			}
		});
		label8 = new Label(sShell, SWT.NONE);
		label8.setText("Thu.");
		checkBoxThursday = new Button(sShell, SWT.CHECK);
		checkBoxThursday.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				updateDisplay();
			}
		});
		label9 = new Label(sShell, SWT.NONE);
		label9.setText("Fri.");
		checkBoxFriday = new Button(sShell, SWT.CHECK);
		checkBoxFriday.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				updateDisplay();
			}
		});
		label10 = new Label(sShell, SWT.NONE);
		label10.setText("Sat.");
		checkBoxSaturday = new Button(sShell, SWT.CHECK);
		checkBoxSaturday.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				updateDisplay();
			}
		});
		label11 = new Label(sShell, SWT.NONE);
		label11.setText("Sun.");
		checkBoxSunday = new Button(sShell, SWT.CHECK);
		Label filler51 = new Label(sShell, SWT.NONE);
		checkBoxSunday.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				updateDisplay();
			}
		});
		Label filler10 = new Label(sShell, SWT.NONE);
		Label filler111 = new Label(sShell, SWT.NONE);
		Label filler2 = new Label(sShell, SWT.NONE);
		label = new Label(sShell, SWT.NONE);
		label.setText("Planification rule:");
		text = new Text(sShell, SWT.BORDER);
		text.setLayoutData(gridData);
		button = new Button(sShell, SWT.NONE);
		button.setText("Refresh");
		button.setLayoutData(gridData1);
		Label filler81 = new Label(sShell, SWT.NONE);
		button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				updateDisplay();
			}
		});
		tableDays = new Table(sShell, SWT.NONE);
		tableDays.setHeaderVisible(true);
		tableDays.setLinesVisible(true);
		tableDays.setLayoutData(gridData6);
		TableColumn tableColumn = new TableColumn(tableDays, SWT.NONE);
		tableColumn.setWidth(55);
		tableColumn.setText("Jan.");
		TableColumn tableColumn1 = new TableColumn(tableDays, SWT.NONE);
		tableColumn1.setWidth(55);
		tableColumn1.setText("Feb.");
		TableColumn tableColumn2 = new TableColumn(tableDays, SWT.NONE);
		tableColumn2.setWidth(55);
		tableColumn2.setText("Mar.");
		TableColumn tableColumn3 = new TableColumn(tableDays, SWT.NONE);
		tableColumn3.setWidth(55);
		tableColumn3.setText("Apr.");
		TableColumn tableColumn4 = new TableColumn(tableDays, SWT.NONE);
		tableColumn4.setWidth(55);
		tableColumn4.setText("May");
		TableColumn tableColumn5 = new TableColumn(tableDays, SWT.NONE);
		tableColumn5.setWidth(55);
		tableColumn5.setText("Jun.");
		TableColumn tableColumn6 = new TableColumn(tableDays, SWT.NONE);
		tableColumn6.setWidth(55);
		tableColumn6.setText("Jul.");
		TableColumn tableColumn21 = new TableColumn(tableDays, SWT.NONE);
		tableColumn21.setWidth(55);
		tableColumn21.setText("Aug.");
		TableColumn tableColumn31 = new TableColumn(tableDays, SWT.NONE);
		tableColumn31.setWidth(55);
		tableColumn31.setText("Sep.");
		TableColumn tableColumn41 = new TableColumn(tableDays, SWT.NONE);
		tableColumn41.setWidth(55);
		tableColumn41.setText("Oct.");
		TableColumn tableColumn51 = new TableColumn(tableDays, SWT.NONE);
		tableColumn51.setWidth(55);
		tableColumn51.setText("Nov.");
		TableColumn tableColumn61 = new TableColumn(tableDays, SWT.NONE);
		tableColumn61.setWidth(55);
		tableColumn61.setText("Dec.");
	}

	public void showErrorMessage(String message) {
		MessageBox messageBox = new MessageBox(this.sShell, SWT.ICON_ERROR | SWT.OK);
		messageBox.setMessage(message);
		messageBox.setText("Error");
		messageBox.open();
	}

	
	public static void centerOnDisplay(Display display, Shell shell) {
		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}
	
	public static int getActualYear() {
		Calendar cal = new GregorianCalendar();
		return cal.get(Calendar.YEAR);
	}
	
	public void updateTabDays(DateConstraint dc) throws PersonnalCalendarException, DateConstraintException, DateException {
		
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(Calendar.YEAR, dc.getCalendar().getYear());
		
		tableDays.removeAll();
		
	    Color red = display.getSystemColor(SWT.COLOR_RED);
	    Color green = display.getSystemColor(SWT.COLOR_GREEN);
		
		// Initialize table line
		for (int i=1;i<=31;i++) {
			TableItem line = new TableItem(tableDays, SWT.NONE);
			line.setText(0, "");
		}
		
		//String dayType;
		DateFormat dateFormat = new SimpleDateFormat("dd E");
		
		for (int month=0;month<=11;month++) {
			cal.set(Calendar.MONTH, month);
			for (int dayOfMonth=1;dayOfMonth<=31;dayOfMonth++) {
				cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

				// If same month
				if (month != cal.get(Calendar.MONTH)) {
					TableItem line = tableDays.getItem(dayOfMonth-1);
					line.setText(month, " ");
				} else if (dc.isAValidDate(cal.getTime())) {
					TableItem line = tableDays.getItem(dayOfMonth-1);
					line.setText(month, dateFormat.format(cal.getTime()));
					line.setBackground(month, green);
				} else {
					TableItem line = tableDays.getItem(dayOfMonth-1);
					line.setText(month, dateFormat.format(cal.getTime()));
					line.setBackground(month, red);
				}
			}
		}

	}
	
	public void updateDisplay() {
		
		if (textYear.getText().length() == 0) {
			showErrorMessage("Year must be set");
		} else {
			try {
		
				int year = Integer.parseInt(textYear.getText());
				
				PersonnalCalendar persCalendar = new PersonnalCalendar();
				persCalendar.setYear(year);
				persCalendar.initializeWithAllDaysOpen();
				
				if (checkBoxHolidays.getSelection()) 
					persCalendar.addFrenchHolydays();
				
				if (checkBoxMonday.getSelection()) 
					persCalendar.addClosedDayOfWeek(1);
	
				if (checkBoxTuesday.getSelection()) 
					persCalendar.addClosedDayOfWeek(2);
	
				if (checkBoxWednesday.getSelection()) 
					persCalendar.addClosedDayOfWeek(3);
	
				if (checkBoxThursday.getSelection()) 
					persCalendar.addClosedDayOfWeek(4);
	
				if (checkBoxFriday.getSelection()) 
					persCalendar.addClosedDayOfWeek(5);
	
				if (checkBoxSaturday.getSelection()) 
					persCalendar.addClosedDayOfWeek(6);
	
				if (checkBoxSunday.getSelection()) 
					persCalendar.addClosedDayOfWeek(7);
				
				DateConstraint dc = new DateConstraint();
				dc.setCalendar(persCalendar);
				if (text.getText().length() > 0) 
					dc.setPlanifRule(text.getText());
				
				updateTabDays(dc);
				
			} catch (Exception e) {
				e.printStackTrace();
				showErrorMessage(e.getClass().getSimpleName()+": "+e.getMessage());
			}
		}
		
	}
	
}
