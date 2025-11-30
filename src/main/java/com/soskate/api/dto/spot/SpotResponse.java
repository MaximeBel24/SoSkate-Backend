package com.soskate.api.dto.spot;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour un spot.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record SpotResponse(
        Long id,
        String name,
        String description,
        String address,
        String city,
        String zipCode,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isIndoor,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}