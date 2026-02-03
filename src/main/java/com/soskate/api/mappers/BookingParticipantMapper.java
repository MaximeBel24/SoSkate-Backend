package com.soskate.api.mappers;

import com.soskate.api.dto.booking.MyBookingResponse;
import com.soskate.api.dto.customer.CustomerSummary; // ← Utilise ton existant
import com.soskate.api.dto.booking.ParticipantResponse;
import com.soskate.api.entities.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingParticipantMapper {

    public ParticipantResponse toResponse(BookingParticipantEntity entity) {
        return new ParticipantResponse(
                entity.getId(),
                entity.getBooking().getId(),
                toCustomerSummary(entity.getCustomer()),
                entity.getNumberOfParticipants(),
                entity.getStatus(),
                entity.getAmountCents(),
                entity.getPaymentIntentId(),
                entity.getInvitedBy(),
                entity.getInvitedByCustomer() != null
                        ? toCustomerSummary(entity.getInvitedByCustomer())
                        : null,
                entity.getParticipantsNotes(),
                entity.getCancellationReason(),
                entity.getCancelledAt(),
                entity.getCreatedAt()
        );
    }

    public List<ParticipantResponse> toResponseList(List<BookingParticipantEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerSummary toCustomerSummary(CustomerEntity entity) {
        return new CustomerSummary(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail()
        );
    }

    public MyBookingResponse toMyBookingResponse(BookingParticipantEntity participation) {
        BookingEntity booking = participation.getBooking();
        InstructorEntity instructor = booking.getInstructor();
        SpotEntity spot = booking.getSpot();
        ServiceEntity service = booking.getService();

        // Calculer le prix total
        int totalPriceCents = (service.getBasePriceCents() * booking.getDurationMinutes()) / 60;

        return new MyBookingResponse(
                // Participation
                participation.getId(),
                participation.getStatus(),

                // Booking
                booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getDurationMinutes(),
                booking.getStatus(),
                booking.getNotes(),

                // Instructor
                instructor.getId(),
                instructor.getFirstName(),
                instructor.getLastName(),

                // Spot
                spot.getId(),
                spot.getName(),
                spot.getAddress(),
                spot.getCity(),

                // Service
                service.getId(),
                service.getName(),
                service.getBasePriceCents(),

                // Prix calculé
                totalPriceCents
        );
    }
}