package com.soskate.api.dtos.service;

import com.soskate.api.enums.ServiceType;

import java.time.LocalDateTime;

public record ServiceResponseDTO(
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
