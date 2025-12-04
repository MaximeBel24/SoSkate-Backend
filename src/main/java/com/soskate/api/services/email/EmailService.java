package com.soskate.api.services.email;

import com.soskate.api.entities.InstructorEntity;

/**
 * Interface for email sending operations.
 * Allows for different implementations (real SMTP, mock for testing, etc.)
 */
public interface EmailService {

    /**
     * Sends an invitation email to a newly created instructor.
     * The email contains the activation link with the token.
     *
     * @param instructor The instructor to send the invitation to
     */
    void sendInstructorInvitation(InstructorEntity instructor);

    /**
     * Sends a confirmation email after successful account activation.
     *
     * @param instructor The instructor who activated their account
     */
    void sendActivationConfirmation(InstructorEntity instructor);

    /**
     * Sends a notification when the invitation is resent.
     *
     * @param instructor The instructor to resend the invitation to
     */
    void sendInvitationResent(InstructorEntity instructor);
}