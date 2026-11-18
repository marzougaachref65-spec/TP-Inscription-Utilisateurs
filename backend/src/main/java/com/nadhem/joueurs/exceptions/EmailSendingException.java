package com.nadhem.joueurs.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class EmailSendingException extends RuntimeException {

	public EmailSendingException(String message) {
		super(message);
	}

	public EmailSendingException(String message, Throwable cause) {
		super(message, cause);
	}
}
