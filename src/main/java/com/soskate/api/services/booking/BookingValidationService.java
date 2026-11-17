package com.soskate.api.services.booking;

import com.soskate.api.dto.booking.BookingCreateRequest;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.booking.*;
import com.soskate.api.repositories.InstructorSpotRepository;
import com.soskate.api.services.availability.AgendaRules;
import com.soskate.api.services.availability.AvailabilityGrid;
import com.soskate.api.services.availability.BookableDay;
import com.soskate.api.services.availability.SlotCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Validates all business rules for booking operations.
 * Extracted from BookingService to follow Single Responsibility Principle.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookingValidationService {

    private final InstructorSpotRepository instructorSpotRepository;
    private final SlotCalculationService slotCalculationService;
    private final AgendaRules rules;

    public void validateForCreation(BookingContext context, BookingCreateRequest request) {
        log.debug("Validating booking: instructor={}, spot={}, start={}",
                context.instructor().getId(), context.spot().getId(), request.startTime());
        rules.requireValidDuration(request.durationMinutes());
        validateInstructorActive(context);
        validateInstructorAtSpot(context);
        validateLessonStart(request);
        validateBookingDate(request);
        validateSlot(context, request);
        validateParticipantCount(context, request);
    }

    /**
     * Validates that the instructor is active.
     */
    private void validateInstructorActive(BookingContext context) {
        if (context.instructor().getStatus() != InstructorStatus.ACTIVE) {
            log.warn("Instructor {} not active (status={})", context.instructor().getId(), context.instructor().getStatus());
            throw new BookingException("Instructor is not available for bookings");
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
            log.warn("Instructor {} does not teach at spot {}", context.instructor().getId(), context.spot().getId());
            throw new InstructorNotAtSpotException();
        }
    }

    /**
     * Validates that the lesson starts on the hour or half past.
     */
    private void validateLessonStart(BookingCreateRequest request) {
        if (!AvailabilityGrid.isLessonStart(request.startTime().toLocalTime())) {
            log.warn("Invalid lesson start: {}", request.startTime());
            throw new BookingException("A lesson starts on the hour or half past");
        }
    }

    /**
     * Validates that the booking is not too soon (minimum calendar days in advance).
     */
    private void validateBookingDate(BookingCreateRequest request) {
        LocalDate firstOpenDate = rules.firstOpenDate();

        if (request.startTime().toLocalDate().isBefore(firstOpenDate)) {
            log.warn("Booking too soon: requested={}, first open date={}", request.startTime(), firstOpenDate);
            throw new BookingTooSoonException(firstOpenDate);
        }
    }

    /**
     * Validates the slot with the calculation that lists the slots shown to clients.
     */
    private void validateSlot(BookingContext context, BookingCreateRequest request) {
        LocalDateTime start = request.startTime();
        BookableDay day = slotCalculationService.bookableDay(context.instructor(), context.spot(), start.toLocalDate());

        if (!day.isOnOffer(start, request.durationMinutes())) {
            log.warn("Instructor {} not available at {} for {}min", context.instructor().getId(), start, request.durationMinutes());
            throw new InstructorNotAvailableException();
        }

        if (!day.canHost(start, request.durationMinutes())) {
            log.warn("Slot taken for instructor {} at {} for {}min", context.instructor().getId(), start, request.durationMinutes());
            throw new SlotNotAvailableException();
        }
    }

    /**
     * Validates that the number of participants doesn't exceed the service capacity.
     */
    private void validateParticipantCount(BookingContext context, BookingCreateRequest request) {
        if (request.numberOfParticipants() > context.service().getMaxParticipants()) {
            log.warn("Number of participants ({}) exceeds capacity ({})",
                    request.numberOfParticipants(), context.service().getMaxParticipants());
            throw new BookingException("Number of participants exceeds service capacity");
        }
    }
}
