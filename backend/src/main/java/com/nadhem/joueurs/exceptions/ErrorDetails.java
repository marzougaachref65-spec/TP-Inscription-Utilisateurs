package com.nadhem.joueurs.exceptions;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Format d'erreur unique utilise par POST /register et GET /verifyEmail. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {

	private LocalDateTime timestamp;
	private String message;
	private String path;
	private String errorCode;
}
