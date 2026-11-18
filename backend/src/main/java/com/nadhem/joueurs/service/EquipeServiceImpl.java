package com.nadhem.joueurs.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nadhem.joueurs.entities.Equipe;
import com.nadhem.joueurs.exceptions.EquipeNonVideException;
import com.nadhem.joueurs.repos.EquipeRepository;
import com.nadhem.joueurs.repos.JoueurRepository;

@Service
public class EquipeServiceImpl implements EquipeService {

	@Autowired
	EquipeRepository equipeRepository;

	@Autowired
	JoueurRepository joueurRepository;

	@Override
	public Equipe saveEquipe(Equipe e) {
		return equipeRepository.save(e);
	}

	@Override
	public Equipe updateEquipe(Equipe e) {
		return equipeRepository.save(e);
	}

	@Override
	public void deleteEquipe(Equipe e) {
		verifierEquipeVide(e.getId());
		equipeRepository.delete(e);
	}

	@Override
	public void deleteEquipeById(Long id) {
		verifierEquipeVide(id);
		equipeRepository.deleteById(id);
	}

	// Une equipe qui contient encore des joueurs ne peut pas etre supprimee
	// (integrite referentielle) : on leve une exception traduite en HTTP 409.
	private void verifierEquipeVide(Long id) {
		if (id != null && joueurRepository.countByEquipeId(id) > 0) {
			throw new EquipeNonVideException(id);
		}
	}

	@Override
	public Equipe getEquipe(Long id) {
		return equipeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Equipe introuvable : " + id));
	}

	@Override
	public List<Equipe> getAllEquipes() {
		return equipeRepository.findAll();
	}
}
