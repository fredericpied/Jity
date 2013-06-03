package org.jity.UIClient.swt;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolItem;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.jity.UIClient.swt.graphicalObject.GCJob;
import org.jity.UIClient.swt.graphicalObject.GCLink;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;

public class DrawingWindows {

	private Shell sShell = null;
	private ArrayList<GCJob> arrayListGCJobs = new ArrayList<GCJob>();
	private ArrayList<GCLink> arrayListGCLink = new ArrayList<GCLink>();

	/**
	 * This method initializes sShell
	 * 
	 * @wbp.parser.entryPoint
	 */
	private void createSShell() {

		sShell = new Shell();
		sShell.setText("Shell");
		sShell.setSize(new Point(609, 301));
		sShell.setLayout(new GridLayout(1, false));

		// Toolbar
		ToolBar toolBar = new ToolBar(sShell, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		final ToolItem tltmJobs = new ToolItem(toolBar, SWT.RADIO);
		tltmJobs.setText("Jobs");
		final ToolItem tltmLinks = new ToolItem(toolBar, SWT.RADIO);
		tltmLinks.setText("Links");
		final ToolItem tltmSelect = new ToolItem(toolBar, SWT.RADIO);
		tltmSelect.setText("Select");

		// Canvas for drawing
		final Canvas canvas = new Canvas(sShell, SWT.NO_REDRAW_RESIZE | SWT.BORDER);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				redrawCanvas(canvas);
			}
		});

		final Listener canvasDrawingListener = new Listener() {
			int lineStartX = 0, lineStartY = 0;
			int lastLineEndX = 0, lastLineEndY = 0;
			boolean lineStarted = false;
			boolean rectMoveStarted = false;

			int lastRectX = 0, lastRectY = 0;

			public void handleEvent(Event event) {
				switch (event.type) {

				// Mouvement de souris sur le canvas
				case SWT.MouseMove:

					if (tltmJobs.getSelection()) {
						// Si on est en mode Job

						if (lineStarted) {
							// si la ligne est démarrée et que l'on a changer de
							// mode.

							// On efface la ligne précédente
							GCLink.maskShadow(canvas, lineStartX, lineStartY, lastLineEndX, lastLineEndY);

							lineStarted = false;
						}

						// On efface le rectangle précédent
						GCJob.maskShadow(canvas, lastRectX, lastRectY);

						// on affiche le cadre en point
						GCJob.drawShadow(canvas, event.x, event.y);

						// On sauvegarde la position du cadre précédent pour
						// l'effacer quand la souris bougera
						lastRectX = event.x;
						lastRectY = event.y;

					} else if (tltmLinks.getSelection()) {
						// Si on est en mode Link

						// On efface le rectangle précédent
						GCJob.maskShadow(canvas, lastRectX, lastRectY);

						if (lineStarted) {
							// si la ligne est démarrée

							// On efface la précédente ligne dès que la souris
							// bouge
							GCLink.maskShadow(canvas, lineStartX, lineStartY, lastLineEndX, lastLineEndY);

							// on affiche la ligne en point
							GCLink.drawShadow(canvas, lineStartX, lineStartY, event.x, event.y);

							// On sauvegarde la fin de la ligne précédente pour
							// l'effacer quand la souris bougera
							lastLineEndX = event.x;
							lastLineEndY = event.y;
						}
					} else if (tltmSelect.getSelection()) {
						// Si on est en mode Select

						if (lineStarted) {
							// si la ligne est démarrée et que l'on a changer de
							// mode.

							// On efface la précédente ligne dès que la souris
							// bouge
							GCLink.maskShadow(canvas, lineStartX, lineStartY, lastLineEndX, lastLineEndY);

							lineStarted = false;
						}

						if (rectMoveStarted) {
							// On efface le rectangle précédent
							GCJob.maskShadow(canvas, lastRectX, lastRectY);


							// on affiche le cadre en point
							GCJob.drawShadow(canvas, event.x, event.y);

							// On sauvegarde la position du cadre précédent pour
							// l'effacer quand la souris bougera
							lastRectX = event.x;
							lastRectY = event.y;

													
							// On déplace également les liens
							// si la ligne est démarrée

							// On efface la précédente ligne dès que la souris
							// bouge
							GCLink.maskShadow(canvas, lineStartX, lineStartY, lastLineEndX, lastLineEndY);

							// on affiche la ligne en point
							GCLink.drawShadow(canvas, lineStartX, lineStartY, event.x, event.y);

							// On sauvegarde la fin de la ligne précédente pour
							// l'effacer quand la souris bougera
							lastLineEndX = event.x;
							lastLineEndY = event.y;
							
						} else {
							// On efface le rectangle précédent
							GCJob.maskShadow(canvas, lastRectX, lastRectY);
						}
						
					}
					break;

				// Clisk sur la souris
				case SWT.MouseDown:
					if (tltmJobs.getSelection()) {
						// Si on est en mode Job
						// le rectangle est positionné, on le dessine
						// définitivement
						GCJob job = new GCJob(event.x, event.y);
						job.draw(canvas);

						// On ajoute à la liste
						arrayListGCJobs.add(job);
					} else if (tltmLinks.getSelection()) {
						// Si on est en mode Link
						if (!lineStarted) {

							if (getSelectedGCJob(event.x, event.y) != null) {
								// Si on est sur un Job
								// Si la ligne n'est pas démarrée, on la démarre
								// lors du
								// click on enregistre le point de départ
								GCJob gcjob = getSelectedGCJob(event.x, event.y);
								lineStartX = gcjob.getPosX();
								lineStartY = gcjob.getPosY();
								
								GCLink gclink = new GCLink();
								gclink.start(lineStartX, lineStartY);
								gcjob.addAfterLinks(gclink);
								
								lineStarted = true;
							}

						} else {

							// On efface la précédente ligne dès que la souris
							// bouge
							GCLink.maskShadow(canvas, lineStartX, lineStartY, lastLineEndX, lastLineEndY);

							if (getSelectedGCJob(event.x, event.y) != null) {
								// Si on est sur un Job
								GCJob gcjobEnd = getSelectedGCJob(event.x, event.y);
								
								// Get Start job
								GCJob gcjobStart = getSelectedGCJob(lineStartX, lineStartY);
								GCLink currentLink = gcjobStart.getCurrentAfterLink();
								currentLink.end(event.x, event.y);
								currentLink.draw(canvas);
								
								// Add before link to current Job
								gcjobEnd.addBeforeLinks(currentLink);
								
								// On ajoute à la liste des liens
								arrayListGCLink.add(currentLink);

								lineStarted = false;

							}

						}
					} else if (tltmSelect.getSelection()) {
				
						if (getSelectedGCJob(event.x, event.y) != null) {
							// Si on est sur un Job, on passe en mode déplacement
							GCJob gcjob = getSelectedGCJob(event.x, event.y);
							gcjob.mask(canvas);
							arrayListGCJobs.remove(gcjob);
							
							rectMoveStarted = true;
							
							
							//lineStartX = currentLink.getStartX();
							//lineStartY = currentLink.getStartY();
							
							GCLink gclink = new GCLink();
							gclink.start(lineStartX, lineStartY);
							gcjob.addAfterLinks(gclink);
							
							lineStarted = true;
						
						} else if (rectMoveStarted) {
							// Si on est en mode Job
							// le rectangle est positionné, on le dessine
							// définitivement
							GCJob job = new GCJob(event.x, event.y);
							job.draw(canvas);

							// On ajoute à la liste
							arrayListGCJobs.add(job);
							
							rectMoveStarted = false;
						}
						
					}

					break;

				}

				// Redrawing objets on canvas
				redrawCanvas(canvas);
			}
		};
		canvas.addListener(SWT.MouseDown, canvasDrawingListener);
		canvas.addListener(SWT.MouseMove, canvasDrawingListener);

	}

	/**
	 * Return selected GCJob
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private GCJob getSelectedGCJob(int x, int y) {

		Iterator<GCJob> iterGCJob = this.arrayListGCJobs.iterator();
		while (iterGCJob.hasNext()) {
			GCJob job = iterGCJob.next();

			if (job.isOn(x, y))
				return job;
		}

		return null;
	}

	// /**
	// * Return true if mouse is on a Job
	// * @param x
	// * @param y
	// * @return
	// */
	// private boolean isOnGCJob(int x, int y) {
	//		
	// Iterator<GCJob> iterGCJob = this.arrayListGCJobs.iterator();
	// while (iterGCJob.hasNext()) {
	// GCJob job = iterGCJob.next();
	//
	// if (job.isOn(x, y)) return true;
	// }
	//		
	// return false;
	//		
	// }

	/**
	 * Redraw graphical objets on canvas
	 */
	private void redrawCanvas(Canvas canvas) {

		Iterator<GCLink> iterGCLink = this.arrayListGCLink.iterator();
		while (iterGCLink.hasNext()) {
			GCLink link = iterGCLink.next();

			link.draw(canvas);
		}

		Iterator<GCJob> iterGCJob = this.arrayListGCJobs.iterator();
		while (iterGCJob.hasNext()) {
			GCJob job = iterGCJob.next();

			job.draw(canvas);
		}

	}

}
