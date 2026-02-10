package com.soskate.api.dto.auth.register;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO for registering a new customer (rider).
 * Immutable and validated via Bean Validation.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record CustomerRegisterRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email cannot exceed 255 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "Invalid email format"
        )
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must contain at least 8 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
                message = "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one digit and one special character"
        )
        String password,

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 100, message = "First name must contain between 2 and 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 100, message = "Last name must contain between 2 and 100 characters")
        String lastName,

        @Pattern(
                regexp = "^(\\+33|0)[1-9](\\d{8})$",
                message = "Phone number must be in a valid French format"
        )
        String phone,

        @Past(message = "Date of birth must be in the past")
        LocalDate birthDate
) {
}
