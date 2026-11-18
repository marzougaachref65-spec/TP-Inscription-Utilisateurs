package com.nadhem.joueurs.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nadhem.joueurs.entities.Auteur;

// Atelier 03 : exposition automatique des web services CRUD via Spring Data REST
@RepositoryRestResource(path = "restAuteurs")
public interface AuteurRepository extends JpaRepository<Auteur, Long> {

	// Atelier 02 : recherche par attribut non cle
	List<Auteur> findByNom(String nom);

	List<Auteur> findByNomContains(String nom);

	List<Auteur> findByOrderByNomAsc();
}
