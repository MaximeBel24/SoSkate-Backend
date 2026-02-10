package com.soskate.api.dto.instructor;

import com.soskate.api.enums.SkateSpecialty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request record for updating an instructor's profile.
 * Used by the instructor to complete/update their own profile after activation.
 * All fields are optional - only provided fields will be updated.
 */
public record InstructorUpdateRequest(

        @Size(min = 2, max = 50, message = "First name must contain between 2 and 50 characters")
        String firstname,

        @Size(min = 2, max = 50, message = "Last name must contain between 2 and 50 characters")
        String lastname,

        @Size(max = 15, message = "Phone cannot exceed 15 characters")
        @Pattern(regexp = "^[+]?[0-9\\s-]{0,15}$", message = "Invalid phone format")
        String phone,

        @Size(max = 2000, message = "Bio cannot exceed 2000 characters")
        String bio,

        SkateSpecialty specialty,

        @Min(value = 0, message = "Experience cannot be negative")
        @Max(value = 50, message = "Experience cannot exceed 50 years")
        Integer yearsOfExperience,

        @Size(max = 30, message = "Instagram handle cannot exceed 30 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_.]*$", message = "Instagram handle can only contain letters, digits, underscores and dots")
        String instagramHandle,

        @Size(max = 100, message = "YouTube channel cannot exceed 100 characters")
        String youtubeChannel

) {}
