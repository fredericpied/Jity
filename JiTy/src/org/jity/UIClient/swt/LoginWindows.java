package org.jity.UIClient.swt;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.jity.UIClient.UIClientConfig;
import org.jity.common.protocol.JityRequest;
import org.jity.common.protocol.JityResponse;
import org.jity.common.protocol.RequestSender;
import org.jity.common.referential.User;
import org.jity.common.util.StringCrypter;
import org.jity.common.util.XMLUtil;

public class LoginWindows {

	private Shell sShell = null; // @jve:decl-index=0:visual-constraint="10,10"
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
		textPassword.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR:

					try {
						loginUser();
					} catch (Exception e1) {
						SWTUtility.showErrorMessage(sShell, e1.getClass().getSimpleName() + ": " + e1.getMessage());
					}
					break;
				}
			}
		});

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

				try {
					loginUser();
				} catch (Exception e1) {
					SWTUtility.showErrorMessage(sShell, e1.getClass().getSimpleName() + ": " + e1.getMessage());
				}

			}
		});
	}

	public LoginWindows() {
		createSShell();
		SWTUtility.centerOnScreen(sShell.getDisplay(), sShell);
		sShell.open();

		// Load config file
		UIClientConfig clientConfig = UIClientConfig.getInstance();
		try {
			clientConfig.initialize();
		} catch (IOException e) {
			SWTUtility.showErrorMessage(sShell, e.getClass().getSimpleName() + ": " + e.getMessage());
		}

		this.textServerHostName.setText(clientConfig.getSERVER_HOSTNAME());
		this.textServerPort.setText(String.valueOf(clientConfig.getSERVER_PORT()));

	}

	private void loginUser() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {

		if (this.textUsername.getText().length() == 0 || this.textPassword.getText().length() == 0
				|| this.textServerHostName.getText().length() == 0 || this.textServerPort.getText().length() == 0) {

			SWTUtility.showErrorMessage(sShell, "All fields are required to login");
		} else {
			JityRequest request = new JityRequest();
			request.setInstructionName("LOGINUSER");

			User sampleUser = new User();
			sampleUser.setLogin(this.textUsername.getText());
			String encryptedPassword = StringCrypter.encrypt(this.textPassword.getText(), "JiTyCedricFred13");
			sampleUser.setPassword(encryptedPassword);
			request.setXmlInputData(XMLUtil.objectToXMLString(sampleUser));

			// Load config file
			UIClientConfig clientConfig = UIClientConfig.getInstance();
			clientConfig.initialize();

			RequestSender requestSender = new RequestSender();
			requestSender.openConnection(this.textServerHostName.getText(), Integer.parseInt(this.textServerPort.getText()));
			JityResponse response = requestSender.sendRequest(request);
			requestSender.closeConnection();

			if (!response.isInstructionResultOK()) {
				SWTUtility.showErrorMessage(sShell, response.getExceptionName() + ": " + response.getExceptionMessage());
			} else {

				User user = (User) XMLUtil.XMLStringToObject(response.getXmlOutputData());

				SWTUtility.showInformationMessage(sShell, "User " + user.getLogin() + " logged in");

				sShell.close();

			}
		}
	}
}
