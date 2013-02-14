package org.jity.UIClient.swt;

import java.io.IOException;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Menu;

public class MainWindows {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private Menu menuBar = null;
	private MenuItem menuItemFile = null;
	private MenuItem menuItemEdit = null;
	private MenuItem menuItemHelp = null;
	private MenuItem menuItemReferential = null;
	private MenuItem menuItemJobsStatus = null;
	private Menu menuFile = null;
	private Menu menuEdit = null;
	private Menu menuHelp = null;
	private Menu menuJobsStatus = null;
	private Menu menuReferential = null;
	private MenuItem menuItemFileExit = null;
	private MenuItem menuItemReferentialJobs = null;
	private MenuItem menuItemJobsStatusWindows = null;
	
	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		sShell = new Shell();
		sShell.setText("JiTy - Main Windows");
		sShell.setSize(new Point(800, 600));
		sShell.setLayout(new GridLayout());
		
		menuBar = new Menu(sShell, SWT.BAR);
		sShell.setMenuBar(menuBar);
		
		menuItemFile = new MenuItem(menuBar, SWT.CASCADE);
		menuItemFile.setText("File");
		menuFile = new Menu(sShell, SWT.DROP_DOWN);
		menuItemFile.setMenu(menuFile);
		menuItemFileExit = new MenuItem(menuFile,SWT.PUSH);
		menuItemFileExit.setText("Exit");
		menuItemFileExit.addListener(SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				sShell.dispose();
			}
		});
		
		
		menuItemEdit = new MenuItem(menuBar, SWT.CASCADE);
		menuItemEdit.setText("Edit");
		menuEdit = new Menu(sShell, SWT.DROP_DOWN);
		menuItemEdit.setMenu(menuEdit);
		
		menuItemReferential = new MenuItem(menuBar, SWT.CASCADE);
		menuItemReferential.setText("Referential");
		menuReferential = new Menu(sShell, SWT.DROP_DOWN);
		menuItemReferential.setMenu(menuReferential);
		
		menuItemReferentialJobs = new MenuItem(menuReferential, SWT.PUSH);
		menuItemReferentialJobs.setText("Jobs");
		menuItemReferentialJobs.addListener(SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				new JobsCRUD();
			}
		});
		
		menuItemJobsStatus = new MenuItem(menuBar, SWT.CASCADE);
		menuItemJobsStatus.setText("Jobs status");
		menuJobsStatus = new Menu(sShell, SWT.DROP_DOWN);
		menuItemJobsStatus.setMenu(menuJobsStatus);
		menuItemJobsStatusWindows = new MenuItem(menuJobsStatus, SWT.PUSH);
		menuItemJobsStatusWindows.setText("Jobs status windows");
		menuItemJobsStatusWindows.addListener(SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				new JobsStatusWindows();
			}
		});
				
		menuItemHelp = new MenuItem(menuBar, SWT.CASCADE);
		menuItemHelp.setText("Help");
		menuHelp = new Menu(sShell, SWT.DROP_DOWN);
		menuItemHelp.setMenu(menuHelp);
		
		
	}

	public void launch() {
		createSShell();
		
		sShell.open();
		
		while (!sShell.isDisposed()) {
			if (!sShell.getDisplay().readAndDispatch())
				sShell.getDisplay().sleep();
		}

		//sShell.getDisplay().dispose();
		
	}	

}
