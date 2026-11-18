package com.nadhem.joueurs.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nadhem.joueurs.entities.Auteur;
import com.nadhem.joueurs.exceptions.AuteurNonVideException;
import com.nadhem.joueurs.repos.AuteurRepository;
import com.nadhem.joueurs.repos.JoueurRepository;

@Service
public class AuteurServiceImpl implements AuteurService {

	@Autowired
	AuteurRepository auteurRepository;

	@Autowired
	JoueurRepository joueurRepository;

	@Override
	public Auteur saveAuteur(Auteur a) {
		return auteurRepository.save(a);
	}

	@Override
	public Auteur updateAuteur(Auteur a) {
		return auteurRepository.save(a);
	}

	@Override
	public void deleteAuteur(Auteur a) {
		verifierAuteurLibre(a.getId());
		auteurRepository.delete(a);
	}

	@Override
	public void deleteAuteurById(Long id) {
		verifierAuteurLibre(id);
		auteurRepository.deleteById(id);
	}

	private void verifierAuteurLibre(Long id) {
		if (id != null && joueurRepository.countByAuteurId(id) > 0) {
			throw new AuteurNonVideException(id);
		}
	}

	@Override
	public Auteur getAuteur(Long id) {
		return auteurRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Auteur introuvable : " + id));
	}

	@Override
	public List<Auteur> getAllAuteurs() {
		return auteurRepository.findAll();
	}

	@Override
	public List<Auteur> findByNom(String nom) {
		return auteurRepository.findByNom(nom);
	}

	@Override
	public List<Auteur> findByNomContains(String nom) {
		return auteurRepository.findByNomContains(nom);
	}

	@Override
	public List<Auteur> findByOrderByNomAsc() {
		return auteurRepository.findByOrderByNomAsc();
	}
}
