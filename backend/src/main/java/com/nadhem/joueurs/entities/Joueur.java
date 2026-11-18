package com.nadhem.joueurs.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entite Joueur representant un joueur de sport.
 * <p>
 * Chaque joueur appartient a une seule equipe (relation ManyToOne).
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Joueur {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Le nom du joueur est obligatoire.")
	private String nom;

	@NotBlank(message = "Le prenom du joueur est obligatoire.")
	private String prenom;

	@NotNull(message = "L age du joueur est obligatoire.")
	@Min(value = 10, message = "L age doit etre superieur ou egal a 10.")
	@Max(value = 100, message = "L age doit etre inferieur ou egal a 100.")
	private Integer age;

	@NotBlank(message = "Le poste du joueur est obligatoire.")
	private String poste;

	@NotNull(message = "Le numero de maillot est obligatoire.")
	@Min(value = 1, message = "Le numero de maillot doit etre positif.")
	private Integer numeroMaillot;

	// Une equipe possede plusieurs joueurs, mais un joueur n appartient qu a une seule equipe.
	// @ToString.Exclude et @EqualsAndHashCode.Exclude evitent les problemes de
	// recursion lors des toString()/equals().
	// @JsonIgnore evite la boucle infinie lors de la serialisation JSON (Equipe ->
	// List<Joueur> -> Equipe ...).
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "equipe_id")
	@NotNull(message = "Le joueur doit appartenir a une equipe.")
	private Equipe equipe;

	// Nouvelle entite : chaque joueur peut avoir un auteur (agent / journaliste).
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "auteur_id")
	private Auteur auteur;

}
