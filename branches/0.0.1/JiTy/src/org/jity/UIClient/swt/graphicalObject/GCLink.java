package org.jity.UIClient.swt.graphicalObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;

public class GCLink {
	private int startX;
	private int startY;
	private int endX;
	private int endY;
		
	private final static int LINE_SYSTEM_COLOR = SWT.COLOR_BLACK;
	
	public GCLink() {};
	
	public void start(int startX, int startY) {
		this.startX = startX;
		this.startY = startY;
	}
	
	public void end(int endX, int endY) {
		this.endX = endX;
		this.endY = endY;
	}
	
	public GCLink(int startX, int startY, int endX, int endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}
	
	public static void drawShadow(Canvas canvas, int beginX, int beginY, int endX, int endY) {
		GC tempLine = new GC(canvas);
		tempLine.setLineStyle(SWT.LINE_DOT);
		tempLine.setForeground(canvas.getShell().getDisplay().getSystemColor(GCLink.LINE_SYSTEM_COLOR));
		tempLine.drawLine(beginX, beginY, endX, endY);
		tempLine.dispose();
	}
	
	public static void maskShadow(Canvas canvas, int beginX, int beginY, int endX, int endY) {
		GC tempLine = new GC(canvas);
		tempLine.setForeground(tempLine.getBackground());
		tempLine.drawLine(beginX, beginY, endX, endY);
		tempLine.dispose();
	}
	
	public void draw(Canvas canvas) {

		GC line = new GC(canvas);
		line.setForeground(canvas.getShell().getDisplay().getSystemColor(LINE_SYSTEM_COLOR));
		//line.setLineWidth(1);
		//line.setAntialias(SWT.ON); 
		line.drawLine(startX, startY, endX, endY);
		line.dispose();
		
		// On ajoute une flèche
		//addArrow(canvas, endX, endY);
	}
	
	private void addArrow(Canvas canvas, int lineEndX, int lineEndY) {
		
		int w = canvas.getSize().y;
		int h = canvas.getSize().x;

		//double x1 = w*3/24, y1 = h*3/32, x2 = w*11/24, y2 = y1;  
		double x1 = w/2, y1 = h/2, x2 = w*27/32, y2 = h*27/32;  
		double theta = Math.atan2(y2 - y1, x2 - x1);  

        int barb = 20; 
        double phi = Math.PI/6;   
        int x = (int) (lineEndX - barb * Math.cos(theta + phi));  
        int y = (int) (lineEndY - barb * Math.sin(theta + phi));  
        
        GC arrow = new GC(canvas);
        arrow.drawLine(lineEndX, lineEndY, x, y);
        x = (int) (lineEndX - barb * Math.cos(theta - phi));  
        y = (int) (lineEndY - barb * Math.sin(theta - phi));  
        arrow.drawLine(lineEndX, lineEndY, x, y);
		arrow.dispose();
	}
	
	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getEndX() {
		return endX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
	}

	public int getEndY() {
		return endY;
	}

	public void setEndY(int endY) {
		this.endY = endY;
	}
	
	
}
