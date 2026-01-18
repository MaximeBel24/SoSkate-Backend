package com.soskate.api.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO pour la mise à jour du profil Customer.
 * Tous les champs sont optionnels (mise à jour partielle).
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record CustomerUpdateRequest(

        @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
        String firstname,

        @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
        String lastname,

        @Email(message = "L'email doit être valide")
        String email,

        @Pattern(
                regexp = "^(\\+33|0)[1-9](\\d{2}){4}$",
                message = "Le téléphone doit être au format +33612345678 ou 0612345678"
        )
        String phone,

        @Past(message = "La date de naissance doit être dans le passé")
        LocalDate birthDate
) {}