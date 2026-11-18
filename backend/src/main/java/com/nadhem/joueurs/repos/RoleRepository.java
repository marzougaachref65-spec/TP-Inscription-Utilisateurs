package com.nadhem.joueurs.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nadhem.joueurs.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findByRole(String role);
}
