package com.nadhem.joueurs;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nadhem.joueurs.entities.Auteur;
import com.nadhem.joueurs.entities.Equipe;
import com.nadhem.joueurs.entities.Joueur;
import com.nadhem.joueurs.repos.AuteurRepository;
import com.nadhem.joueurs.repos.EquipeRepository;
import com.nadhem.joueurs.repos.JoueurRepository;

@SpringBootTest
class JoueursEquipesApplicationTests {

	@Autowired
	private JoueurRepository joueurRepository;

	@Autowired
	private EquipeRepository equipeRepository;

	@Autowired
	private AuteurRepository auteurRepository;

	/* ------------------------------------------------------------------ */
	/* Methodes utilitaires (pour creer rapidement des donnees de test)    */
	/* ------------------------------------------------------------------ */

	private Equipe creerEquipe(String nom, String ville, String pays) {
		Equipe e = new Equipe();
		e.setNom(nom);
		e.setVille(ville);
		e.setPays(pays);
		e.setDateCreation(LocalDate.now());
		return equipeRepository.save(e);
	}

	private Auteur creerAuteur(String nom, String prenom, String nationalite) {
		Auteur a = new Auteur();
		a.setNom(nom);
		a.setPrenom(prenom);
		a.setNationalite(nationalite);
		return auteurRepository.save(a);
	}

	private Joueur creerJoueur(String nom, String prenom, String poste, Integer age, Integer numeroMaillot,
			Equipe equipe) {
		return creerJoueur(nom, prenom, poste, age, numeroMaillot, equipe, null);
	}

	private Joueur creerJoueur(String nom, String prenom, String poste, Integer age, Integer numeroMaillot,
			Equipe equipe, Auteur auteur) {
		Joueur j = new Joueur();
		j.setNom(nom);
		j.setPrenom(prenom);
		j.setPoste(poste);
		j.setAge(age);
		j.setNumeroMaillot(numeroMaillot);
		j.setEquipe(equipe);
		j.setAuteur(auteur);
		return joueurRepository.save(j);
	}

	/* ================================================================== */
	/* SECTION 1 - Atelier 01 : CRUD sur les entites                       */
	/* ================================================================== */

	@Test
	public void testCreateEquipe() {
		Equipe eq = creerEquipe("Esperance de Tunis", "Tunis", "Tunisie");
		System.out.println("Equipe creee : " + eq);
	}

	@Test
	public void testCreateJoueur() {
		Equipe eq = creerEquipe("Club Africain", "Ariana", "Tunisie");
		Joueur j = creerJoueur("Ben Ali", "Karim", "Attaquant", 25, 10, eq);
		System.out.println("Joueur cree : " + j);
	}

	@Test
	public void testFindJoueur() {
		Equipe eq = creerEquipe("Etoile du Sahel", "Sfax", "Tunisie");
		Joueur cree = creerJoueur("Trabelsi", "Sami", "Milieu", 28, 8, eq);
		Joueur j = joueurRepository.findById(cree.getId()).get();
		System.out.println(j);
	}

	@Test
	public void testUpdateJoueur() {
		Equipe eq = creerEquipe("CS Sfaxien", "Sfax", "Tunisie");
		Joueur j = creerJoueur("Gharbi", "Ali", "Defenseur", 30, 5, eq);
		j.setPoste("Gardien");
		j.setAge(31);
		joueurRepository.save(j);
		System.out.println("Joueur modifie : " + j);
	}

	@Test
	public void testDeleteJoueur() {
		Equipe eq = creerEquipe("AS Marsa", "Marsa", "Tunisie");
		Joueur j = creerJoueur("Jaziri", "Walid", "Attaquant", 27, 9, eq);
		joueurRepository.deleteById(j.getId());
		System.out.println("Joueur supprime, id = " + j.getId());
	}

	@Test
	public void testListerTousJoueurs() {
		List<Joueur> joueurs = joueurRepository.findAll();
		for (Joueur j : joueurs) {
			System.out.println(j);
		}
	}

	/* ================================================================== */
	/* SECTION 2 - Atelier 02 : interrogation des donnees                  */
	/* ================================================================== */

	@Test
	public void testFindByNom() {
		Equipe eq = creerEquipe("US Monastir", "Monastir", "Tunisie");
		creerJoueur("Messi", "Lionel", "Attaquant", 35, 10, eq);
		List<Joueur> joueurs = joueurRepository.findByNom("Messi");
		for (Joueur j : joueurs) {
			System.out.println(j);
		}
	}

	@Test
	public void testFindByNomContains() {
		Equipe eq = creerEquipe("Real Madrid", "Madrid", "Espagne");
		creerJoueur("Benzema", "Karim", "Attaquant", 36, 9, eq);
		List<Joueur> joueurs = joueurRepository.findByNomContains("Benz");
		for (Joueur j : joueurs) {
			System.out.println(j);
		}
	}

