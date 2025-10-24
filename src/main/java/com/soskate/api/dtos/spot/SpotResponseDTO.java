package com.soskate.api.dtos.spot;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de réponse pour un spot.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public record SpotResponseDTO(
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
        List<String> photos,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}