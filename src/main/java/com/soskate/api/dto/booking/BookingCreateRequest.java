package com.soskate.api.dto.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record BookingCreateRequest(

        @NotNull(message = "Customer is required")
        Long customerId,

        @NotNull(message = "Instructor is required")
        Long instructorId,

        @NotNull(message = "Spot is required")
        Long spotId,

        @NotNull(message = "Service is required")
        Long serviceId,

        @NotNull(message = "Start time is required")
        LocalDateTime startTime,

        @NotNull(message = "Duration is required")
        Integer durationMinutes,

        @NotNull(message = "Number of participants is required")
        @Min(value = 1, message = "There must be at least 1 participant")
        Integer numberOfParticipants,

        @Size(max = 500, message = "Notes cannot exceed 500 characters")
        String participantsNotes
) {}
