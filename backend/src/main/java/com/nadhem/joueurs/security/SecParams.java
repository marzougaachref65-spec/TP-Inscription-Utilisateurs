package com.nadhem.joueurs.security;

// Ateliers 13-15 : constantes partagees entre generation et verification du JWT.
public interface SecParams {

	long EXP_TIME = 10L * 24 * 60 * 60 * 1000; // 10 jours
	String SECRET = "nadhemb@yahoo.com";
	String PREFIX = "Bearer ";
	String LOGIN_URL = "/login";
	// Certaines versions de Spring Security matchent l'URI complete (avec le context-path).
	String LOGIN_URL_WITH_CONTEXT = "/joueurs/login";
}
