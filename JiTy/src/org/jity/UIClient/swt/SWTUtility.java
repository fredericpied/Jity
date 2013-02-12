package org.jity.UIClient.swt;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class SWTUtility {

	/**
	 * Set windows to screen center
	 * 
	 * @param display
	 * @param shell
	 */
	public static void centerOnScreen(Display display, Shell shell) {
		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}

	/**
	 * Show message windows
	 * 
	 * @param shell
	 * @param message
	 */
	public static void showInformationMessage(Shell shell, String message) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		messageBox.setMessage(message);
		messageBox.setText("Information");
		messageBox.open();
	}

	/**
	 * Show error message
	 * 
	 * @param shell
	 * @param message
	 */
	public static void showErrorMessage(Shell shell, String message) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		messageBox.setMessage(message);
		messageBox.setText("Error");
		messageBox.open();
	}

	
	/**
	 * Affiche une question donnée en entrée et attend la réponse de
	 * l'utilisateur (deux boutons OUI ou NON), retourne True si l'utilisateur à
	 * cliquer sur le bouton OUI
	 * 
	 * @param shell
	 * @param messageQuestion
	 * @return
	 */
	public static boolean showQuestionBox(Shell shell, String messageQuestion) {
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setMessage(messageQuestion);
		messageBox.setText("Question");
		int rc = messageBox.open();
		switch (rc) {
		case SWT.YES:
			return true;
		case SWT.NO:
			return false;
		}
		return false;
	}

	/**
	 * Change l'ordre de la valeur sélectionnée dans une liste SWT. si monter =
	 * true, la valeur est déplacée vers le haut, sinon vers le bas
	 * 
	 * @param liste
	 * @param monter
	 */
	public static void changerOrdreDansliste(List liste, boolean monter) {

		// Si la liste contient au moins une valeur
		if (liste.getItemCount() > 0) {

			// Nombre de valeur dans la liste
			int maxIndex = liste.getItemCount();

			// Index de la valeur sélectionné
			int index = liste.getSelectionIndex();

			if (monter) {
				// Si on veut monter la valeur
				if (index > 0) {
					// Si on est pas déja en première prosition

					// Valeur a déplacée
					String chaine = liste.getItem(index);

					// Valeur remplacée par la valeur déplacée
					String chaineRemplacee = liste.getItem(index - 1);

					// On déplace la valeur à remplacer à la valeur courante
					liste.setItem(index, chaineRemplacee);
					// On remonte la valeur sélectionnée d'un cran
					liste.setItem(index - 1, chaine);
					// On sélectionne la valeur à déplacé
					liste.select(index - 1);
				}
			} else {
				if (!(index < 0) && index < maxIndex - 1) {
					String chaine = liste.getItem(index);
					String chaineRemplacee = liste.getItem(index + 1);
					liste.setItem(index, chaineRemplacee);
					liste.setItem(index + 1, chaine);
					liste.select(index + 1);
				}
			}
		}
	}

	/**
	 * Déplace la valeur sélectionnée de la listeSource vers la listeCible
	 * 
	 * @param listeSource
	 * @param listeCible
	 */
	public static void deplacerDeListeVersListe(List listeSource, List listeCible) {
		if (listeSource.getItemCount() > 0 && listeSource.getSelectionCount() == 1) {
			// Si la liste source contient des valeur et qu'une valeur
			// (seulement une)
			// est sélectionnée

			// Index de la valeur sélectionnée
			int index = listeSource.getSelectionIndex();
			// Valeur sélectionnée
			String valeurSelectionne = listeSource.getItem(index);

			// Si l'élément n'existe pas déja dans la liste cible, on l'ajoute
			if (listeCible.indexOf(valeurSelectionne) == -1)
				listeCible.add(valeurSelectionne);
			// et on le supprime de la liste source
			listeSource.remove(index);
		}
	}
	
	/**
	 * return "" if stringValue is null
	 * @param stringValue
	 */
	public static String nvl(String stringValue) {
		if (stringValue == null) return "";
		else return stringValue;
	}

}
