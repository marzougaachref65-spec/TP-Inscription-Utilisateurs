package com.nadhem.joueurs.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import com.nadhem.joueurs.entities.Role;
import com.nadhem.joueurs.entities.User;
import com.nadhem.joueurs.entities.VerificationToken;
import com.nadhem.joueurs.exceptions.EmailAlreadyExistsException;
import com.nadhem.joueurs.exceptions.ExpiredTokenException;
import com.nadhem.joueurs.exceptions.InvalidTokenException;
import com.nadhem.joueurs.exceptions.UsernameAlreadyExistsException;
import com.nadhem.joueurs.register.RegistrationRequest;
import com.nadhem.joueurs.repos.RoleRepository;
import com.nadhem.joueurs.repos.UserRepository;
import com.nadhem.joueurs.repos.VerificationTokenRepository;
import com.nadhem.joueurs.util.EmailSender;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private static final SecureRandom RANDOM = new SecureRandom();

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	VerificationTokenRepository verificationTokenRepo;

	@Autowired
	EmailSender emailSender;

	@Override
	public User saveUser(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public Role addRole(Role role) {
		return roleRepository.save(role);
	}

	@Override
	public User addRoleToUser(String username, String rolename) {
		User user = userRepository.findByUsername(username);
		Role role = roleRepository.findByRole(rolename);
		if (user == null || role == null) {
			throw new IllegalArgumentException("Utilisateur ou role introuvable : " + username + " / " + rolename);
		}
		if (!user.getRoles().contains(role)) {
			user.getRoles().add(role);
		}
		return userRepository.save(user);
	}

	@Override
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}

	/* ================================================================== */
	/* TP 2025 : inscription, code a 6 chiffres et validation par email     */
	/* ================================================================== */

	@Override
	public User registerUser(RegistrationRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new EmailAlreadyExistsException("email déjà existant !");
		}
		if (userRepository.findByUsername(request.getUsername()) != null) {
			throw new UsernameAlreadyExistsException("nom d'utilisateur déjà existant !");
		}

		User newUser = new User();
		newUser.setUsername(request.getUsername());
		newUser.setEmail(request.getEmail());
		newUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
		// Le compte reste desactive tant que le code n'a pas ete valide.
		newUser.setEnabled(false);

		Role userRole = roleRepository.findByRole("USER");
		if (userRole == null) {
			userRole = roleRepository.save(new Role(null, "USER"));
		}
		newUser.setRoles(new ArrayList<>(List.of(userRole)));

		newUser = userRepository.save(newUser);

		// Generer un code secret unique a 6 chiffres et le stocker 15 minutes.
		String code = generateUniqueCode();
		verificationTokenRepo.save(new VerificationToken(code, newUser));

		// Envoyer par email pour valider l'email de l'utilisateur.
		sendEmailUser(newUser, code);

		return newUser;
	}

	@Override
	public User validateToken(String code) {
		VerificationToken token = verificationTokenRepo.findByToken(code)
				.orElseThrow(() -> new InvalidTokenException("Code invalide"));

		if (token.isExpired()) {
			verificationTokenRepo.delete(token);
			throw new ExpiredTokenException("Code expiré");
		}

		User user = token.getUser();
		user.setEnabled(true);
		userRepository.save(user);
		// Le code n'est plus utilisable apres validation.
		verificationTokenRepo.delete(token);
		return user;
	}

	/** Genere un code a 6 chiffres (100000 - 999999). */
	public String generateCode() {
		return String.valueOf(100000 + RANDOM.nextInt(900000));
	}

	public void sendEmailUser(User user, String code) {
		String safeUsername = HtmlUtils.htmlEscape(user.getUsername());
		String emailBody = "Bonjour <h1>" + safeUsername + "</h1>"
				+ "<p>Votre code de validation est :</p><h1>" + code + "</h1>"
				+ "<p>Ce code expire dans " + VerificationToken.EXPIRATION_TIME_MINUTES + " minutes.</p>";
		emailSender.sendEmail(user.getEmail(), emailBody);
	}

	private String generateUniqueCode() {
		for (int i = 0; i < 20; i++) {
			String code = generateCode();
			if (!verificationTokenRepo.existsByToken(code)) {
				return code;
			}
		}
		throw new IllegalStateException("Impossible de generer un code de verification unique");
	}
}
