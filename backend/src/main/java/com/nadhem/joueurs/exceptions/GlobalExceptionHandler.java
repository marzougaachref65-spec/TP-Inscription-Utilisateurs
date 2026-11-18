package com.nadhem.joueurs.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(e -> e.getField() + " : " + e.getDefaultMessage())
				.distinct()
				.collect(java.util.stream.Collectors.joining("; "));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
	}
}
