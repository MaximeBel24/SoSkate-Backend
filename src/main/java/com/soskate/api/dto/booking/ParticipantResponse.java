package com.soskate.api.dto.booking;

import com.soskate.api.dto.customer.CustomerSummary;
import com.soskate.api.enums.InvitedBy;
import com.soskate.api.enums.ParticipantStatus;
import java.time.LocalDateTime;

public record ParticipantResponse(
        Long id,
        Long bookingId,
        CustomerSummary customer,
        Integer numberOfParticipants,
        ParticipantStatus status,
        Integer amountCents,
        String paymentIntentId,
        InvitedBy invitedBy,
        CustomerSummary invitedByCustomer,
        String participantsNotes,
        String cancellationReason,
        LocalDateTime cancelledAt,
        LocalDateTime createdAt
) {}