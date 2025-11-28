package com.soskate.api.dto.service;

import com.soskate.api.enums.ServiceType;

import java.time.LocalDateTime;

public record ServiceResponse(
        Long id,
        String name,
        ServiceType type,
        String description,
        Integer durationMinutes,
        Integer basePriceCents,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
