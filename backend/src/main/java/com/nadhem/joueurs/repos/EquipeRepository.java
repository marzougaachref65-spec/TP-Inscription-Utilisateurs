package com.nadhem.joueurs.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nadhem.joueurs.entities.Equipe;

// Atelier 03 : exposition automatique des web services CRUD via Spring Data REST
@RepositoryRestResource(path = "restEquipes")
public interface EquipeRepository extends JpaRepository<Equipe, Long> {

	// Atelier 02 : recherche par attribut non cle
	List<Equipe> findByNomContains(String nom);
}
