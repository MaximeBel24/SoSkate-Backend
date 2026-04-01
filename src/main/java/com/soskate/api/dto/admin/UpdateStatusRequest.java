package com.soskate.api.dto.admin;

import com.soskate.api.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull(message = "Le status est requis")
        BookingStatus status
) {
}
