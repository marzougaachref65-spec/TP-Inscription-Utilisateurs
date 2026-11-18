package com.nadhem.joueurs.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.nadhem.joueurs.entities.Auteur;
import com.nadhem.joueurs.entities.Equipe;
import com.nadhem.joueurs.entities.Joueur;

// Atelier 03 : exposition automatique des web services CRUD via Spring Data REST
@RepositoryRestResource(path = "restJoueurs")
public interface JoueurRepository extends JpaRepository<Joueur, Long> {

	// Atelier 02 : recherche par un attribut non cle
	List<Joueur> findByNom(String nom);

	List<Joueur> findByNomContains(String nom);

	// Atelier 02 : requete JPQL avec plusieurs criteres (@Param)
	@Query("select j from Joueur j where j.nom like %:nom% and j.poste = :poste")
	List<Joueur> findByNomPoste(@Param("nom") String nom, @Param("poste") String poste);

	// Atelier 02 : requete JPQL en passant une entite en parametre
	@Query("select j from Joueur j where j.equipe = ?1")
	List<Joueur> findByEquipe(Equipe equipe);

	// Atelier 02 : recherche selon l'id de l'equipe
	List<Joueur> findByEquipeId(Long id);

	// Nombre de joueurs rattaches a une equipe (utilise avant de supprimer une equipe)
	long countByEquipeId(Long idEquipe);

	// Atelier 02 : recherche selon l'auteur du joueur
	@Query("select j from Joueur j where j.auteur = ?1")
	List<Joueur> findByAuteur(Auteur auteur);

	List<Joueur> findByAuteurId(Long id);

	long countByAuteurId(Long idAuteur);

	// Atelier 02 : tri par nom
	List<Joueur> findByOrderByNomAsc();

	// Atelier 02 : tri par nom puis par poste
	@Query("select j from Joueur j order by j.nom ASC, j.poste DESC")
	List<Joueur> trierJoueursNomsPoste();
}
