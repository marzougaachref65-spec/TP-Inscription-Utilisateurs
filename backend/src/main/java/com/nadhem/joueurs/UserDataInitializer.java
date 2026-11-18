package com.nadhem.joueurs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nadhem.joueurs.entities.Role;
import com.nadhem.joueurs.entities.User;
import com.nadhem.joueurs.repos.RoleRepository;
import com.nadhem.joueurs.repos.UserRepository;

import org.springframework.boot.CommandLineRunner;

/**
 * Atelier 13 : initialise les roles et les utilisateurs au premier demarrage.
 * Adapte de la methode init_users() de l'enonce (admin / joueur au lieu de nadhem / yassine).
 */
@Component
@Order(1)
public class UserDataInitializer implements CommandLineRunner {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public UserDataInitializer(RoleRepository roleRepository, UserRepository userRepository,
			BCryptPasswordEncoder passwordEncoder) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void run(String... args) {
		Role adminRole = getOrCreateRole("ADMIN");
		Role userRole = getOrCreateRole("USER");

		createUser("admin", "123", "admin@joueurs.local", List.of(adminRole, userRole));
		createUser("joueur", "123", "joueur@joueurs.local", List.of(userRole));
	}

	private Role getOrCreateRole(String libelle) {
		Role role = roleRepository.findByRole(libelle);
		if (role == null) {
			role = roleRepository.save(new Role(null, libelle));
		}
		return role;
	}

	private void createUser(String username, String password, String email, List<Role> roles) {
		User existing = userRepository.findByUsername(username);
		if (existing != null) {
			// Les comptes deja crees avant l'ajout du champ email restent valides.
			if (existing.getEmail() == null) {
				existing.setEmail(email);
				userRepository.save(existing);
			}
			return;
		}
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user.setEnabled(true);
		user.setRoles(new ArrayList<>(roles));
		userRepository.save(user);
	}
}
