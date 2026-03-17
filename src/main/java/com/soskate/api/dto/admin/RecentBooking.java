package com.soskate.api.dto.admin;

import com.soskate.api.enums.BookingStatus;

import java.time.LocalDateTime;

public record RecentBooking(
        Long id,
        LocalDateTime date,
        String serviceName,
        String instructorFirstName,
        String instructorLastName,
        BookingStatus status
) {
}
