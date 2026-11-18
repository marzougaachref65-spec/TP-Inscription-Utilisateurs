package com.nadhem.joueurs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.nadhem.joueurs.entities.Role;
import com.nadhem.joueurs.entities.User;
import com.nadhem.joueurs.entities.VerificationToken;
import com.nadhem.joueurs.exceptions.EmailAlreadyExistsException;
import com.nadhem.joueurs.exceptions.ExpiredTokenException;
import com.nadhem.joueurs.exceptions.InvalidTokenException;
import com.nadhem.joueurs.register.RegistrationRequest;
import com.nadhem.joueurs.repos.RoleRepository;
import com.nadhem.joueurs.repos.UserRepository;
import com.nadhem.joueurs.repos.VerificationTokenRepository;
import com.nadhem.joueurs.service.UserServiceImpl;
import com.nadhem.joueurs.util.EmailSender;

/** TP 2025 : tests unitaires du flux inscription -> code -> validation. */
@ExtendWith(MockitoExtension.class)
class RegistrationServiceTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Mock
	private VerificationTokenRepository verificationTokenRepo;

	@Mock
	private EmailSender emailSender;

	@InjectMocks
	private UserServiceImpl service;

	@Test
	void registerUser_creeUnCompteDesactiveAvecLeRoleUSEREtEnvoieLeCode() {
		when(userRepository.findByEmail("nouveau@joueurs.local")).thenReturn(Optional.empty());
		when(userRepository.findByUsername("nouveau")).thenReturn(null);
		when(bCryptPasswordEncoder.encode("secret123")).thenReturn("MOT_DE_PASSE_CRYPTE");
		when(roleRepository.findByRole("USER")).thenReturn(null);
		when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(verificationTokenRepo.existsByToken(anyString())).thenReturn(false);

		User result = service.registerUser(new RegistrationRequest("nouveau", "nouveau@joueurs.local", "secret123"));

		assertFalse(result.getEnabled());
		assertEquals("MOT_DE_PASSE_CRYPTE", result.getPassword());
		assertEquals(1, result.getRoles().size());
		assertEquals("USER", result.getRoles().get(0).getRole());

		ArgumentCaptor<VerificationToken> tokenCaptor = ArgumentCaptor.forClass(VerificationToken.class);
		verify(verificationTokenRepo).save(tokenCaptor.capture());
		assertTrue(tokenCaptor.getValue().getToken().matches("\\d{6}"));
		assertTrue(tokenCaptor.getValue().getExpirationTime().after(new Date()));

		verify(emailSender).sendEmail(eq("nouveau@joueurs.local"), contains(tokenCaptor.getValue().getToken()));
	}

	@Test
	void registerUser_refuseUnEmailDejaUtilise() {
		when(userRepository.findByEmail("deja@joueurs.local")).thenReturn(Optional.of(new User()));

		assertThrows(EmailAlreadyExistsException.class,
				() -> service.registerUser(new RegistrationRequest("autre", "deja@joueurs.local", "secret123")));
		verify(userRepository, never()).save(any(User.class));
		verify(emailSender, never()).sendEmail(anyString(), anyString());
	}

	@Test
	void validateToken_activeLeCompteEtSupprimeLeCode() {
		User user = new User();
		user.setEnabled(false);
		VerificationToken token = new VerificationToken("123456", user);
		when(verificationTokenRepo.findByToken("123456")).thenReturn(Optional.of(token));

		User result = service.validateToken("123456");

		assertTrue(result.getEnabled());
		verify(userRepository).save(user);
		verify(verificationTokenRepo).delete(token);
	}

	@Test
	void validateToken_refuseUnCodeInconnu() {
		when(verificationTokenRepo.findByToken("000000")).thenReturn(Optional.empty());

		assertThrows(InvalidTokenException.class, () -> service.validateToken("000000"));
	}

	@Test
	void validateToken_refuseUnCodeExpireEtLeSupprime() {
		User user = new User();
		user.setEnabled(false);
		VerificationToken token = new VerificationToken("654321", user);
		token.setExpirationTime(new Date(0));
		when(verificationTokenRepo.findByToken("654321")).thenReturn(Optional.of(token));

		assertThrows(ExpiredTokenException.class, () -> service.validateToken("654321"));
		verify(verificationTokenRepo).delete(token);
		verify(userRepository, never()).save(any(User.class));
	}
}
