package com.soskate.api.services.email;

import com.soskate.api.entities.InstructorEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of EmailService for development and testing.
 * Logs email content to console instead of actually sending emails.
 *
 * Active when profile is NOT "prod" (i.e., dev, test, local).
 */
@Service
@Profile("!prod")
@Slf4j
public class MockEmailService implements EmailService {

    @Override
    public void sendInstructorInvitation(InstructorEntity instructor) {
        log.info("========================================");
        log.info("📧 MOCK EMAIL - Instructor Invitation");
        log.info("========================================");
        log.info("To: {}", instructor.getEmail());
        log.info("Subject: Bienvenue sur SoSkate - Activez votre compte instructeur");
        log.info("----------------------------------------");
        log.info("Bonjour {} {},", instructor.getFirstName(), instructor.getLastName());
        log.info("");
        log.info("Vous avez été invité(e) à rejoindre SoSkate en tant qu'instructeur de skateboard !");
        log.info("");
        log.info("Vos identifiants de connexion :");
        log.info("  Email : {}", instructor.getEmail());
        log.info("");
        log.info("Pour activer votre compte, cliquez sur le lien suivant :");
        log.info("  https://app.soskate.fr/activate?token={}", instructor.getActivationToken());
        log.info("");
        log.info("Ce lien expire dans 48 heures.");
        log.info("");
        log.info("Lors de votre première connexion, il vous sera demandé de changer votre mot de passe.");
        log.info("");
        log.info("À très bientôt sur SoSkate !");
        log.info("L'équipe SoSkate");
        log.info("========================================");
    }

    @Override
    public void sendActivationConfirmation(InstructorEntity instructor) {
        log.info("========================================");
        log.info("📧 MOCK EMAIL - Activation Confirmation");
        log.info("========================================");
        log.info("To: {}", instructor.getEmail());
        log.info("Subject: Votre compte SoSkate est activé !");
        log.info("----------------------------------------");
        log.info("Bonjour {} {},", instructor.getFirstName(), instructor.getLastName());
        log.info("");
        log.info("Félicitations ! Votre compte instructeur SoSkate est maintenant actif.");
        log.info("");
        log.info("Vous pouvez dès à présent :");
        log.info("  - Compléter votre profil avec votre biographie et vos réseaux sociaux");
        log.info("  - Gérer votre planning de disponibilités");
        log.info("  - Recevoir des réservations de cours");
        log.info("");
        log.info("Connectez-vous ici : https://app.soskate.fr/login");
        log.info("");
        log.info("Bienvenue dans l'équipe SoSkate !");
        log.info("L'équipe SoSkate");
        log.info("========================================");
    }

    @Override
    public void sendInvitationResent(InstructorEntity instructor) {
        log.info("========================================");
        log.info("📧 MOCK EMAIL - Invitation Resent");
        log.info("========================================");
        log.info("To: {}", instructor.getEmail());
        log.info("Subject: Nouvelle invitation SoSkate - Activez votre compte");
        log.info("----------------------------------------");
        log.info("Bonjour {} {},", instructor.getFirstName(), instructor.getLastName());
        log.info("");
        log.info("Une nouvelle invitation vous a été envoyée pour rejoindre SoSkate.");
        log.info("");
        log.info("Vos nouveaux identifiants de connexion :");
        log.info("  Email : {}", instructor.getEmail());
        log.info("");
        log.info("Pour activer votre compte, cliquez sur le lien suivant :");
        log.info("  https://app.soskate.fr/activate?token={}", instructor.getActivationToken());
        log.info("");
        log.info("Attention : Votre ancien lien d'activation n'est plus valide.");
        log.info("Ce nouveau lien expire dans 48 heures.");
        log.info("");
        log.info("À très bientôt sur SoSkate !");
        log.info("L'équipe SoSkate");
        log.info("========================================");
    }
}