package com.nadhem.joueurs;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.nadhem.joueurs.entities.Auteur;
import com.nadhem.joueurs.entities.Equipe;
import com.nadhem.joueurs.entities.Joueur;
import com.nadhem.joueurs.repos.AuteurRepository;
import com.nadhem.joueurs.repos.EquipeRepository;
import com.nadhem.joueurs.repos.JoueurRepository;

/**
 * Jeu de demonstration : cree des donnees au premier demarrage uniquement.
 * Les TPs restent utilisables avec une base vide (les tables sont creees par Hibernate).
 */
@Component
@Order(2)
public class DatabaseSeeder implements CommandLineRunner {

	private final EquipeRepository equipeRepository;
	private final AuteurRepository auteurRepository;
	private final JoueurRepository joueurRepository;

	public DatabaseSeeder(EquipeRepository equipeRepository, AuteurRepository auteurRepository,
			JoueurRepository joueurRepository) {
		this.equipeRepository = equipeRepository;
		this.auteurRepository = auteurRepository;
		this.joueurRepository = joueurRepository;
	}

	@Override
	public void run(String... args) {
		if (joueurRepository.count() > 0) {
			return;
		}

		Equipe est = equipeRepository.save(equipe("Espérance de Tunis", "Tunis", "Tunisie"));
		Equipe juve = equipeRepository.save(equipe("Juventus", "Turin", "Italie"));
		Equipe madrid = equipeRepository.save(equipe("Real Madrid", "Madrid", "Espagne"));
		Equipe city = equipeRepository.save(equipe("Manchester City", "Manchester", "Angleterre"));
		equipeRepository.save(equipe("Al Ahly", "Le Caire", "Égypte"));

		Auteur karim = auteurRepository.save(auteur("Ben Ali", "Karim", "Tunisienne"));
		Auteur marco = auteurRepository.save(auteur("Rossi", "Marco", "Italienne"));
		Auteur lucia = auteurRepository.save(auteur("Garcia", "Lucia", "Espagnole"));

		joueurRepository.save(joueur("Ben Cherifia", "Moez", 34, "Gardien", 1, est, karim));
		joueurRepository.save(joueur("Ben Romdhane", "Mohamed", 26, "Milieu", 8, est, karim));
		joueurRepository.save(joueur("Khenissi", "Naim", 33, "Attaquant", 9, est, karim));
		joueurRepository.save(joueur("Vlahovic", "Dusan", 26, "Attaquant", 9, juve, marco));
		joueurRepository.save(joueur("Locatelli", "Manuel", 28, "Milieu", 5, juve, marco));
		joueurRepository.save(joueur("Bremer", "Gleison", 28, "Défenseur", 3, juve, marco));
		joueurRepository.save(joueur("Vinicius", "Junior", 26, "Attaquant", 7, madrid, lucia));
		joueurRepository.save(joueur("Bellingham", "Jude", 23, "Milieu", 5, madrid, lucia));
		joueurRepository.save(joueur("Courtois", "Thibaut", 34, "Gardien", 1, madrid, lucia));
		joueurRepository.save(joueur("Haaland", "Erling", 25, "Attaquant", 9, city, marco));
		joueurRepository.save(joueur("De Bruyne", "Kevin", 34, "Milieu", 17, city, marco));
	}

	private Equipe equipe(String nom, String ville, String pays) {
		Equipe e = new Equipe();
		e.setNom(nom);
		e.setVille(ville);
		e.setPays(pays);
		e.setDateCreation(LocalDate.now().minusYears(5));
		return e;
	}

	private Auteur auteur(String nom, String prenom, String nationalite) {
		Auteur a = new Auteur();
		a.setNom(nom);
		a.setPrenom(prenom);
		a.setNationalite(nationalite);
		return a;
	}

	private Joueur joueur(String nom, String prenom, int age, String poste, int numero,
			Equipe equipe, Auteur auteur) {
		Joueur j = new Joueur();
		j.setNom(nom);
		j.setPrenom(prenom);
		j.setAge(age);
		j.setPoste(poste);
		j.setNumeroMaillot(numero);
		j.setEquipe(equipe);
		j.setAuteur(auteur);
		return j;
	}
}
