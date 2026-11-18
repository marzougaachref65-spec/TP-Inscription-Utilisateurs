package com.nadhem.joueurs.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Atelier 13 : POST /login -> generation du token JWT.
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	private final RequestMatcher loginMatcher = new OrRequestMatcher(
			new AntPathRequestMatcher(SecParams.LOGIN_URL, "POST"),
			new AntPathRequestMatcher(SecParams.LOGIN_URL_WITH_CONTEXT, "POST"));

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
		setFilterProcessesUrl(SecParams.LOGIN_URL);
	}

	@Override
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		return loginMatcher.matches(request);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		Map<String, String> credentials;
		try {
			credentials = objectMapper.readValue(request.getInputStream(), new TypeReference<Map<String, String>>() {});
		} catch (IOException e) {
			throw new org.springframework.security.authentication.BadCredentialsException(
					"Corps de requete login invalide", e);
		}

		String username = credentials == null ? null : credentials.get("username");
		String password = credentials == null ? null : credentials.get("password");
		if (username == null || password == null) {
			throw new org.springframework.security.authentication.BadCredentialsException("Identifiants manquants");
		}

		return authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password));
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		// TP 2025 : un compte non active ne doit pas pouvoir se connecter.
		if (failed instanceof DisabledException) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("UTF-8");
			Map<String, Object> data = new HashMap<>();
			data.put("errorCause", "disabled");
			data.put("message", "Utilisateur désactivé, veuillez valider votre email ou contacter votre administrateur.");
			objectMapper.writeValue(response.getWriter(), data);
			return;
		}
		super.unsuccessfulAuthentication(request, response, failed);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain, Authentication authResult) throws IOException, ServletException {

		org.springframework.security.core.userdetails.User springUser =
				(org.springframework.security.core.userdetails.User) authResult.getPrincipal();

		List<String> roles = new ArrayList<>();
		for (GrantedAuthority authority : springUser.getAuthorities()) {
			roles.add(authority.getAuthority());
		}

		String jwt = JWT.create()
				.withSubject(springUser.getUsername())
				.withArrayClaim("roles", roles.toArray(new String[0]))
				.withExpiresAt(new Date(System.currentTimeMillis() + SecParams.EXP_TIME))
				.sign(Algorithm.HMAC256(SecParams.SECRET));

		// Atelier 16bis : Angular lit l'en-tete Authorization.
		response.addHeader("Authorization", jwt);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(),
				Map.of("token", jwt, "username", springUser.getUsername(), "roles", roles));
	}
}
