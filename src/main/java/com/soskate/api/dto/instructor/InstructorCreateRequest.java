package com.soskate.api.dto.instructor;

import com.soskate.api.enums.SkateSpecialty;
import jakarta.validation.constraints.*;

/**
 * Request record for creating a new instructor account by an admin.
 * Only basic information is required at this stage.
 * The instructor will complete their profile after activation.
 */
public record InstructorCreateRequest(

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must contain between 2 and 50 characters")
        String firstname,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must contain between 2 and 50 characters")
        String lastname,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email,

        @Size(max = 15, message = "Phone cannot exceed 15 characters")
        @Pattern(regexp = "^[+]?[0-9\\s-]{0,15}$", message = "Invalid phone format")
        String phone,

        /**
         * Optional: Admin can pre-fill the specialty if known.
         */
        SkateSpecialty specialty,

        /**
         * Optional: Admin can pre-fill years of experience if known.
         */
        @Min(value = 0, message = "Experience cannot be negative")
        @Max(value = 50, message = "Experience cannot exceed 50 years")
        Integer yearsOfExperience

) {}
