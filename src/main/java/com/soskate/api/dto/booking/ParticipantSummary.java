package com.soskate.api.dto.booking;

import com.soskate.api.enums.InvitedBy;
import com.soskate.api.enums.ParticipantStatus;

public record ParticipantSummary(
        Long id,
        Long customerId,
        String customerName,
        Integer numberOfParticipants,
        ParticipantStatus status,
        Integer amountCents,
        InvitedBy invitedBy,
        String participantsNotes
) {}