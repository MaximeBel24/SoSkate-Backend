package com.soskate.api.dto.instructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request record for instructor account activation.
 * Contains the activation token and the new password chosen by the instructor.
 */
public record InstructorActivateRequest(

        @NotBlank(message = "Le token d'activation est obligatoire")
        @Size(min = 36, max = 36, message = "Format du token d'activation invalide")
        String token,

        /**
         * New password chosen by the instructor.
         * Must meet security requirements:
         * - Minimum 8 characters
         * - At least one uppercase letter
         * - At least one lowercase letter
         * - At least one digit
         * - At least one special character
         */
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, max = 100, message = "Le mot de passe doit contenir entre 8 et 100 caractères")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,;:!§+-])[A-Za-z\\d@$!%*?&.,;:!§+-]{8,}$",
                message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial"
        )
        String password,

        /**
         * Password confirmation to prevent typos.
         */
        @NotBlank(message = "La confirmation du mot de passe est obligatoire")
        String passwordConfirmation

) {}
