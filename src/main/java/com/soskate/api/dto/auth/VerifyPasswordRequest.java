package com.soskate.api.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record VerifyPasswordRequest(
        @NotBlank(message = "Le mot de passe est requis")
        String password
) {}
