package com.soskate.api.dtos.user;

import java.time.Instant;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        Instant createdAt,
        Instant updatedAt
) {
}
