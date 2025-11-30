package com.soskate.api.dto.customer;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour un customer.
 * Immutable et ne contient JAMAIS le mot de passe.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record CustomerResponse(
        Long id,
        String email,
        String firstname,
        String lastname,
        String phone,
        LocalDate birthDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
