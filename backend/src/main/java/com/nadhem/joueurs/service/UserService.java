package com.nadhem.joueurs.service;

import java.util.List;

import com.nadhem.joueurs.entities.Role;
import com.nadhem.joueurs.entities.User;
import com.nadhem.joueurs.register.RegistrationRequest;

public interface UserService {

	User saveUser(User user);

	User findUserByUsername(String username);

	Role addRole(Role role);

	User addRoleToUser(String username, String rolename);

	List<User> findAllUsers();

	// TP 2025 : inscription + validation de l'email
	User registerUser(RegistrationRequest request);

	User validateToken(String code);
}
