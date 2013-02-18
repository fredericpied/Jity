package org.jity.UIClient.swt;

import java.io.IOException;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

public class LoginWindows {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Label labelUser = null;
	private Label labelPasswor = null;
	private Text textUsername = null;
	private Text textPassword = null;
	private Label labelServerHost = null;
	private Text textServerHostName = null;
	private Label labelServerPort = null;
	private Text textServerPort = null;
	private Button buttonOK = null;
	private Button buttonCancel = null;

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		GridData gridData8 = new GridData();
		gridData8.widthHint = 60;
		gridData8.verticalAlignment = GridData.CENTER;
		gridData8.horizontalAlignment = GridData.END;
		GridData gridData7 = new GridData();
		gridData7.widthHint = 60;
		gridData7.verticalAlignment = GridData.CENTER;
		gridData7.horizontalAlignment = GridData.END;
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.horizontalSpan = 2;
		gridData6.verticalAlignment = GridData.CENTER;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.END;
		gridData5.verticalAlignment = GridData.CENTER;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.END;
		gridData4.verticalAlignment = GridData.CENTER;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.END;
		gridData3.verticalAlignment = GridData.CENTER;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.END;
		gridData2.verticalAlignment = GridData.CENTER;
		GridData gridData11 = new GridData();
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.verticalAlignment = GridData.CENTER;
		gridData11.horizontalSpan = 2;
		gridData11.horizontalAlignment = GridData.FILL;
		GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.horizontalSpan = 2;
		gridData1.horizontalAlignment = GridData.FILL;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		sShell = new Shell(SWT.APPLICATION_MODAL | SWT.TITLE);
		sShell.setText("JiTy - Login");
		sShell.setLayout(gridLayout);
		sShell.setSize(new Point(300, 175));
		labelUser = new Label(sShell, SWT.NONE);
		labelUser.setText("Username:");
		labelUser.setLayoutData(gridData2);
		textUsername = new Text(sShell, SWT.BORDER);
		textUsername.setLayoutData(gridData);
		labelPasswor = new Label(sShell, SWT.NONE);
		labelPasswor.setText("Password:");
		labelPasswor.setLayoutData(gridData3);
		textPassword = new Text(sShell, SWT.BORDER | SWT.PASSWORD);
		textPassword.setLayoutData(gridData1);
		labelServerHost = new Label(sShell, SWT.NONE);
		labelServerHost.setText("Server hostname:");
		labelServerHost.setLayoutData(gridData4);
		textServerHostName = new Text(sShell, SWT.BORDER);
		textServerHostName.setLayoutData(gridData11);
		labelServerPort = new Label(sShell, SWT.NONE);
		labelServerPort.setText("Server port:");
		labelServerPort.setLayoutData(gridData5);
		textServerPort = new Text(sShell, SWT.BORDER);
		textServerPort.setLayoutData(gridData6);
		Label filler = new Label(sShell, SWT.NONE);
		buttonCancel = new Button(sShell, SWT.NONE);
		buttonCancel.setText("Cancel");
		buttonCancel.setLayoutData(gridData7);
		buttonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				sShell.getDisplay().close();
			}
		});
		buttonOK = new Button(sShell, SWT.NONE);
		buttonOK.setText("OK");
		buttonOK.setLayoutData(gridData8);
		buttonOK.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				sShell.close();
			}
		});
	}
		
	public LoginWindows() {
		createSShell();
		SWTUtility.centerOnScreen(sShell.getDisplay(), sShell);	
		sShell.open();
	}
	

}
