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
	 * Affiche une question donn�e en entr�e et attend la r�ponse de
	 * l'utilisateur (deux boutons OUI ou NON), retourne True si l'utilisateur �
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
	 * Change l'ordre de la valeur s�lectionn�e dans une liste SWT. si monter =
	 * true, la valeur est d�plac�e vers le haut, sinon vers le bas
	 * 
	 * @param liste
	 * @param monter
	 */
	public static void changerOrdreDansliste(List liste, boolean monter) {

		// Si la liste contient au moins une valeur
		if (liste.getItemCount() > 0) {

			// Nombre de valeur dans la liste
			int maxIndex = liste.getItemCount();

			// Index de la valeur s�lectionn�
			int index = liste.getSelectionIndex();

			if (monter) {
				// Si on veut monter la valeur
				if (index > 0) {
					// Si on est pas d�ja en premi�re prosition

					// Valeur a d�plac�e
					String chaine = liste.getItem(index);

					// Valeur remplac�e par la valeur d�plac�e
					String chaineRemplacee = liste.getItem(index - 1);

					// On d�place la valeur � remplacer � la valeur courante
					liste.setItem(index, chaineRemplacee);
					// On remonte la valeur s�lectionn�e d'un cran
					liste.setItem(index - 1, chaine);
					// On s�lectionne la valeur � d�plac�
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
	 * D�place la valeur s�lectionn�e de la listeSource vers la listeCible
	 * 
	 * @param listeSource
	 * @param listeCible
	 */
	public static void deplacerDeListeVersListe(List listeSource, List listeCible) {
		if (listeSource.getItemCount() > 0 && listeSource.getSelectionCount() == 1) {
			// Si la liste source contient des valeur et qu'une valeur
			// (seulement une)
			// est s�lectionn�e

			// Index de la valeur s�lectionn�e
			int index = listeSource.getSelectionIndex();
			// Valeur s�lectionn�e
			String valeurSelectionne = listeSource.getItem(index);

			// Si l'�l�ment n'existe pas d�ja dans la liste cible, on l'ajoute
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
