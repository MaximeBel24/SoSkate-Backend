package com.soskate.api.services.booking;

import com.soskate.api.dto.booking.BookingCreateRequest;
import com.soskate.api.dto.booking.BookingResponse;
import com.soskate.api.entities.*;
import com.soskate.api.enums.BookingStatus;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.enums.InvitedBy;
import com.soskate.api.enums.ParticipantStatus;
import com.soskate.api.exceptions.booking.*;
import com.soskate.api.exceptions.common.ResourceNotFoundException;
import com.soskate.api.exceptions.customer.CustomerNotFoundException;
import com.soskate.api.exceptions.instructor.InstructorNotFoundException;
import com.soskate.api.exceptions.service.ServiceNotFoundException;
import com.soskate.api.exceptions.spot.SpotNotFoundException;
import com.soskate.api.mappers.BookingMapper;
import com.soskate.api.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingParticipantRepository participantRepository;
    private final InstructorRepository instructorRepository;
    private final CustomerRepository customerRepository;
    private final SpotRepository spotRepository;
    private final ServiceRepository serviceRepository;
    private final PlatformSettingsRepository settingsRepository;
    private final InstructorSpotRepository instructorSpotRepository;
    private final AvailabilityRepository availabilityRepository;
    private final BufferCalculationService bufferCalculationService;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingResponse create(Long customerId, BookingCreateRequest request) {
        // 1. Valider la durée
        if (!isValidDuration(request.durationMinutes())) {
            throw new BookingException("Durée invalide. Valeurs autorisées : 60, 90, 120, 180, 240 minutes");
        }

        // 2. Récupérer les entités
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        InstructorEntity instructor = instructorRepository.findById(request.instructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructeur non trouvé"));

        SpotEntity spot = spotRepository.findById(request.spotId())
                .orElseThrow(() -> new ResourceNotFoundException("Spot non trouvé"));

        ServiceEntity service = serviceRepository.findById(request.serviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service non trouvé"));

        PlatformSettingsEntity settings = settingsRepository.getSettings();

        // 3. Valider les règles métier
        validateBookingRules(instructor, spot, service, request, settings);

        // 4. Calculer l'heure de fin
        LocalDateTime endTime = request.startTime().plusMinutes(request.durationMinutes());

        // 5. Créer le booking
        BookingEntity booking = BookingEntity.builder()
                .instructor(instructor)
                .spot(spot)
                .service(service)
                .startTime(request.startTime())
                .endTime(endTime)
                .durationMinutes(request.durationMinutes())
                .maxParticipants(service.getMaxParticipants())
                .status(BookingStatus.OPEN)
                .build();

        BookingEntity savedBooking = bookingRepository.save(booking);

        // 6. Calculer le prix basé sur la durée
        double hours = request.durationMinutes() / 60.0;
        int pricePerParticipant = (int) Math.round(service.getBasePriceCents() * hours);
        int amountCents = pricePerParticipant * request.numberOfParticipants();

        // 7. Créer le participant
        BookingParticipantEntity participant = BookingParticipantEntity.builder()
                .booking(savedBooking)
                .customer(customer)
                .numberOfParticipants(request.numberOfParticipants())
                .status(ParticipantStatus.CONFIRMED)
                .amountCents(amountCents)
                .invitedBy(InvitedBy.SELF)
                .participantsNotes(request.participantsNotes())
                .build();

        participantRepository.save(participant);

        // 8. Mettre à jour le statut du booking si complet
        updateBookingStatus(savedBooking);

        return bookingMapper.toResponse(savedBooking);
    }

    private boolean isValidDuration(Integer durationMinutes) {
        return durationMinutes != null &&
                (durationMinutes == 60 ||
                        durationMinutes == 90 ||
                        durationMinutes == 120 ||
                        durationMinutes == 180 ||
                        durationMinutes == 240);
    }

    private void validateBookingRules(
            InstructorEntity instructor,
            SpotEntity spot,
            ServiceEntity service,
            BookingCreateRequest request,
            PlatformSettingsEntity settings
    ) {
        // Vérifier que l'instructeur est actif
        if (instructor.getStatus() != InstructorStatus.ACTIVE) {
            throw new BookingException("L'instructeur n'est pas disponible pour des réservations");
        }

        // Vérifier que l'instructeur enseigne sur ce spot
        if (!instructorSpotRepository.existsByInstructorIdAndSpotId(instructor.getId(), spot.getId())) {
            throw new InstructorNotAtSpotException();
        }

        // Vérifier le délai minimum de réservation
        LocalDateTime minBookingTime = LocalDateTime.now()
                .plusHours(settings.getMinBookingHoursInAdvance());

        if (request.startTime().isBefore(minBookingTime)) {
            throw new BookingTooSoonException(settings.getMinBookingHoursInAdvance());
        }

        // Calculer les heures avec la durée du REQUEST (pas du service)
        LocalDate date = request.startTime().toLocalDate();
        LocalTime startTime = request.startTime().toLocalTime();
        LocalTime endTime = startTime.plusMinutes(request.durationMinutes());

        // Vérifier que l'instructeur est disponible
        List<AvailabilityEntity> availabilities = availabilityRepository
                .findAvailableByInstructorAndDate(instructor.getId(), date);

        boolean isWithinAvailability = availabilities.stream()
                .anyMatch(a -> !startTime.isBefore(a.getStartTime()) && !endTime.isAfter(a.getEndTime()));

        if (!isWithinAvailability) {
            throw new InstructorNotAvailableException();
        }

        // Vérifier pas de conflit avec d'autres bookings
        List<BookingEntity> existingBookings = bookingRepository
                .findByInstructorAndDateNotCancelled(instructor.getId(), date);

        for (BookingEntity existing : existingBookings) {
            int buffer = bufferCalculationService.calculateBuffer(existing.getSpot(), spot);

            LocalTime existingStart = existing.getStartTime().toLocalTime();
            LocalTime existingEnd = existing.getEndTime().toLocalTime();

            LocalTime blockedStart = existingStart.minusMinutes(buffer);
            LocalTime blockedEnd = existingEnd.plusMinutes(buffer);

            boolean overlaps = startTime.isBefore(blockedEnd) && endTime.isAfter(blockedStart);

            if (overlaps) {
                throw new SlotNotAvailableException();
            }
        }

        // Vérifier le nombre de participants
        if (request.numberOfParticipants() > service.getMaxParticipants()) {
            throw new BookingException("Le nombre de participants dépasse la capacité du service");
        }
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

    public BookingResponse getById(Long id) {
        BookingEntity booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));
        return bookingMapper.toResponse(booking);
    }

    public List<BookingResponse> getByInstructor(Long instructorId) {
        List<BookingEntity> bookings = bookingRepository.findByInstructorId(instructorId);
        return bookingMapper.toResponseList(bookings);
    }

    public List<BookingResponse> getUpcomingByInstructor(Long instructorId) {
        List<BookingEntity> bookings = bookingRepository
                .findUpcomingByInstructorId(instructorId, LocalDateTime.now());
        return bookingMapper.toResponseList(bookings);
    }

    public List<BookingResponse> getUpcomingBySpot(Long spotId) {
        List<BookingEntity> bookings = bookingRepository
                .findUpcomingBySpotId(spotId, LocalDateTime.now());
        return bookingMapper.toResponseList(bookings);
    }

    @Transactional
    public BookingResponse complete(Long instructorId, Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));

        if (!booking.getInstructor().getId().equals(instructorId)) {
            throw new ResourceNotFoundException("Réservation non trouvée");
        }

        if (booking.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BookingException("Le cours n'a pas encore eu lieu");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        // Marquer tous les participants confirmés comme COMPLETED
        List<BookingParticipantEntity> participants = participantRepository
                .findByBookingIdAndStatus(bookingId, ParticipantStatus.CONFIRMED);

        for (BookingParticipantEntity participant : participants) {
            participant.setStatus(ParticipantStatus.COMPLETED);
            participantRepository.save(participant);
        }

        return bookingMapper.toResponse(booking);
    }

    @Transactional
    public BookingResponse cancel(Long instructorId, Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));

        if (!booking.getInstructor().getId().equals(instructorId)) {
            throw new ResourceNotFoundException("Réservation non trouvée");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Rembourser tous les participants
        List<BookingParticipantEntity> participants = participantRepository.findByBookingId(bookingId);

        for (BookingParticipantEntity participant : participants) {
            if (participant.getStatus() == ParticipantStatus.CONFIRMED) {
                participant.setStatus(ParticipantStatus.REFUNDED);
                participant.setCancelledAt(LocalDateTime.now());
                participantRepository.save(participant);
            }
        }

        return bookingMapper.toResponse(booking);
    }
}