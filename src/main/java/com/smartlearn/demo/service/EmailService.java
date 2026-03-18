package com.smartlearn.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Envoyer un email de confirmation d'inscription
     */
    public void sendEnrollmentConfirmation(String email, String studentName, String courseName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Inscription confirmée - " + courseName);
        message.setText("Bonjour " + studentName + ",\n\n"
                + "Votre inscription au cours \"" + courseName + "\" a été confirmée.\n"
                + "Vous pouvez maintenant accéder au contenu du cours.\n\n"
                + "Bonne formation!\n\n"
                + "SmartLearn");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log only, don't fail the enrollment
            System.err.println("Erreur lors de l'envoi d'email : " + e.getMessage());
        }
    }

    /**
     * Envoyer un email de confirmation de paiement
     */
    public void sendPaymentConfirmation(String email, String studentName, String courseName, String amount, String currency) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Paiement confirmé - " + courseName);
        message.setText("Bonjour " + studentName + ",\n\n"
                + "Votre paiement de " + amount + " " + currency + " pour le cours \"" + courseName + "\" a été confirmé.\n"
                + "Votre accès au cours est maintenant activé.\n\n"
                + "Bonne formation!\n\n"
                + "SmartLearn");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi d'email : " + e.getMessage());
        }
    }

    /**
     * Envoyer un email échecc de paiement
     */
    public void sendPaymentFailureNotification(String email, String studentName, String courseName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Paiement échoué - " + courseName);
        message.setText("Bonjour " + studentName + ",\n\n"
                + "Votre paiement pour le cours \"" + courseName + "\" a échoué.\n"
                + "Veuillez essayer à nouveau ou contacter le support.\n\n"
                + "SmartLearn");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi d'email : " + e.getMessage());
        }
    }

    /**
     * Envoyer un email de confirmation de remboursement
     */
    public void sendRefundConfirmation(String email, String studentName, String courseName, String amount, String currency) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Remboursement confirmé - " + courseName);
        message.setText("Bonjour " + studentName + ",\n\n"
                + "Votre demande de remboursement de " + amount + " " + currency + " pour le cours \"" + courseName + "\" a été confirmée.\n"
                + "Le montant sera restitué sur votre compte dans 3-5 jours ouvrables.\n\n"
                + "SmartLearn");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi d'email : " + e.getMessage());
        }
    }
}
