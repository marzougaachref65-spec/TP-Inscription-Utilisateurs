package com.nadhem.joueurs.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entite Auteur : personne qui suit / presente les joueurs (agent, journaliste...).
 * <p>
 * Un auteur peut etre associe a plusieurs joueurs (OneToMany), et chaque joueur
 * a au plus un auteur (ManyToOne du cote de {@link Joueur}).
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Auteur {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Le nom de l auteur est obligatoire.")
	private String nom;

	@NotBlank(message = "Le prenom de l auteur est obligatoire.")
	private String prenom;

	@NotBlank(message = "La nationalite de l auteur est obligatoire.")
	private String nationalite;

	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "auteur")
	private List<Joueur> joueurs;
}
