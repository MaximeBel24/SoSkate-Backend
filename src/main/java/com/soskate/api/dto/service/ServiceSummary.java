package com.soskate.api.dto.service;

public record ServiceSummary(
        Long id,
        String name,
        Integer basePriceCents,
        Integer maxParticipants
) {}