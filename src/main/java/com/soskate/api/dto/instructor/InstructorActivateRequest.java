package com.soskate.api.dto.instructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request record for instructor account activation.
 * Contains the activation token and the new password chosen by the instructor.
 */
public record InstructorActivateRequest(

        @NotBlank(message = "Activation token is required")
        @Size(min = 36, max = 36, message = "Invalid activation token format")
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
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must contain between 8 and 100 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,;:!§+-])[A-Za-z\\d@$!%*?&.,;:!§+-]{8,}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one digit and one special character"
        )
        String password,

        /**
         * Password confirmation to prevent typos.
         */
        @NotBlank(message = "Password confirmation is required")
        String passwordConfirmation

) {}
