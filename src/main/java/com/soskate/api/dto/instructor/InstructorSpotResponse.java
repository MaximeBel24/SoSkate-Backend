package com.soskate.api.dto.instructor;

import com.soskate.api.dto.spot.SpotSummary;

import java.time.LocalDateTime;

public record InstructorSpotResponse(
        Long id,
        Long instructorId,
        SpotSummary spot,
        LocalDateTime createdAt
) {}