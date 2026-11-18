package com.nadhem.joueurs.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nadhem.joueurs.entities.User;
import com.nadhem.joueurs.service.UserService;

// Atelier 13 : charger l'utilisateur et ses roles depuis la base.
@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userService.findUserByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Utilisateur introuvable : " + username);
		}

		List<GrantedAuthority> authorities = new ArrayList<>();
		if (user.getRoles() != null) {
			user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRole())));
		}

		return org.springframework.security.core.userdetails.User
				.withUsername(user.getUsername())
				.password(user.getPassword())
				.authorities(authorities)
				.disabled(!Boolean.TRUE.equals(user.getEnabled()))
				.build();
	}
}
