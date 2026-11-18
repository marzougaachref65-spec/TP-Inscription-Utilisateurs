package com.nadhem.joueurs.restcontrollers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nadhem.joueurs.entities.User;
import com.nadhem.joueurs.register.RegistrationRequest;
import com.nadhem.joueurs.service.UserService;

import jakarta.validation.Valid;

/**
 * TP 2025 : inscription et validation de l'email.
 * Les alias /api/users/... sont ajoutes pour rester coherents avec
 * UserRestController (Atelier 14).
 */
@RestController
public class RegistrationController {

	private final UserService userService;

	public RegistrationController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping({ "/register", "/api/users/register" })
	public User register(@Valid @RequestBody RegistrationRequest request) {
		return userService.registerUser(request);
	}

	@GetMapping({ "/verifyEmail/{code}", "/api/users/verifyEmail/{code}" })
	public User verifyEmail(@PathVariable("code") String code) {
		return userService.validateToken(code);
	}
}
