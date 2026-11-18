package com.nadhem.joueurs.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nadhem.joueurs.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);
}
