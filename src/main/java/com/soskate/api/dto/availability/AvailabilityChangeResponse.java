package com.soskate.api.dto.availability;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @param applied         false when nothing was written because the change needs a confirmation
 * @param coursesToCancel lessons this change cancels entirely
 * @param alsoRemoved     what the change would leave but no lesson could fit in, so it goes too
 */
public record AvailabilityChangeResponse(
        boolean applied,
        List<CourseToCancel> coursesToCancel,
        List<AvailabilityRangeResponse> alsoRemoved
) {

    public record CourseToCancel(
            Long bookingId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String spotName
    ) {}
}