	@Test
	public void testFindByNomPoste() {
		Equipe eq = creerEquipe("Barcelona", "Barcelone", "Espagne");
		creerJoueur("Pedri", "Pedro", "Milieu", 24, 20, eq);
		List<Joueur> joueurs = joueurRepository.findByNomPoste("Ped", "Milieu");
		for (Joueur j : joueurs) {
			System.out.println(j);
		}
	}

	@Test
	public void testFindByEquipe() {
		Equipe eq = creerEquipe("Juventus", "Turin", "Italie");
		creerJoueur("Vlahovic", "Dusan", "Attaquant", 23, 11, eq);

		Equipe recherche = new Equipe();
		recherche.setId(eq.getId());

		List<Joueur> joueurs = joueurRepository.findByEquipe(recherche);
		for (Joueur j : joueurs) {
			System.out.println(j);
		}
	}

	@Test
	public void testFindByEquipeId() {
		Equipe eq = creerEquipe("Liverpool", "Liverpool", "Angleterre");
		creerJoueur("Salah", "Mohamed", "Attaquant", 31, 11, eq);

		List<Joueur> joueurs = joueurRepository.findByEquipeId(eq.getId());
		for (Joueur j : joueurs) {
			System.out.println(j);
		}
	}

	@Test
	public void testFindByOrderByNomAsc() {
		Equipe eq = creerEquipe("Bayern Munich", "Munich", "Allemagne");
		creerJoueur("Muller", "Thomas", "Milieu", 32, 17, eq);
		creerJoueur("Neuer", "Manuel", "Gardien", 35, 1, eq);
		creerJoueur("Kane", "Harry", "Attaquant", 31, 18, eq);

		List<Joueur> joueurs = joueurRepository.findByOrderByNomAsc();
		for (Joueur j : joueurs) {
			System.out.println(j);
		}
	}

	@Test
	public void testTrierJoueursNomsPoste() {
		Equipe eq = creerEquipe("PSG", "Paris", "France");
		creerJoueur("Mbappe", "Kylian", "Attaquant", 23, 7, eq);
		creerJoueur("Dembele", "Ousmane", "Attaquant", 23, 11, eq);
		creerJoueur("Vitinha", "Vitor", "Milieu", 25, 2, eq);

		List<Joueur> joueurs = joueurRepository.trierJoueursNomsPoste();
		for (Joueur j : joueurs) {
			System.out.println(j);
		}
	}

	/* ================================================================== */
	/* SECTION 3 - Nouvelle entite Auteur (Ateliers 01 + 02)                */
	/* ================================================================== */

	@Test
	public void testCreateAuteur() {
		Auteur a = creerAuteur("Dupont", "Jean", "Française");
		System.out.println("Auteur cree : " + a);
	}

	@Test
	public void testFindAuteur() {
		Auteur cree = creerAuteur("Martin", "Claire", "Belge");
		Auteur a = auteurRepository.findById(cree.getId()).get();
		System.out.println("Auteur trouve : " + a);
	}

	@Test
	public void testUpdateAuteur() {
		Auteur a = creerAuteur("Moreau", "Luc", "Canadienne");
		a.setNationalite("Suisse");
		auteurRepository.save(a);
		System.out.println("Auteur modifie : " + a);
	}

	@Test
	public void testDeleteAuteur() {
		Auteur a = creerAuteur("Petit", "Paul", "Française");
		auteurRepository.deleteById(a.getId());
		System.out.println("Auteur supprime, id = " + a.getId());
	}

	@Test
	public void testListerTousAuteurs() {
		creerAuteur("Bernard", "Alice", "Française");
		creerAuteur("Leroy", "Marc", "Belge");
		List<Auteur> auteurs = auteurRepository.findAll();
		for (Auteur a : auteurs) {
			System.out.println(a);
		}
	}

	@Test
	public void testFindByAuteur() {
		Equipe eq = creerEquipe("Ajax", "Amsterdam", "Pays-Bas");
		Auteur auteur = creerAuteur("Jansen", "Jan", "Neerlandaise");
		creerJoueur("De Jong", "Frenkie", "Milieu", 28, 6, eq, auteur);

		Auteur recherche = new Auteur();
		recherche.setId(auteur.getId());
		List<Joueur> joueurs = joueurRepository.findByAuteur(recherche);
		for (Joueur j : joueurs) {
			System.out.println("Joueur de l'auteur : " + j);
		}
	}

	@Test
	public void testFindByAuteurId() {
		Equipe eq = creerEquipe("Porto", "Porto", "Portugal");
		Auteur auteur = creerAuteur("Silva", "Rui", "Portugaise");
		creerJoueur("Dias", "Ruben", "Defenseur", 27, 3, eq, auteur);

		List<Joueur> joueurs = joueurRepository.findByAuteurId(auteur.getId());
		for (Joueur j : joueurs) {
			System.out.println("Joueur de l'auteur (id) : " + j);
		}
	}

}

