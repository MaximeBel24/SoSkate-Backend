package com.soskate.api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "L'ancien mot de passe est requis")
        String currentPassword,

        @NotBlank(message = "Le nouveau mot de passe est requis")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        String newPassword
) {}
