package com.soskate.api.dtos.service;

import com.soskate.api.enums.ServiceType;

public record ServiceResponseDto(
        Long id,
        String name,
        ServiceType type,
        String description,
        Integer durationMin,
        Integer basePriceCents,
        Boolean isActive
) {
}
