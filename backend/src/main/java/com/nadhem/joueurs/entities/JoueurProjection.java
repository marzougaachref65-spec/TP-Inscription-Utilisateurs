package com.nadhem.joueurs.entities;

import org.springframework.data.rest.core.config.Projection;

// Atelier 03 : projection "nomJoueur" (restreindre les donnees retournees)
@Projection(name = "nomJoueur", types = { Joueur.class })
public interface JoueurProjection {
	String getNom();

	String getPrenom();

	String getPoste();
}
