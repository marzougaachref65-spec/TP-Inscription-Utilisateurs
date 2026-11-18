package com.nadhem.joueurs.restcontrollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nadhem.joueurs.entities.User;
import com.nadhem.joueurs.service.UserService;

// Atelier 14 : API /all (adaptee en /api/users/all) reservee aux ADMIN.
@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserRestController {

	@Autowired
	UserService userService;

	@GetMapping("/all")
	public List<User> getAllUsers() {
		return userService.findAllUsers();
	}
}
