package org.jity.UIClient.swt.graphicalObject;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.jity.common.referential.Job;

public class GCJob {
	private Job job;
	private int posX;
	private int posY;
	ArrayList<GCLink> arrayListBeforeLinks = new ArrayList<GCLink>();
	ArrayList<GCLink> arrayListAfterLinks = new ArrayList<GCLink>();
		
	private final static int RECT_WIDTH = 90;
	private final static int RECT_HEIGHT = 30;
	private final static int RECT_SYSTEM_COLOR = SWT.COLOR_DARK_MAGENTA;
	
	private final static int OVAL_WIDTH = 50;
	private final static int OVAL_HEIGHT = 50;
	
	/**
	 * Retourne Vrai si les coordonnées indiquées sont sur le GCJob dessinné
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isOn(int x, int y) {
		if ( x >= this.posX - RECT_WIDTH/2 && y >= this.posY - RECT_HEIGHT/2 &&
				x <= (this.posX - RECT_WIDTH/2)+RECT_WIDTH && y <= (this.posY - RECT_HEIGHT/2) + RECT_HEIGHT) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void drawShadow(Canvas canvas, int x, int y) {
		GC temp = new GC(canvas);
		temp.setLineStyle(SWT.LINE_DOT);
		temp.setForeground(canvas.getShell().getDisplay().getSystemColor(RECT_SYSTEM_COLOR));
		//temp.drawRectangle(x - RECT_WIDTH/2, y - RECT_HEIGHT/2, RECT_WIDTH, RECT_HEIGHT);
		//temp.drawRoundRectangle(x - RECT_WIDTH/2, y - RECT_HEIGHT/2, RECT_WIDTH, RECT_HEIGHT, 28, 10);
		temp.drawOval(x - OVAL_WIDTH/2, y - OVAL_HEIGHT/2, OVAL_WIDTH, OVAL_HEIGHT);
		temp.dispose();
	}
	
	public static void maskShadow(Canvas canvas, int x, int y) {
		GC temp = new GC(canvas);
		temp.setForeground(temp.getBackground());
		//temp.drawRectangle(x - RECT_WIDTH/2, y - RECT_HEIGHT/2, RECT_WIDTH, RECT_HEIGHT);
		temp.drawOval(x - OVAL_WIDTH/2, y - OVAL_HEIGHT/2, OVAL_WIDTH, OVAL_HEIGHT);
		temp.dispose();
	}
	
	public GCJob(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	/**
	 * Draw the Job objet on the canvas
	 * @param canvas
	 */
	public void draw(Canvas canvas) {
		GC gc = new GC(canvas);
		gc.setBackground(canvas.getShell().getDisplay().getSystemColor(RECT_SYSTEM_COLOR));
		//gc.fillRectangle(this.posX - RECT_WIDTH/2, this.posY - RECT_HEIGHT/2, RECT_WIDTH, RECT_HEIGHT);
		gc.fillOval(this.posX - OVAL_WIDTH/2, this.posY - OVAL_HEIGHT/2, OVAL_WIDTH, OVAL_HEIGHT);
		gc.dispose();
	}

	public void mask(Canvas canvas) {
		GC gc = new GC(canvas);
		gc.setBackground(gc.getBackground());
		//gc.fillRectangle(this.posX - RECT_WIDTH/2, this.posY - RECT_HEIGHT/2, RECT_WIDTH, RECT_HEIGHT);
		gc.fillOval(this.posX - OVAL_WIDTH/2, this.posY - OVAL_HEIGHT/2, OVAL_WIDTH, OVAL_HEIGHT);
		gc.dispose();
	}
	
	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}
	
	public void addBeforeLinks(GCLink gclink) {
		this.arrayListBeforeLinks.add(gclink);
	}
	
	public void addAfterLinks(GCLink gclink) {
		this.arrayListAfterLinks.add(gclink);
	}
	
	public GCLink getCurrentAfterLink() {
		Iterator<GCLink> iterGCLink = this.arrayListAfterLinks.iterator();
		while (iterGCLink.hasNext()) {
			GCLink currentLink = iterGCLink.next();
			
			if (currentLink.getEndX() == 0 && currentLink.getEndY() == 0) return currentLink;
		}
		
		return null;
	}
	
}
