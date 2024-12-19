package com.example.medguide;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;

public class SendGridHelper {

    // Méthode pour envoyer un email de réinitialisation du mot de passe
    public static void sendResetPasswordEmail(String userEmail, String resetCode) {
        // Remplace "YOUR_SENDGRID_API_KEY" par ta clé API SendGrid réelle
        String apiKey = "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";  // <-- Insère ici ta clé API SendGrid

        // Email de l'expéditeur (celui qui enverra l'email)
        Email from = new Email("afafhiba2002@gmail.com");  // Remplace avec l'email de ton choix
        String subject = "Réinitialisation de mot de passe";
        Email to = new Email(userEmail);  // L'email du destinataire

        // Corps du message avec le code de réinitialisation
        String messageContent = "Bonjour, <br>Voici votre code de réinitialisation : <strong>" + resetCode + "</strong><br>" +
                "Cliquez sur ce lien pour réinitialiser votre mot de passe : <a href='https://votreapp.com/reset?code=" + resetCode + "'>Réinitialiser le mot de passe</a>";

        Content content = new Content("text/html", messageContent);
        Mail mail = new Mail(from, subject, to, content);

        // Création d'une requête SendGrid pour envoyer l'email
        SendGrid sg = new SendGrid(apiKey);  // <-- Clé API utilisée ici
        Request request = new Request();

        try {
            // Envoi de l'email via l'API SendGrid
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            // Affichage de la réponse pour vérifier si l'email a été envoyé
            System.out.println("Status Code: " + response.getStatusCode());  // Affiche le code de statut HTTP
            System.out.println("Body: " + response.getBody());  // Affiche la réponse complète du serveur
            System.out.println("Headers: " + response.getHeaders());  // Affiche les en-têtes de la réponse
        } catch (IOException ex) {
            System.err.println("Erreur lors de l'envoi de l'email : " + ex.getMessage());  // Gestion des erreurs
        }
    }
}
