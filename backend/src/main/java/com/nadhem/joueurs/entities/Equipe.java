package com.nadhem.joueurs.entities;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entite Equipe representant une equipe sportive.
 * <p>
 * Une equipe possede plusieurs joueurs (relation OneToMany) via la liaison
 * {@code joueurs} qui est mappee par le champ {@code equipe} de l entite Joueur.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Equipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Le nom de l equipe est obligatoire.")
	private String nom;

	@NotBlank(message = "La ville de l equipe est obligatoire.")
	private String ville;

	@NotBlank(message = "Le pays de l equipe est obligatoire.")
	private String pays;

	@NotNull(message = "La date de creation de l equipe est obligatoire.")
	private LocalDate dateCreation;

	// Une equipe contient plusieurs joueurs. MappedBy indique que la liaison est
	// definie du cote de l entite Joueur (champ 'equipe').
	// @JsonIgnore evite la boucle infinie lors de la serialisation JSON
	// (Equipe -> List<Joueur> -> Equipe ...).
	// @ToString.Exclude et @EqualsAndHashCode.Exclude evitent les problemes de
	// recursion lors des toString()/equals().
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "equipe")
	private List<Joueur> joueurs;

}
