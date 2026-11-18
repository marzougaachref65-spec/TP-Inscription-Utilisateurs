package com.nadhem.joueurs.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nadhem.joueurs.entities.Auteur;
import com.nadhem.joueurs.entities.Equipe;
import com.nadhem.joueurs.entities.Joueur;
import com.nadhem.joueurs.repos.JoueurRepository;

@Service
public class JoueurServiceImpl implements JoueurService {

	@Autowired
	JoueurRepository joueurRepository;

	@Override
	public Joueur saveJoueur(Joueur j) {
		return joueurRepository.save(j);
	}

	@Override
	public Joueur updateJoueur(Joueur j) {
		return joueurRepository.save(j);
	}

	@Override
	public void deleteJoueur(Joueur j) {
		joueurRepository.delete(j);
	}

	@Override
	public void deleteJoueurById(Long id) {
		joueurRepository.deleteById(id);
	}

	@Override
	public Joueur getJoueur(Long id) {
		return joueurRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Joueur introuvable : " + id));
	}

	@Override
	public List<Joueur> getAllJoueurs() {
		return joueurRepository.findAll();
	}

	@Override
	public List<Joueur> findByNom(String nom) {
		return joueurRepository.findByNom(nom);
	}

	@Override
	public List<Joueur> findByNomContains(String nom) {
		return joueurRepository.findByNomContains(nom);
	}

	@Override
	public List<Joueur> findByNomPoste(String nom, String poste) {
		return joueurRepository.findByNomPoste(nom, poste);
	}

	@Override
	public List<Joueur> findByEquipe(Equipe equipe) {
		return joueurRepository.findByEquipe(equipe);
	}

	@Override
	public List<Joueur> findByEquipeId(Long id) {
		return joueurRepository.findByEquipeId(id);
	}

	@Override
	public List<Joueur> findByAuteur(Auteur auteur) {
		return joueurRepository.findByAuteur(auteur);
	}

	@Override
	public List<Joueur> findByAuteurId(Long id) {
		return joueurRepository.findByAuteurId(id);
	}

	@Override
	public List<Joueur> findByOrderByNomAsc() {
		return joueurRepository.findByOrderByNomAsc();
	}

	@Override
	public List<Joueur> trierJoueursNomsPoste() {
		return joueurRepository.trierJoueursNomsPoste();
	}
}
