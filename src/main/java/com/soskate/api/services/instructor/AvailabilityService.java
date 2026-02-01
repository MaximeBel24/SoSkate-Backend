package com.soskate.api.services.instructor;

import com.soskate.api.dto.availability.AvailabilityCreateRequest;
import com.soskate.api.dto.availability.AvailabilityResponse;
import com.soskate.api.dto.availability.AvailabilityUpdateRequest;
import com.soskate.api.dto.availability.AvailableSlotResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for instructor availability management.
 */
public interface AvailabilityService {

    /**
     * Creates a new availability slot.
     */
    AvailabilityResponse createAvailability(Long instructorId, AvailabilityCreateRequest request);

    /**
     * Gets all availabilities for an instructor.
     */
    List<AvailabilityResponse> getByInstructor(Long instructorId);

    /**
     * Gets availabilities for an instructor within a date range.
     */
    List<AvailabilityResponse> getByInstructorAndDateRange(Long instructorId, LocalDate startDate, LocalDate endDate);

    /**
     * Gets an availability by ID.
     */
    AvailabilityResponse getById(Long id);

    /**
     * Updates an availability.
     */
    AvailabilityResponse update(Long instructorId, Long id, AvailabilityUpdateRequest request);

    /**
     * Deletes (cancels) an availability.
     */
    void delete(Long instructorId, Long id);

    /**
     * Gets planning slots (availabilities minus existing bookings).
     */
    List<AvailableSlotResponse> getPlanningSlots(Long instructorId, LocalDate startDate, LocalDate endDate);
}
