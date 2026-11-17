package com.soskate.api.services.availability;

import com.soskate.api.dto.availability.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Manages instructor availabilities.
 */
public interface AvailabilityService {

    /**
     * An instructor's ranges over a period, both dates included.
     * By default, from today to the end of the open period.
     */
    List<AvailabilityRangeResponse> getRanges(Long instructorId, LocalDate from, LocalDate to);

    /**
     * Adds the same hours on one or several days. Only the missing blocks are created.
     */
    AvailabilityAddResponse add(Long instructorId, AvailabilityAddRequest request);

    /**
     * Repeats a week's schedule on the following weeks. Only the missing blocks are created.
     */
    AvailabilityAddResponse copyWeek(Long instructorId, AvailabilityCopyWeekRequest request);

    /**
     * Changes the hours of a range on one day.
     */
    AvailabilityChangeResponse update(Long instructorId, AvailabilityUpdateRequest request);

    /**
     * Removes the same hours on one or several days.
     */
    AvailabilityChangeResponse remove(Long instructorId, AvailabilityRemovalRequest request);
}
