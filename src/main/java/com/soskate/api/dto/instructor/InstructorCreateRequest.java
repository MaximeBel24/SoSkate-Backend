package com.soskate.api.dto.instructor;

import com.soskate.api.enums.SkateSpecialty;
import jakarta.validation.constraints.*;

/**
 * Request record for creating a new instructor account by an admin.
 * Only basic information is required at this stage.
 * The instructor will complete their profile after activation.
 */
public record InstructorCreateRequest(

        @NotBlank(message = "Le prénom est obligatoire")
        @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
        String firstname,

        @NotBlank(message = "Le nom est obligatoire")
        @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
        String lastname,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email doit être valide")
        @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
        String email,

        @Size(max = 15, message = "Le téléphone ne peut pas dépasser 15 caractères")
        @Pattern(regexp = "^[+]?[0-9\\s-]{0,15}$", message = "Le format du téléphone est invalide")
        String phone,

        /**
         * Optional: Admin can pre-fill the specialty if known.
         */
        SkateSpecialty specialty,

        /**
         * Optional: Admin can pre-fill years of experience if known.
         */
        @Min(value = 0, message = "L'expérience ne peut pas être négative")
        @Max(value = 50, message = "L'expérience ne peut pas dépasser 50 ans")
        Integer yearsOfExperience

) {}
