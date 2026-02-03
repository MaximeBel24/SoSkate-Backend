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

        @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
        String firstname,

        @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
        String lastname,

        @Size(max = 15, message = "Le téléphone ne peut pas dépasser 15 caractères")
        @Pattern(regexp = "^[+]?[0-9\\s-]{0,15}$", message = "Le format du téléphone est invalide")
        String phone,

        @Size(max = 2000, message = "La bio ne peut pas dépasser 2000 caractères")
        String bio,

        SkateSpecialty specialty,

        @Min(value = 0, message = "L'expérience ne peut pas être négative")
        @Max(value = 50, message = "L'expérience ne peut pas dépasser 50 ans")
        Integer yearsOfExperience,

        @Size(max = 30, message = "Le pseudo Instagram ne peut pas dépasser 30 caractères")
        @Pattern(regexp = "^[a-zA-Z0-9_.]*$", message = "Le pseudo Instagram ne peut contenir que des lettres, chiffres, underscores et points")
        String instagramHandle,

        @Size(max = 100, message = "La chaîne YouTube ne peut pas dépasser 100 caractères")
        String youtubeChannel

) {}
