package org.jity.UIClient.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DrawLines {



	static final int sides = 18;



	public static void main(String[] args) {

	    final Point center = new Point(0,0);

	    final int[] radial = new int[sides*2];

	    final Display display = new Display();

	    final Shell shell = new Shell(display);

	    shell.addListener(SWT.Resize, new Listener() {

	        public void handleEvent(Event event) {

	            Rectangle bounds = shell.getClientArea();

	            center.x = bounds.x + bounds.width/2;

	            center.y = bounds.y + bounds.height/2;

	            for (int i = 0; i < sides; i++) {

	                double r = Math.PI*2 * i/sides;

	                radial[i*2] = (int)

	                    ((1+Math.cos(r))*center.x);

	                radial[i*2+1] = (int)

	                    ((1+Math.sin(r))*center.y);

	            }

	        }});

	    shell.addListener(SWT.Paint, new Listener() {

	        public void handleEvent(Event event) {

	            for (int i = 0; i < sides; i++) {

	                event.gc.drawLine(

	                    center.x, center.y,

	                    radial[i*2], radial[i*2+1]);

	            }

	            event.gc.drawPolygon(radial);

	        }});

	    shell.setText("Draw Lines");

	    shell.setSize(400, 400);

	    shell.open();

	    while (!shell.isDisposed()) {

	        if (!display.readAndDispatch())

	            display.sleep();

	    }

	    display.dispose();

	}}
