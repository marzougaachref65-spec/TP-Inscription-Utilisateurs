package com.nadhem.joueurs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

import com.nadhem.joueurs.entities.Auteur;
import com.nadhem.joueurs.entities.Equipe;
import com.nadhem.joueurs.entities.Joueur;

@SpringBootApplication
public class JoueursEquipesApplication implements CommandLineRunner {

	@Autowired
	private RepositoryRestConfiguration repositoryRestConfiguration;

	public static void main(String[] args) {
		SpringApplication.run(JoueursEquipesApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Atelier 03 : retourner l'ID dans les reponses de Spring Data REST
		repositoryRestConfiguration.exposeIdsFor(Joueur.class, Equipe.class, Auteur.class);
	}

}
