package com.soskate.api.dtos.spot;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SpotDto(
        Long id,
        String name,
        String addressLine1,
        String addressLine2,
        String city,
        String postalCode,
        String country,        // ex: "FR"
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isIndoor,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
