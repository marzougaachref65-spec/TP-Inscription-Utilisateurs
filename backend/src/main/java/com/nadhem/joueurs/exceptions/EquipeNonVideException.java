package com.nadhem.joueurs.exceptions;

// Levee lorsqu'on essaie de supprimer une equipe qui contient encore des joueurs.
public class EquipeNonVideException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EquipeNonVideException(Long idEquipe) {
		super("Impossible de supprimer l'equipe " + idEquipe + " : elle contient encore des joueurs.");
	}
}
