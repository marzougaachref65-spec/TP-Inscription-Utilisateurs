package com.nadhem.joueurs.service;

import java.util.List;

import com.nadhem.joueurs.entities.Equipe;

public interface EquipeService {

	Equipe saveEquipe(Equipe e);

	Equipe updateEquipe(Equipe e);

	void deleteEquipe(Equipe e);

	void deleteEquipeById(Long id);

	Equipe getEquipe(Long id);

	List<Equipe> getAllEquipes();
}
