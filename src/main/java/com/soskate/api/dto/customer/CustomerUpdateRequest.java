package com.soskate.api.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO for updating the Customer profile.
 * All fields are optional (partial update).
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record CustomerUpdateRequest(

        @Size(min = 2, max = 50, message = "First name must contain between 2 and 50 characters")
        String firstname,

        @Size(min = 2, max = 50, message = "Last name must contain between 2 and 50 characters")
        String lastname,

        @Email(message = "Email must be valid")
        String email,

        @Pattern(
                regexp = "^(\\+33|0)[1-9](\\d{2}){4}$",
                message = "Phone must be in the format +33612345678 or 0612345678"
        )
        String phone,

        @Past(message = "Date of birth must be in the past")
        LocalDate birthDate
) {}
