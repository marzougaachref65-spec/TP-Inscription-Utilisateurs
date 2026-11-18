package com.nadhem.joueurs.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nadhem.joueurs.entities.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	Optional<VerificationToken> findByToken(String token);

	boolean existsByToken(String token);
}
