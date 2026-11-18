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

import com.nadhem.joueurs.entities.Auteur;
import com.nadhem.joueurs.service.AuteurService;

// Atelier 03 : web services REST CRUD de l'entite Auteur
@RestController
@RequestMapping("/api/auteurs")
@CrossOrigin
public class AuteurRESTController {

	@Autowired
	AuteurService auteurService;

	@GetMapping()
	public List<Auteur> getAllAuteurs() {
		return auteurService.getAllAuteurs();
	}

	@GetMapping("/{id}")
	public Auteur getAuteurById(@PathVariable("id") Long id) {
		return auteurService.getAuteur(id);
	}

	@PostMapping()
	public Auteur createAuteur(@Valid @RequestBody Auteur auteur) {
		return auteurService.saveAuteur(auteur);
	}

	@PutMapping()
	public Auteur updateAuteur(@Valid @RequestBody Auteur auteur) {
		return auteurService.updateAuteur(auteur);
	}

	@DeleteMapping("/{id}")
	public void deleteAuteur(@PathVariable("id") Long id) {
		auteurService.deleteAuteurById(id);
	}

	// Atelier 07 : recherche par nom (equivalent de /prodsByName/{nom})
	@GetMapping("/nom/{nom}")
	public List<Auteur> findByNomContains(@PathVariable("nom") String nom) {
		return auteurService.findByNomContains(nom);
	}
}
