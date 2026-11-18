package com.nadhem.joueurs.entities;

import org.springframework.data.rest.core.config.Projection;

// Atelier 03 : projection "nomAuteur" (restreindre les donnees retournees)
@Projection(name = "nomAuteur", types = { Auteur.class })
public interface AuteurProjection {
	String getNom();

	String getPrenom();
}
