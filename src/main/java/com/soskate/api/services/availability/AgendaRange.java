package com.soskate.api.services.availability;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * A continuous period of time, start included and end excluded.
 */
public record AgendaRange(
        LocalDateTime start,
        LocalDateTime end
) {

    public long minutes() {
        return Duration.between(start, end).toMinutes();
    }

    public boolean contains(LocalDateTime time) {
        return !time.isBefore(start) && time.isBefore(end);
    }
}
