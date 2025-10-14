package com.soskate.api.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(
        @NotBlank(message = "Email is mandatory.") String email,
        @NotBlank(message = "Password is mandatory.") String password,
        @NotBlank(message = "Firstname is mandatory.") String firstName,
        @NotBlank(message = "Lastname is mandatory") String lastName
) {
}
