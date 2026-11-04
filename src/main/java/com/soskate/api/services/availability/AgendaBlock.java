package com.soskate.api.services.availability;

import java.time.LocalDateTime;

/**
 * A stored 15-minute availability block.
 *
 * @param startAt   start of the block
 * @param bookingId the session occupying the block, null while it is free
 */
public record AgendaBlock(
        LocalDateTime startAt,
        Long bookingId
) {

    public boolean isFree() {
        return bookingId == null;
    }
}
