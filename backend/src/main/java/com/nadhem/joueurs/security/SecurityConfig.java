package com.nadhem.joueurs.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

// Ateliers 14 + 15 : securisation des API REST par JWT et par roles.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
		this.authenticationConfiguration = authenticationConfiguration;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

		http
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(request -> {
				CorsConfiguration config = new CorsConfiguration();
				config.setAllowedOrigins(List.of("http://localhost:4200"));
				config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
				config.setAllowedHeaders(List.of("*"));
				config.setExposedHeaders(List.of("Authorization"));
				config.setAllowCredentials(true);
				config.setMaxAge(3600L);
				return config;
			}))
			.authorizeHttpRequests(requests -> requests
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.requestMatchers(SecParams.LOGIN_URL, SecParams.LOGIN_URL_WITH_CONTEXT).permitAll()

				// TP 2025 : inscription et validation de l'email sont publiques.
				.requestMatchers(HttpMethod.POST,
					"/register", "/joueurs/register", "/api/users/register").permitAll()
				.requestMatchers(HttpMethod.GET,
					"/verifyEmail/**", "/joueurs/verifyEmail/**", "/api/users/verifyEmail/**").permitAll()

				// Lecture : ADMIN et USER
				.requestMatchers(HttpMethod.GET,
					"/api/joueurs/**", "/api/equipes/**", "/api/auteurs/**",
					"/restJoueurs/**", "/restEquipes/**", "/restAuteurs/**")
					.hasAnyAuthority("ADMIN", "USER")

				// Ecriture via les controleurs REST classiques : ADMIN uniquement
				.requestMatchers(HttpMethod.POST, "/api/joueurs", "/api/equipes", "/api/auteurs")
					.hasAuthority("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/api/joueurs", "/api/equipes", "/api/auteurs")
					.hasAuthority("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/joueurs/**", "/api/equipes/**", "/api/auteurs/**")
					.hasAuthority("ADMIN")

				// Ecriture via Spring Data REST : ADMIN uniquement
				.requestMatchers(HttpMethod.POST, "/restJoueurs", "/restEquipes", "/restAuteurs")
					.hasAuthority("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/restJoueurs/**", "/restEquipes/**", "/restAuteurs/**")
					.hasAuthority("ADMIN")
				.requestMatchers(HttpMethod.PATCH, "/restJoueurs/**", "/restEquipes/**", "/restAuteurs/**")
					.hasAuthority("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/restJoueurs/**", "/restEquipes/**", "/restAuteurs/**")
					.hasAuthority("ADMIN")

				// Atelier 14 : API protegee reservee a ADMIN
				.requestMatchers(HttpMethod.GET, "/api/users/all").hasAuthority("ADMIN")

				.anyRequest().authenticated()
			)
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(401);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write("{\"message\":\"Authentification requise\"}");
				})
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					response.setStatus(403);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write("{\"message\":\"Acces refuse : droits insuffisants\"}");
				})
			)
			.addFilterBefore(new JWTAuthenticationFilter(authenticationManager),
					UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new JWTAuthorizationFilter(),
					UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
