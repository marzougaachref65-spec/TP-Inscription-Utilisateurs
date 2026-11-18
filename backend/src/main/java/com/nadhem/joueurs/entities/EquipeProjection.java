package com.nadhem.joueurs.entities;

import org.springframework.data.rest.core.config.Projection;

// Atelier 03 : projection "nomEquipe" (restreindre les donnees retournees)
@Projection(name = "nomEquipe", types = { Equipe.class })
public interface EquipeProjection {
	String getNom();

	String getPays();
}
