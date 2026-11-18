package com.nadhem.joueurs.util;

/** Abstraction de l'envoi d'email (TP 2025 : code de confirmation). */
public interface EmailSender {

	void sendEmail(String toEmail, String body);
}
