package com.nadhem.joueurs.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nadhem.joueurs.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);

	// TP 2025 : inscription — verification de l'unicite de l'email.
	Optional<User> findByEmail(String email);
}
