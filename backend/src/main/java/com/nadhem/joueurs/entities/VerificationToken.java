package com.nadhem.joueurs.entities;

import java.util.Calendar;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TP 2025 : code secret a 6 chiffres envoye par email.
 * Le code expire 15 minutes apres sa generation.
 */
@Data
@Entity
@Table(name = "verification_token")
@NoArgsConstructor
public class VerificationToken {

	public static final int EXPIRATION_TIME_MINUTES = 15;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(nullable = false)
	private Date expirationTime;

	// EAGER : le controleur renvoie le User valide en JSON ; un proxy LAZY
	// provoquerait une erreur de serialization ByteBuddyInterceptor.
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public VerificationToken(String token, User user) {
		this.token = token;
		this.user = user;
		this.expirationTime = calculateExpirationTime();
	}

	public VerificationToken(String token) {
		this.token = token;
		this.expirationTime = calculateExpirationTime();
	}

	/** Conserve pour correspondre a l'enonce du TP. */
	public Date getTokenExpirationTime() {
		return calculateExpirationTime();
	}

	public boolean isExpired() {
		return expirationTime == null || expirationTime.getTime() <= System.currentTimeMillis();
	}

	public Date calculateExpirationTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(Calendar.MINUTE, EXPIRATION_TIME_MINUTES);
		return new Date(calendar.getTime().getTime());
	}
}
