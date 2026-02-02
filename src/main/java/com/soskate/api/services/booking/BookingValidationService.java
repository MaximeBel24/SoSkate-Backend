package com.soskate.api.services.booking;

import com.soskate.api.dto.booking.BookingCreateRequest;
import com.soskate.api.entities.AvailabilityEntity;
import com.soskate.api.entities.BookingEntity;
import com.soskate.api.entities.SpotEntity;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.booking.*;
import com.soskate.api.repositories.AvailabilityRepository;
import com.soskate.api.repositories.BookingRepository;
import com.soskate.api.repositories.InstructorSpotRepository;
import com.soskate.api.services.availability.BufferCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Validates all business rules for booking operations.
 * Extracted from BookingService to follow Single Responsibility Principle.
 */
@Component
@RequiredArgsConstructor
public class BookingValidationService {

    private static final Set<Integer> VALID_DURATIONS = Set.of(60, 90, 120, 180, 240);

    private final InstructorSpotRepository instructorSpotRepository;
    private final AvailabilityRepository availabilityRepository;
    private final BookingRepository bookingRepository;
    private final BufferCalculationService bufferCalculationService;

    public void validateForCreation(BookingContext context, BookingCreateRequest request) {
        validateDuration(request.durationMinutes());
        validateInstructorActive(context);
        validateInstructorAtSpot(context);
        validateBookingTime(context, request);
        validateInstructorAvailability(context, request);
        validateNoConflict(context, request);
        validateParticipantCount(context, request);
    }

    public void validateDuration(Integer durationMinutes) {
        if (durationMinutes == null || !VALID_DURATIONS.contains(durationMinutes)) {
            throw new BookingException(
                    "Durée invalide. Valeurs autorisées : 60, 90, 120, 180, 240 minutes"
            );
        }
    }

    /**
     * Validates that the instructor is active.
     */
    private void validateInstructorActive(BookingContext context) {
        if (context.instructor().getStatus() != InstructorStatus.ACTIVE) {
            throw new BookingException("L'instructeur n'est pas disponible pour des réservations");
        }
    }

    /**
     * Validates that the instructor teaches at the requested spot.
     */
    private void validateInstructorAtSpot(BookingContext context) {
        boolean teachesAtSpot = instructorSpotRepository.existsByInstructorIdAndSpotId(
                context.instructor().getId(),
                context.spot().getId()
        );

        if (!teachesAtSpot) {
            throw new InstructorNotAtSpotException();
        }
    }

    /**
     * Validates that the booking is not too soon (minimum hours in advance).
     */
    private void validateBookingTime(BookingContext context, BookingCreateRequest request) {
        int minHoursInAdvance = context.settings().getMinBookingHoursInAdvance();
        LocalDateTime minBookingTime = LocalDateTime.now().plusHours(minHoursInAdvance);

        if (request.startTime().isBefore(minBookingTime)) {
            throw new BookingTooSoonException(minHoursInAdvance);
        }
    }

    /**
     * Validates that the instructor has availability for the requested time slot.
     */
    private void validateInstructorAvailability(BookingContext context, BookingCreateRequest request) {
        LocalDate date = request.startTime().toLocalDate();
        LocalTime startTime = request.startTime().toLocalTime();
        LocalTime endTime = startTime.plusMinutes(request.durationMinutes());

        List<AvailabilityEntity> availabilities = availabilityRepository
                .findAvailableByInstructorAndDate(context.instructor().getId(), date);

        boolean isWithinAvailability = availabilities.stream()
                .anyMatch(a -> !startTime.isBefore(a.getStartTime()) && !endTime.isAfter(a.getEndTime()));

        if (!isWithinAvailability) {
            throw new InstructorNotAvailableException();
        }
    }

    /**
     * Validates that there are no conflicts with existing bookings.
     * Takes into account buffer time between different spots.
     */
    private void validateNoConflict(BookingContext context, BookingCreateRequest request) {
        LocalDate date = request.startTime().toLocalDate();
        LocalTime startTime = request.startTime().toLocalTime();
        LocalTime endTime = startTime.plusMinutes(request.durationMinutes());
        SpotEntity requestedSpot = context.spot();

        List<BookingEntity> existingBookings = bookingRepository
                .findByInstructorAndDateNotCancelled(context.instructor().getId(), date);

        for (BookingEntity existing : existingBookings) {
            int buffer = bufferCalculationService.calculateBuffer(existing.getSpot(), requestedSpot);

            LocalTime existingStart = existing.getStartTime().toLocalTime();
            LocalTime existingEnd = existing.getEndTime().toLocalTime();

            LocalTime blockedStart = existingStart.minusMinutes(buffer);
            LocalTime blockedEnd = existingEnd.plusMinutes(buffer);

            boolean overlaps = startTime.isBefore(blockedEnd) && endTime.isAfter(blockedStart);

            if (overlaps) {
                throw new SlotNotAvailableException();
            }
        }
    }

    /**
     * Validates that the number of participants doesn't exceed the service capacity.
     */
    private void validateParticipantCount(BookingContext context, BookingCreateRequest request) {
        if (request.numberOfParticipants() > context.service().getMaxParticipants()) {
            throw new BookingException("Le nombre de participants dépasse la capacité du service");
        }
    }
}
