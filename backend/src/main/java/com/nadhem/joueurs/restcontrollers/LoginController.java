package com.nadhem.joueurs.restcontrollers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nadhem.joueurs.security.SecParams;

/**
 * Atelier 13 : endpoint de generation du JWT.
 * Complete le JWTAuthenticationFilter : selon la version de Spring Security,
 * le filtre peut ou non intercepter "/login" ; ce controleur garantit le resultat.
 */
@RestController
public class LoginController {

	private final AuthenticationConfiguration authenticationConfiguration;

	public LoginController(AuthenticationConfiguration authenticationConfiguration) {
		this.authenticationConfiguration = authenticationConfiguration;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) throws Exception {
		String username = credentials.get("username");
		String password = credentials.get("password");
		if (username == null || password == null) {
			throw new BadCredentialsException("Identifiants manquants");
		}

		Authentication authentication = authenticationConfiguration.getAuthenticationManager()
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		List<String> roles = new ArrayList<>();
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			roles.add(authority.getAuthority());
		}

		String jwt = JWT.create()
				.withSubject(username)
				.withArrayClaim("roles", roles.toArray(new String[0]))
				.withExpiresAt(new Date(System.currentTimeMillis() + SecParams.EXP_TIME))
				.sign(Algorithm.HMAC256(SecParams.SECRET));

		return ResponseEntity.ok()
				.header("Authorization", jwt)
				.body(Map.of("token", jwt, "username", username, "roles", roles));
	}
}
