package org.jity.UIClient.swt;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class DrawingWindows {

	private Shell sShell = null;  //  @jve:decl-index=0:visual-constraint="10,10"
    	
	
	/**
	 * This method initializes sShell
	 * @wbp.parser.entryPoint
	 */
	private void createSShell() {
			
		sShell = new Shell();
		sShell.setText("Shell");
		sShell.setSize(new Point(609, 301));
		sShell.setLayout(new GridLayout(1, false));
		
		final Color jobMarron = new Color(sShell.getDisplay(), 90, 90, 60);
		
		ToolBar toolBar = new ToolBar(sShell, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		final ScrolledComposite scrolledComposite = new ScrolledComposite(sShell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

//		final Listener listenerDrawLinks = new Listener() {
//			int lastX = 0, lastY = 0;
//
//			public void handleEvent(Event event) {
//				switch (event.type) {
//				
//				case SWT.MouseMove:
//					if ((event.stateMask & SWT.BUTTON1) == 0) break;
//					GC gc = new GC(scrolledComposite);
//					gc.drawLine(lastX, lastY, event.x, event.y);
//					gc.dispose();
//
//				case SWT.MouseDown:
//					lastX = event.x;
//					lastY = event.y;
//					break;
//				
//				}
//			}
//		};
		
		final Listener listenerDrawLinks = new Listener() {
			int startX = 0, startY = 0;
			boolean lineStarted = false;
			Color systemBlue = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
			Color systemBackgroun = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
			
			public void handleEvent(Event event) {
				switch (event.type) {
				
				case SWT.MouseMove:
					if (lineStarted) {
						
					//if ((event.stateMask & SWT.BUTTON1) == 0) break;
						GC gc1 = new GC(scrolledComposite);
						gc1.setForeground(systemBlue);
						gc1.drawLine(startX, startY, event.x, event.y);
						gc1.dispose();
					}
					break;
					
				case SWT.MouseDown:
					if (!lineStarted) {
						startX = event.x;
						startY = event.y;
						GC gc2 = new GC(scrolledComposite);
						gc2.drawPoint(event.x, event.y);
						gc2.dispose();
						lineStarted = true;
					} else {
						GC gc2 = new GC(scrolledComposite);
						gc2.drawLine(startX, startY, event.x, event.y);
						gc2.dispose();
						lineStarted = false;
					}
					break;
				
				}
			}
		};
		
		
		
		final Listener listenerDrawJobs  = new Listener() {

			public void handleEvent(Event event) {
				switch (event.type) {
				
//				case SWT.MouseMove:
//					if ((event.stateMask & SWT.BUTTON1) == 0) break;
//					GC gc = new GC(shell);
//					gc.drawLine(lastX, lastY, event.x, event.y);
//					gc.dispose();
//					break;
					
				case SWT.MouseDown:
					// Si on est pas sur un job, on dessine un nouveau job

			        GC gc = new GC(scrolledComposite);
			        gc.setBackground(jobMarron);
			        gc.fillRectangle(event.x-45, event.y-15, 90, 30);
			        gc.dispose();
			        
			        // Si on est sur un job, on le selectione
			        
			        
					break;
				
				}
			}
		};

		
		final ToolItem tltmJobs = new ToolItem(toolBar, SWT.RADIO);
		final ToolItem tltmLinks = new ToolItem(toolBar, SWT.RADIO);

		tltmJobs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tltmJobs.getSelection()) {
					System.out.println("drawJobs");
					scrolledComposite.removeListener(SWT.MouseDown, listenerDrawLinks);
					scrolledComposite.removeListener(SWT.MouseMove, listenerDrawLinks);
					scrolledComposite.addListener(SWT.MouseDown, listenerDrawJobs);
					scrolledComposite.addListener(SWT.MouseMove, listenerDrawJobs);
				}
			}
		});
		tltmJobs.setText("Jobs");
		

		tltmLinks.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (tltmLinks.getSelection()) {
					System.out.println("drawLinks");
					scrolledComposite.removeListener(SWT.MouseDown, listenerDrawJobs);
					scrolledComposite.removeListener(SWT.MouseMove, listenerDrawJobs);
					scrolledComposite.addListener(SWT.MouseDown, listenerDrawLinks);
					scrolledComposite.addListener(SWT.MouseMove, listenerDrawLinks);
				}
			}
			
		});
		tltmLinks.setText("Links");
				

	}

}
