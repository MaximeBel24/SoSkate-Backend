package com.soskate.api.dto.availability;

import java.time.LocalDate;
import java.util.List;

/**
 * @param blocksCreated       number of 15-minute blocks created
 * @param alreadyCoveredDates dates on which these hours were already available
 * @param skippedDates        dates the rules forbid: outside the open period or the opening hours,
 *                            or less than one hour in a row
 */
public record AvailabilityAddResponse(
        int blocksCreated,
        List<LocalDate> alreadyCoveredDates,
        List<LocalDate> skippedDates
) {}
