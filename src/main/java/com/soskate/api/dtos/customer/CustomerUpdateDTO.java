package com.soskate.api.dtos.customer;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO pour la mise à jour du profil customer.
 * Tous les champs sont optionnels (nullable).
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record CustomerUpdateDTO(

    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
    String firstname,

    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    String lastname,

    @Pattern(
            regexp = "^(\\+33|0)[1-9](\\d{8})$",
            message = "Le numéro de téléphone doit être au format français valide"
    )
    String phone,

    @Past(message = "La date de naissance doit être dans le passé")
    LocalDate birthDate,

    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    String password
) {
}
