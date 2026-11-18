package com.nadhem.joueurs.restcontrollers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nadhem.joueurs.entities.Equipe;
import com.nadhem.joueurs.service.EquipeService;

@RestController
@RequestMapping("/api/equipes")
@CrossOrigin
public class EquipeRESTController {

	@Autowired
	EquipeService equipeService;

	// Retourner toutes les equipes
	@GetMapping()
	public List<Equipe> getAllEquipes() {
		return equipeService.getAllEquipes();
	}

	// Consulter une equipe par son id
	@GetMapping("/{id}")
	public Equipe getEquipeById(@PathVariable("id") Long id) {
		return equipeService.getEquipe(id);
	}

	// Creer une equipe
	@PostMapping()
	public Equipe createEquipe(@Valid @RequestBody Equipe equipe) {
		return equipeService.saveEquipe(equipe);
	}

	// Modifier une equipe
	@PutMapping()
	public Equipe updateEquipe(@Valid @RequestBody Equipe equipe) {
		return equipeService.updateEquipe(equipe);
	}

	// Supprimer une equipe
	@DeleteMapping("/{id}")
	public void deleteEquipe(@PathVariable("id") Long id) {
		equipeService.deleteEquipeById(id);
	}
}
