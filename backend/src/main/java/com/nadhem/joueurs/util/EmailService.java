package com.nadhem.joueurs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nadhem.joueurs.exceptions.EmailSendingException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Envoie le code de validation par email via SMTP.
 *
 * Si MAIL_USERNAME / MAIL_PASSWORD ne sont pas definis, le service reste
 * demarrable et affiche le message dans la console : le flux d'inscription
 * reste donc testable sans compte Gmail reel.
 */
@Service
public class EmailService implements EmailSender {

	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

	private final JavaMailSender mailSender;
	private final String from;
	private final String mailUsername;
	private final String mailPassword;

	public EmailService(JavaMailSender mailSender,
			@Value("${app.mail.from:}") String from,
			@Value("${spring.mail.username:}") String mailUsername,
			@Value("${spring.mail.password:}") String mailPassword) {
		this.mailSender = mailSender;
		this.from = from;
		this.mailUsername = mailUsername;
		this.mailPassword = mailPassword;
	}

	@Override
	public void sendEmail(String to, String body) {
		if (!isSmtpConfigured()) {
			log.warn("[MODE DEMO] SMTP non configure : aucun email reel n'est envoye a {}. Message : {}", to, body);
			return;
		}

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
			helper.setText(body, true);
			helper.setTo(to);
			helper.setSubject("Confirmation de votre email - Joueurs & Equipes");
			helper.setFrom(StringUtils.hasText(from) ? from : mailUsername);
			mailSender.send(mimeMessage);
		} catch (MessagingException | MailException e) {
			throw new EmailSendingException("Impossible d'envoyer l'email de confirmation", e);
		}
	}

	private boolean isSmtpConfigured() {
		return StringUtils.hasText(mailUsername) && StringUtils.hasText(mailPassword);
	}
}
