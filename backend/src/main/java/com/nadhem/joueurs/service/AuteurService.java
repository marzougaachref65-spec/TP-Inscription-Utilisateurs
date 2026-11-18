package com.nadhem.joueurs.service;

import java.util.List;

import com.nadhem.joueurs.entities.Auteur;

public interface AuteurService {

	Auteur saveAuteur(Auteur a);

	Auteur updateAuteur(Auteur a);

	void deleteAuteur(Auteur a);

	void deleteAuteurById(Long id);

	Auteur getAuteur(Long id);

	List<Auteur> getAllAuteurs();

	List<Auteur> findByNom(String nom);

	List<Auteur> findByNomContains(String nom);

	List<Auteur> findByOrderByNomAsc();
}
