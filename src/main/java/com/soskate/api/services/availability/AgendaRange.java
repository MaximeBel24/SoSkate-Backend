package com.soskate.api.services.availability;

import java.time.LocalDateTime;

/**
 * A continuous period of local time, start included and end excluded.
 * Lessons take place during the day, so a range never crosses a clock change.
 */
public record AgendaRange(
        LocalDateTime start,
        LocalDateTime end
) {

    public boolean isShorterThan(int minutes) {
        return start.plusMinutes(minutes).isAfter(end);
    }

    public boolean contains(LocalDateTime time) {
        return !time.isBefore(start) && time.isBefore(end);
    }
}