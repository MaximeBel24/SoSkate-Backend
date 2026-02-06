package com.soskate.api.services.booking;

import com.soskate.api.dto.booking.BookingCreateRequest;
import com.soskate.api.dto.booking.BookingResponse;
import com.soskate.api.entities.BookingEntity;
import com.soskate.api.entities.BookingParticipantEntity;
import com.soskate.api.enums.BookingStatus;
import com.soskate.api.enums.InvitedBy;
import com.soskate.api.enums.ParticipantStatus;
import com.soskate.api.exceptions.booking.BookingException;
import com.soskate.api.exceptions.common.ResourceNotFoundException;
import com.soskate.api.mappers.BookingMapper;
import com.soskate.api.repositories.BookingParticipantRepository;
import com.soskate.api.repositories.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of BookingService.
 * Orchestrates booking creation, using dedicated services for context loading and validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingParticipantRepository participantRepository;
    private final BookingContextLoader contextLoader;
    private final BookingValidationService validationService;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request) {
        log.info("Creating booking for customer {} with instructor {}", request.customerId(), request.instructorId());

        // 1. Load context (all required entities)
        BookingContext context = contextLoader.loadForCreation(request);

        // 2. Validate all business rules
        validationService.validateForCreation(context, request);

        // 3. Calculate end time
        LocalDateTime endTime = request.startTime().plusMinutes(request.durationMinutes());

        // 4. Create booking
        BookingEntity booking = BookingEntity.builder()
                .instructor(context.instructor())
                .spot(context.spot())
                .service(context.service())
                .startTime(request.startTime())
                .endTime(endTime)
                .durationMinutes(request.durationMinutes())
                .maxParticipants(context.service().getMaxParticipants())
                .status(BookingStatus.OPEN)
                .build();

        BookingEntity savedBooking = bookingRepository.save(booking);

        // 5. Create participant
        int amountCents = context.calculateTotalAmount(
                request.durationMinutes(),
                request.numberOfParticipants()
        );

        BookingParticipantEntity participant = BookingParticipantEntity.builder()
                .booking(savedBooking)
                .customer(context.customer())
                .numberOfParticipants(request.numberOfParticipants())
                .status(ParticipantStatus.CONFIRMED)
                .amountCents(amountCents)
                .invitedBy(InvitedBy.SELF)
                .participantsNotes(request.participantsNotes())
                .build();

        participantRepository.save(participant);

        // 6. Update booking status if full
        updateBookingStatus(savedBooking);

        log.info("Booking created: id={}", savedBooking.getId());
        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        BookingEntity booking = findBookingById(id);
        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByInstructor(Long instructorId) {
        List<BookingEntity> bookings = bookingRepository.findByInstructorId(instructorId);
        return bookingMapper.toResponseList(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getUpcomingBookingsByInstructor(Long instructorId) {
        List<BookingEntity> bookings = bookingRepository
                .findUpcomingByInstructorId(instructorId, LocalDateTime.now());
        return bookingMapper.toResponseList(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getPassedBookingsByInstructor(Long instructorId) {
        List<BookingEntity> bookings = bookingRepository
                .findPassedByInstructorId(instructorId, LocalDateTime.now());
        return bookingMapper.toResponseList(bookings);
    }


    @Override
    @Transactional
    public BookingResponse cancelBooking(Long instructorId, Long bookingId) {
        log.info("Cancelling booking {} by instructor {}", bookingId, instructorId);

        BookingEntity booking = findBookingByIdAndInstructor(bookingId, instructorId);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Refund all confirmed participants
        List<BookingParticipantEntity> participants = participantRepository.findByBookingId(bookingId);

        for (BookingParticipantEntity participant : participants) {
            if (participant.getStatus() == ParticipantStatus.CONFIRMED) {
                participant.setStatus(ParticipantStatus.REFUNDED);
                participant.setCancelledAt(LocalDateTime.now());
                participantRepository.save(participant);
            }
        }

        log.info("Booking {} cancelled", bookingId);
        return bookingMapper.toResponse(booking);
    }


    // ========== Private helpers ==========

    private BookingEntity findBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));
    }

    private BookingEntity findBookingByIdAndInstructor(Long bookingId, Long instructorId) {
        BookingEntity booking = findBookingById(bookingId);

        if (!booking.getInstructor().getId().equals(instructorId)) {
            throw new ResourceNotFoundException("Réservation non trouvée");
        }

        return booking;
    }

    private void updateBookingStatus(BookingEntity booking) {
        int confirmedCount = participantRepository.countConfirmedParticipants(booking.getId());

        if (confirmedCount >= booking.getMaxParticipants()) {
            booking.setStatus(BookingStatus.FULL);
        } else if (confirmedCount > 0) {
            booking.setStatus(BookingStatus.OPEN);
        }

        bookingRepository.save(booking);
    }
}
