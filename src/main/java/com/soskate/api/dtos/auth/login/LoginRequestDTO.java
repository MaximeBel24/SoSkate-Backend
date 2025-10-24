package com.soskate.api.dtos.auth.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO pour la requête de connexion.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record LoginRequestDTO(

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email doit être valide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        String password
) {}
