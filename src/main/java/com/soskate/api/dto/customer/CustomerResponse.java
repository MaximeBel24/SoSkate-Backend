package com.soskate.api.dto.customer;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for a customer.
 * Immutable and NEVER contains the password.
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
