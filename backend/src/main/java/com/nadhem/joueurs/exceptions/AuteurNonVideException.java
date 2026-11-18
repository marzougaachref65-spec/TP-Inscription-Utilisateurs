package com.nadhem.joueurs.exceptions;

// Levee lorsqu'on essaie de supprimer un auteur qui a encore des joueurs.
public class AuteurNonVideException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AuteurNonVideException(Long idAuteur) {
		super("Impossible de supprimer l'auteur " + idAuteur + " : il a encore des joueurs.");
	}
}
