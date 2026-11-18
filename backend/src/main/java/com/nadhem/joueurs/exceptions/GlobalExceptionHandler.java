package com.nadhem.joueurs.exceptions;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

// Gestionnaire d'exceptions commun a tous les controleurs REST.
@RestControllerAdvice
public class GlobalExceptionHandler {

	// Renvoie un code HTTP 409 (Conflit) au lieu d'une erreur 500 lorsque l'on
	// tente de supprimer une equipe qui contient encore des joueurs.
	@ExceptionHandler(EquipeNonVideException.class)
	public ResponseEntity<Map<String, String>> handleEquipeNonVide(EquipeNonVideException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler(AuteurNonVideException.class)
	public ResponseEntity<Map<String, String>> handleAuteurNonVide(AuteurNonVideException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleIntrouvable(jakarta.persistence.EntityNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(e -> e.getField() + " : " + e.getDefaultMessage())
				.distinct()
				.collect(Collectors.joining("; "));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
	}

	/* ================================================================== */
	/* TP 2025 : inscription / verification de l'email                     */
	/* ================================================================== */

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ErrorDetails> handleEmailAlreadyExists(EmailAlreadyExistsException ex, WebRequest request) {
		return error(HttpStatus.BAD_REQUEST, "USER_EMAIL_ALREADY_EXISTS", ex.getMessage(), request);
	}

	@ExceptionHandler(UsernameAlreadyExistsException.class)
	public ResponseEntity<ErrorDetails> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex, WebRequest request) {
		return error(HttpStatus.BAD_REQUEST, "USER_USERNAME_ALREADY_EXISTS", ex.getMessage(), request);
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ErrorDetails> handleInvalidToken(InvalidTokenException ex, WebRequest request) {
		return error(HttpStatus.NOT_FOUND, "INVALID_TOKEN", ex.getMessage(), request);
	}

	@ExceptionHandler(ExpiredTokenException.class)
	public ResponseEntity<ErrorDetails> handleExpiredToken(ExpiredTokenException ex, WebRequest request) {
		return error(HttpStatus.BAD_REQUEST, "EXPIRED_TOKEN", ex.getMessage(), request);
	}

	@ExceptionHandler(EmailSendingException.class)
	public ResponseEntity<ErrorDetails> handleEmailSending(EmailSendingException ex, WebRequest request) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SENDING_FAILED", ex.getMessage(), request);
	}

	/** Compte non active : le frontend affiche un message dedie sur le login. */
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<Map<String, String>> handleDisabled(DisabledException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
				"errorCause", "disabled",
				"message", "Utilisateur désactivé, veuillez valider votre email ou contacter votre administrateur."));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
				"errorCause", "bad_credentials",
				"message", "Login ou mot de passe erronés.."));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorDetails> handleUnreadableBody(HttpMessageNotReadableException ex, WebRequest request) {
		return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Corps de requête invalide", request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", ex.getMessage(), request);
	}

	private ResponseEntity<ErrorDetails> error(HttpStatus status, String errorCode, String message, WebRequest request) {
		ErrorDetails details = new ErrorDetails(
				LocalDateTime.now(),
				message,
				request.getDescription(false),
				errorCode);
		return ResponseEntity.status(status).body(details);
	}
}
