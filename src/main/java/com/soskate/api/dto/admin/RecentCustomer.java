package com.soskate.api.dto.admin;

import java.time.LocalDateTime;

public record RecentCustomer(
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt
) {
}
