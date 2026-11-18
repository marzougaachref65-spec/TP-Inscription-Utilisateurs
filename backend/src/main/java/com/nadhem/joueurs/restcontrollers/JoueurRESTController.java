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

import com.nadhem.joueurs.entities.Joueur;
import com.nadhem.joueurs.service.JoueurService;

@RestController
@RequestMapping("/api/joueurs")
@CrossOrigin
public class JoueurRESTController {

	@Autowired
	JoueurService joueurService;

	// 1. Retourner tous les joueurs
	@GetMapping()
	public List<Joueur> getAllJoueurs() {
		return joueurService.getAllJoueurs();
	}

	// 2. Consulter un joueur par son id
	@GetMapping("/{id}")
	public Joueur getJoueurById(@PathVariable("id") Long id) {
		return joueurService.getJoueur(id);
	}

	// 3. Creer un joueur
	@PostMapping()
	public Joueur createJoueur(@Valid @RequestBody Joueur joueur) {
		return joueurService.saveJoueur(joueur);
	}

	// 4. Modifier un joueur
	@PutMapping()
	public Joueur updateJoueur(@Valid @RequestBody Joueur joueur) {
		return joueurService.updateJoueur(joueur);
	}

	// 5. Supprimer un joueur
	@DeleteMapping("/{id}")
	public void deleteJoueur(@PathVariable("id") Long id) {
		joueurService.deleteJoueurById(id);
	}

	// 6. Retourner la liste des joueurs d'une equipe donnee
	@GetMapping("/equipe/{idEquipe}")
	public List<Joueur> getJoueursByEquipeId(@PathVariable("idEquipe") Long idEquipe) {
		return joueurService.findByEquipeId(idEquipe);
	}

	// 7. Nouvelle association : joueurs d'un auteur donne
	@GetMapping("/auteur/{idAuteur}")
	public List<Joueur> getJoueursByAuteurId(@PathVariable("idAuteur") Long idAuteur) {
		return joueurService.findByAuteurId(idAuteur);
	}

	// Atelier 07 : recherche par nom, equivalent de /prodsByName/{nom} de l'enonce
	@GetMapping("/nom/{nom}")
	public List<Joueur> findByNomContains(@PathVariable("nom") String nom) {
		return joueurService.findByNomContains(nom);
	}
}
