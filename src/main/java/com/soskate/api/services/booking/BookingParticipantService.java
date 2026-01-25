package com.soskate.api.services.booking;

import com.soskate.api.dto.booking.*;
import com.soskate.api.entities.*;
import com.soskate.api.enums.BookingStatus;
import com.soskate.api.enums.InvitedBy;
import com.soskate.api.enums.ParticipantStatus;
import com.soskate.api.exceptions.booking.*;
import com.soskate.api.exceptions.common.ResourceNotFoundException;
import com.soskate.api.exceptions.customer.CustomerNotFoundException;
import com.soskate.api.mappers.BookingParticipantMapper;
import com.soskate.api.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingParticipantService {

    private final BookingParticipantRepository participantRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final PlatformSettingsRepository settingsRepository;
    private final BookingParticipantMapper participantMapper;

    @Transactional
    public ParticipantResponse inviteByInstructor(Long bookingId, ParticipantInviteRequest request) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));

        CustomerEntity customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Client non trouvé"));

        validateInvitation(booking, customer, request.numberOfParticipants());

        BookingParticipantEntity participant = BookingParticipantEntity.builder()
                .booking(booking)
                .customer(customer)
                .numberOfParticipants(request.numberOfParticipants() != null ? request.numberOfParticipants() : 1)
                .status(ParticipantStatus.INVITED)
                .invitedBy(InvitedBy.INSTRUCTOR)
                .participantsNotes(request.participantsNotes())
                .build();

        BookingParticipantEntity saved = participantRepository.save(participant);
        return participantMapper.toResponse(saved);
    }

    @Transactional
    public ParticipantResponse inviteByParticipant(
            Long bookingId,
            Long inviterCustomerId,
            ParticipantInviteRequest request
    ) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));

        CustomerEntity customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Client non trouvé"));

        CustomerEntity inviter = customerRepository.findById(inviterCustomerId)
                .orElseThrow(() -> new CustomerNotFoundException("Client invitant non trouvé"));

        // Vérifier que l'invitant est bien participant
        if (!participantRepository.existsByBookingIdAndCustomerId(bookingId, inviterCustomerId)) {
            throw new BookingException("Vous n'êtes pas participant de ce cours");
        }

        validateInvitation(booking, customer, request.numberOfParticipants());

        BookingParticipantEntity participant = BookingParticipantEntity.builder()
                .booking(booking)
                .customer(customer)
                .numberOfParticipants(request.numberOfParticipants() != null ? request.numberOfParticipants() : 1)
                .status(ParticipantStatus.INVITED)
                .invitedBy(InvitedBy.PARTICIPANT)
                .invitedByCustomer(inviter)
                .participantsNotes(request.participantsNotes())
                .build();

        BookingParticipantEntity saved = participantRepository.save(participant);
        return participantMapper.toResponse(saved);
    }

    private void validateInvitation(BookingEntity booking, CustomerEntity customer, Integer numberOfParticipants) {
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Ce cours est annulé");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BookingException("Ce cours est terminé");
        }

        if (participantRepository.existsByBookingIdAndCustomerId(booking.getId(), customer.getId())) {
            throw new ParticipantAlreadyExistsException();
        }

        int currentCount = participantRepository.countConfirmedParticipants(booking.getId());
        int requestedCount = numberOfParticipants != null ? numberOfParticipants : 1;

        if (currentCount + requestedCount > booking.getMaxParticipants()) {
            throw new BookingFullException();
        }
    }

    @Transactional
    public ParticipantResponse acceptInvitation(Long customerId, Long participantId) {
        BookingParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participation non trouvée"));

        if (!participant.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Participation non trouvée");
        }

        if (participant.getStatus() != ParticipantStatus.INVITED) {
            throw new BookingException("Cette invitation n'est plus valide");
        }

        participant.setStatus(ParticipantStatus.PENDING_PAYMENT);
        BookingParticipantEntity saved = participantRepository.save(participant);
        return participantMapper.toResponse(saved);
    }

    @Transactional
    public ParticipantResponse confirm(Long customerId, Long participantId, ParticipantConfirmRequest request) {
        BookingParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participation non trouvée"));

        if (!participant.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Participation non trouvée");
        }

        if (participant.getStatus() != ParticipantStatus.PENDING_PAYMENT) {
            throw new BookingException("Le paiement n'est pas attendu pour cette participation");
        }

        // Calculer le montant
        BookingEntity booking = participant.getBooking();
        int amountCents = booking.getService().getBasePriceCents() * participant.getNumberOfParticipants();

        participant.setStatus(ParticipantStatus.CONFIRMED);
        participant.setAmountCents(amountCents);
        participant.setPaymentIntentId(request.paymentIntentId());

        if (request.participantsNotes() != null) {
            participant.setParticipantsNotes(request.participantsNotes());
        }

        BookingParticipantEntity saved = participantRepository.save(participant);

        // Mettre à jour le statut du booking
        updateBookingStatus(booking);

        return participantMapper.toResponse(saved);
    }

    @Transactional
    public ParticipantResponse cancel(Long customerId, Long participantId, ParticipantCancelRequest request) {
        BookingParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participation non trouvée"));

        if (!participant.getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Participation non trouvée");
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
            // Incrémenter le compteur d'annulations tardives
            CustomerEntity customer = participant.getCustomer();
            customer.incrementLateCancellation();
            customerRepository.save(customer);
        }

        participant.setCancellationReason(request.reason());
        participant.setCancelledAt(LocalDateTime.now());

        BookingParticipantEntity saved = participantRepository.save(participant);

        // Mettre à jour le statut du booking
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

    public List<ParticipantResponse> getByBooking(Long bookingId) {
        List<BookingParticipantEntity> participants = participantRepository.findByBookingId(bookingId);
        return participantMapper.toResponseList(participants);
    }

    public List<ParticipantResponse> getUpcomingByCustomer(Long customerId) {
        List<BookingParticipantEntity> participants = participantRepository
                .findUpcomingByCustomerId(customerId, LocalDateTime.now());
        return participantMapper.toResponseList(participants);
    }

    public List<ParticipantResponse> getPendingInvitations(Long customerId) {
        List<BookingParticipantEntity> participants = participantRepository
                .findPendingInvitationsByCustomerId(customerId);
        return participantMapper.toResponseList(participants);
    }

    public List<ParticipantResponse> getCompletedByCustomer(Long customerId) {
        List<BookingParticipantEntity> participants = participantRepository
                .findCompletedByCustomerId(customerId);
        return participantMapper.toResponseList(participants);
    }

    /**
     * Récupère toutes les réservations d'un customer
     */
    public List<MyBookingResponse> getMyBookings(Long customerId) {
        List<BookingParticipantEntity> participations =
                participantRepository.findAllByCustomerIdWithDetails(customerId);

        return participations.stream()
                .map(participantMapper::toMyBookingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Modifie les notes d'une réservation
     */
    public MyBookingResponse updateNotes(Long customerId, Long participationId, String notes) {
        BookingParticipantEntity participation = participantRepository
                .findByIdAndCustomerId(participationId, customerId)
                .orElseThrow(() -> new ParticipationNotFoundException("Participation non trouvée"));

        BookingEntity booking = participation.getBooking();

        // Vérifier que le cours n'est pas dans moins de 24h
        if (booking.getStartTime().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new BookingModificationNotAllowedException(
                    "Impossible de modifier les notes moins de 24h avant le cours"
            );
        }

        // Vérifier le statut
        if (participation.getStatus() == ParticipantStatus.CANCELLED) {
            throw new BookingModificationNotAllowedException(
                    "Impossible de modifier une participation annulée"
            );
        }

        booking.setNotes(notes);
        bookingRepository.save(booking);

        return participantMapper.toMyBookingResponse(participation);
    }
}