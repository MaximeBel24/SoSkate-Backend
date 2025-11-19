package com.soskate.api.dto.auth.register;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO pour l'inscription d'un nouveau customer (rider).
 * Immutable et validé via Bean Validation.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record CustomerRegisterRequestDTO(

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email doit être valide")
        @Size(max = 255, message = "L'email ne peut pas dépasser 255 caractères")
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "Format d'email invalide"
        )
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
                message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial"
        )
        String password,

        @NotBlank(message = "Le prénom est obligatoire")
        @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
        String firstname,

        @NotBlank(message = "Le nom est obligatoire")
        @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
        String lastname,

        @Pattern(
                regexp = "^(\\+33|0)[1-9](\\d{8})$",
                message = "Le numéro de téléphone doit être au format français valide"
        )
        String phone,

        @Past(message = "La date de naissance doit être dans le passé")
        LocalDate birthDate
) {
}
