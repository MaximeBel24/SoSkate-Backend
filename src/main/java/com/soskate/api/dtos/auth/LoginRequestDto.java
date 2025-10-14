package com.soskate.api.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Email is mandatory.") String email,
        @NotBlank(message = "Password is mandatory.") String password
) {
}
