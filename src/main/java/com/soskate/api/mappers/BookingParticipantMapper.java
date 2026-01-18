package com.soskate.api.mappers;

import com.soskate.api.dto.customer.CustomerSummary; // ← Utilise ton existant
import com.soskate.api.dto.booking.ParticipantResponse;
import com.soskate.api.entities.BookingParticipantEntity;
import com.soskate.api.entities.CustomerEntity;
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
                entity.getFirstname(),
                entity.getLastname(),
                entity.getEmail()
        );
    }
}