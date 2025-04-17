package org.example.projetdemargement.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailUtil {

    // Adresse e-mail de l'expéditeur
    private static final String FROM_EMAIL = "mouhamadoumoustapha.diouf7@unchk.edu.sn";

    // Mot de passe d'application Gmail (à créer depuis ton compte)
    private static final String FROM_PASSWORD = "ntsl xkng qxuy hmbz";

    public static void sendEmail(String to, String subject, String body) {
        // Création de la session avec les propriétés SMTP
        Properties properties = getSMTPProperties();

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });

        try {
            // Création du message e-mail
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Envoi de l'e-mail
            Transport.send(message);
            System.out.println("📧 E-mail envoyé avec succès à : " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Properties getSMTPProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        return props;
    }
}
