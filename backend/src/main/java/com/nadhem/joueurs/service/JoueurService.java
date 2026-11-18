package com.nadhem.joueurs.service;

import java.util.List;

import com.nadhem.joueurs.entities.Auteur;
import com.nadhem.joueurs.entities.Equipe;
import com.nadhem.joueurs.entities.Joueur;

public interface JoueurService {

	Joueur saveJoueur(Joueur j);

	Joueur updateJoueur(Joueur j);

	void deleteJoueur(Joueur j);

	void deleteJoueurById(Long id);

	Joueur getJoueur(Long id);

	List<Joueur> getAllJoueurs();

	// Methodes ajoutees a l'Atelier 02
	List<Joueur> findByNom(String nom);

	List<Joueur> findByNomContains(String nom);

	List<Joueur> findByNomPoste(String nom, String poste);

	List<Joueur> findByEquipe(Equipe equipe);

	List<Joueur> findByEquipeId(Long id);

	List<Joueur> findByAuteur(Auteur auteur);

	List<Joueur> findByAuteurId(Long id);

	List<Joueur> findByOrderByNomAsc();

	List<Joueur> trierJoueursNomsPoste();
}
