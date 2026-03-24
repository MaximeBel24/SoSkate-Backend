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
        log.info("Subject: Welcome to SoSkate - Activate your instructor account");
        log.info("----------------------------------------");
        log.info("Hello {} {},", instructor.getFirstName(), instructor.getLastName());
        log.info("");
        log.info("You have been invited to join SoSkate as a skateboard instructor!");
        log.info("");
        log.info("Your login credentials:");
        log.info("  Email: {}", instructor.getEmail());
        log.info("");
        log.info("To activate your account, click the following link:");
        log.info("  https://app.soskate.fr/activate?token={}", instructor.getActivationToken());
        log.info("");
        log.info("This link expires in 48 hours.");
        log.info("");
        log.info("On your first login, you will be asked to change your password.");
        log.info("");
        log.info("See you soon on SoSkate!");
        log.info("The SoSkate Team");
        log.info("========================================");
    }

    @Override
    public void sendActivationConfirmation(InstructorEntity instructor) {
        log.info("========================================");
        log.info("📧 MOCK EMAIL - Activation Confirmation");
        log.info("========================================");
        log.info("To: {}", instructor.getEmail());
        log.info("Subject: Your SoSkate account is activated!");
        log.info("----------------------------------------");
        log.info("Hello {} {},", instructor.getFirstName(), instructor.getLastName());
        log.info("");
        log.info("Congratulations! Your SoSkate instructor account is now active.");
        log.info("");
        log.info("You can now:");
        log.info("  - Complete your profile with your biography and social media links");
        log.info("  - Manage your availability schedule");
        log.info("  - Receive lesson bookings");
        log.info("");
        log.info("Log in here: https://app.soskate.fr/login");
        log.info("");
        log.info("Welcome to the SoSkate team!");
        log.info("The SoSkate Team");
        log.info("========================================");
    }

    @Override
    public void sendInvitationResent(InstructorEntity instructor) {
        log.info("========================================");
        log.info("📧 MOCK EMAIL - Invitation Resent");
        log.info("========================================");
        log.info("To: {}", instructor.getEmail());
        log.info("Subject: New SoSkate invitation - Activate your account");
        log.info("----------------------------------------");
        log.info("Hello {} {},", instructor.getFirstName(), instructor.getLastName());
        log.info("");
        log.info("A new invitation has been sent to you to join SoSkate.");
        log.info("");
        log.info("Your new login credentials:");
        log.info("  Email: {}", instructor.getEmail());
        log.info("");
        log.info("To activate your account, click the following link:");
        log.info("  https://app.soskate.fr/activate?token={}", instructor.getActivationToken());
        log.info("");
        log.info("Note: Your previous activation link is no longer valid.");
        log.info("This new link expires in 48 hours.");
        log.info("");
        log.info("See you soon on SoSkate!");
        log.info("The SoSkate Team");
        log.info("========================================");
    }

    @Override
    public void sendPasswordResetEmail(String email, String firstName, String resetCode) {
        log.info("========================================");
        log.info("📧 MOCK EMAIL - Password Reset");
        log.info("========================================");
        log.info("To: {}", email);
        log.info("Subject: SoSkate - Réinitialisation de votre mot de passe");
        log.info("----------------------------------------");
        log.info("Bonjour {},", firstName);
        log.info("");
        log.info("Vous avez demandé la réinitialisation de votre mot de passe.");
        log.info("");
        log.info("Votre code de vérification : {}", resetCode);
        log.info("");
        log.info("Ce code expire dans 15 minutes.");
        log.info("");
        log.info("Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.");
        log.info("");
        log.info("L'équipe SoSkate");
        log.info("========================================");
    }

}