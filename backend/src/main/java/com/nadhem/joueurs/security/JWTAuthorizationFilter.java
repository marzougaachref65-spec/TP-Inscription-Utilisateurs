package com.nadhem.joueurs.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Atelier 14 : verifier le JWT present dans l'en-tete Authorization.
public class JWTAuthorizationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String jwt = request.getHeader("Authorization");
		if (jwt == null || !jwt.startsWith(SecParams.PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecParams.SECRET)).build();
			DecodedJWT decodedJWT = verifier.verify(jwt.substring(SecParams.PREFIX.length()));

			String username = decodedJWT.getSubject();
			List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

			Collection<GrantedAuthority> authorities = new ArrayList<>();
			if (roles != null) {
				for (String role : roles) {
					authorities.add(new SimpleGrantedAuthority(role));
				}
			}

			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(username, null, authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(request, response);
		} catch (JWTVerificationException ex) {
			SecurityContextHolder.clearContext();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("{\"message\":\"Token JWT invalide ou expire\"}");
		}
	}
}
