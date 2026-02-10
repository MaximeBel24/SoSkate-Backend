package com.soskate.api.services.booking;

import com.soskate.api.dto.booking.*;
import com.soskate.api.entities.*;
import com.soskate.api.enums.BookingStatus;
import com.soskate.api.enums.ParticipantStatus;
import com.soskate.api.exceptions.booking.*;
import com.soskate.api.mappers.BookingParticipantMapper;
import com.soskate.api.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of BookingParticipantService.
 * Handles all participant-related operations for bookings.
 */
@Service
@RequiredArgsConstructor
public class BookingParticipantServiceImpl implements BookingParticipantService {

    private final BookingParticipantRepository participantRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final PlatformSettingsRepository settingsRepository;
    private final BookingParticipantMapper participantMapper;

    @Transactional
    public ParticipantResponse cancel(Long customerId, Long participantId, ParticipantCancelRequest request) {
        BookingParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ParticipationNotFoundException("Participation not found"));

        if (!participant.getCustomer().getId().equals(customerId)) {
            throw new ParticipationNotFoundException("Participation not found");
        }

        if (!participant.canBeCancelled()) {
            throw new CancellationNotAllowedException();
        }

        BookingEntity booking = participant.getBooking();
        PlatformSettingsEntity settings = settingsRepository.getSettings();

        LocalDateTime refundDeadline = booking.getStartTime()
                .minusHours(settings.getAutoRefundHoursLimit());

        boolean autoRefund = LocalDateTime.now().isBefore(refundDeadline);

        if (autoRefund) {
            participant.setStatus(ParticipantStatus.REFUNDED);
        } else {
            participant.setStatus(ParticipantStatus.CANCELLED);
            // Increment the late cancellation counter
            CustomerEntity customer = participant.getCustomer();
            customer.incrementLateCancellation();
            customerRepository.save(customer);
        }

        participant.setCancellationReason(request.reason());
        participant.setCancelledAt(LocalDateTime.now());

        BookingParticipantEntity saved = participantRepository.save(participant);

        // Update booking status
        updateBookingStatus(booking);

        return participantMapper.toResponse(saved);
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

    /**
     * Retrieves all bookings for a customer
     */
    public List<MyBookingResponse> getMyBookings(Long customerId) {
        List<BookingParticipantEntity> participations =
                participantRepository.findAllByCustomerIdWithDetails(customerId);

        return participations.stream()
                .map(participantMapper::toMyBookingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates booking notes
     */
    public MyBookingResponse updateNotes(Long customerId, Long participationId, String notes) {
        BookingParticipantEntity participation = participantRepository
                .findByIdAndCustomerId(participationId, customerId)
                .orElseThrow(() -> new ParticipationNotFoundException("Participation not found"));

        BookingEntity booking = participation.getBooking();

        // Check that the lesson is not within 24 hours
        if (booking.getStartTime().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new BookingModificationNotAllowedException(
                    "Cannot modify notes less than 24 hours before the lesson"
            );
        }

        // Check status
        if (participation.getStatus() == ParticipantStatus.CANCELLED) {
            throw new BookingModificationNotAllowedException(
                    "Cannot modify a cancelled participation"
            );
        }

        booking.setNotes(notes);
        bookingRepository.save(booking);

        return participantMapper.toMyBookingResponse(participation);
    }
}
