package com.nadhem.joueurs.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TP 2025 : donnees recues par POST /register.
 * La validation est aussi cote backend : un appel Postman direct ne peut pas
 * contourner les regles du formulaire Angular.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

	@NotBlank(message = "Le nom d'utilisateur est obligatoire")
	@Size(max = 100, message = "Le nom d'utilisateur ne doit pas depasser 100 caracteres")
	private String username;

	@NotBlank(message = "L'email est obligatoire")
	@Email(message = "L'email n'est pas valide")
	@Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres")
	private String email;

	@NotBlank(message = "Le mot de passe est obligatoire")
	@Size(min = 6, max = 100, message = "Le mot de passe doit contenir au moins 6 caracteres")
	private String password;
}
