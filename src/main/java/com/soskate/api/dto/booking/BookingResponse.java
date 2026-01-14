package com.soskate.api.dto.booking;

import com.soskate.api.dto.instructor.InstructorSummary;
import com.soskate.api.dto.service.ServiceSummary;
import com.soskate.api.dto.spot.SpotSummary;
import com.soskate.api.enums.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long id,
        InstructorSummary instructor,
        SpotSummary spot,
        ServiceSummary service,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer durationMinutes,
        Integer maxParticipants,
        Integer confirmedParticipants,
        Integer availablePlaces,
        BookingStatus status,
        String notes,
        List<ParticipantSummary> participants,
        LocalDateTime createdAt
) {}