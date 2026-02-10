package com.soskate.api.services.availability;

import com.soskate.api.dto.availability.AvailabilityCreateRequest;
import com.soskate.api.dto.availability.AvailabilityResponse;
import com.soskate.api.dto.availability.AvailabilityUpdateRequest;
import com.soskate.api.dto.availability.AvailableSlotResponse;
import com.soskate.api.entities.AvailabilityEntity;
import com.soskate.api.entities.BookingEntity;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.enums.AvailabilityStatus;

import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.booking.AvailabilityNotFoundException;
import com.soskate.api.exceptions.booking.AvailabilityOverlapException;
import com.soskate.api.exceptions.booking.BookingException;
import com.soskate.api.exceptions.instructor.InstructorNotFoundException;
import com.soskate.api.mappers.AvailabilityMapper;
import com.soskate.api.repositories.AvailabilityRepository;
import com.soskate.api.repositories.BookingRepository;
import com.soskate.api.repositories.InstructorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of AvailabilityService.
 * Manages instructor availability slots.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final InstructorRepository instructorRepository;
    private final BookingRepository bookingRepository;
    private final AvailabilityMapper availabilityMapper;

    @Transactional
    public AvailabilityResponse createAvailability(Long instructorId, AvailabilityCreateRequest request) {
        log.info("Creating availability for instructor {}: {} from {} to {}",
                instructorId, request.date(), request.startTime(), request.endTime());
        InstructorEntity instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException("Instructor not found"));

        if (instructor.getStatus() != InstructorStatus.ACTIVE) {
            throw new BookingException("Instructor must be active to create an availability");
        }

        // Check for overlap
        boolean hasOverlap = availabilityRepository.existsOverlapping(
                instructorId,
                request.date(),
                request.startTime(),
                request.endTime()
        );

        if (hasOverlap) {
            log.warn("Overlap detected for instructor {} on {} from {} to {}",
                    instructorId, request.date(), request.startTime(), request.endTime());
            throw new AvailabilityOverlapException();
        }

        AvailabilityEntity availability = AvailabilityEntity.builder()
                .instructor(instructor)
                .date(request.date())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(AvailabilityStatus.AVAILABLE)
                .build();

        AvailabilityEntity saved = availabilityRepository.save(availability);
        log.info("Availability created successfully: ID {}", saved.getId());
        return availabilityMapper.toResponse(saved);
    }

    public List<AvailabilityResponse> getAvailabilityByInstructor(Long instructorId) {
        log.debug("Retrieving availabilities for instructor {}", instructorId);
        List<AvailabilityEntity> availabilities = availabilityRepository
                .findByInstructorIdAndStatus(instructorId, AvailabilityStatus.AVAILABLE);
        return availabilityMapper.toResponseList(availabilities);
    }

    public List<AvailabilityResponse> getAvailabilityByInstructorAndDateRange(
            Long instructorId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        log.debug("Retrieving availabilities for instructor {} from {} to {}", instructorId, startDate, endDate);
        List<AvailabilityEntity> availabilities = availabilityRepository.findAvailableByInstructorAndDateRange(instructorId, startDate, endDate);
        return availabilityMapper.toResponseList(availabilities);
    }

    @Transactional
    public AvailabilityResponse updateAvailability(Long instructorId, Long id, AvailabilityUpdateRequest request) {
        log.info("Updating availability {} for instructor {}", id, instructorId);
        AvailabilityEntity availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new AvailabilityNotFoundException("Availability not found"));

        if (!availability.getInstructor().getId().equals(instructorId)) {
            throw new AvailabilityNotFoundException("Availability not found");
        }

        if (request.date() != null) {
            availability.setDate(request.date());
        }
        if (request.startTime() != null) {
            availability.setStartTime(request.startTime());
        }
        if (request.endTime() != null) {
            availability.setEndTime(request.endTime());
        }

        AvailabilityEntity saved = availabilityRepository.save(availability);
        return availabilityMapper.toResponse(saved);
    }

    @Transactional
    public void deleteAvailability(Long instructorId, Long id) {
        log.info("Deleting availability {} for instructor {}", id, instructorId);
        AvailabilityEntity availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new AvailabilityNotFoundException("Availability not found"));

        if (!availability.getInstructor().getId().equals(instructorId)) {
            throw new AvailabilityNotFoundException("Availability not found");
        }

        availability.setStatus(AvailabilityStatus.CANCELLED);
        availabilityRepository.save(availability);
    }

    @Transactional(readOnly = true)
    public List<AvailableSlotResponse> getAvailableSlots(
            Long instructorId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        log.debug("Calculating free slots for instructor {} from {} to {}", instructorId, startDate, endDate);
        // 1. Retrieve availabilities
        List<AvailabilityEntity> availabilities = availabilityRepository.findAvailableByInstructorAndDateRange(instructorId, startDate, endDate);

        // 2. Retrieve bookings
        List<BookingEntity> bookings = bookingRepository
                .findByInstructorIdAndStartTimeBetween(
                        instructorId,
                        startDate.atStartOfDay(),
                        endDate.plusDays(1).atStartOfDay()
                );

        // 3. Calculate actually available slots
        List<AvailableSlotResponse> slots = new ArrayList<>();

        for (AvailabilityEntity availability : availabilities) {
            slots.addAll(splitAvailability(availability, bookings));
        }

        return slots;
    }

    private List<AvailableSlotResponse> splitAvailability(
            AvailabilityEntity availability,
            List<BookingEntity> allBookings
    ) {
        List<AvailableSlotResponse> result = new ArrayList<>();
        LocalDate date = availability.getDate();
        LocalTime availStart = availability.getStartTime();
        LocalTime availEnd = availability.getEndTime();

        // Filter bookings that overlap with this availability
        List<BookingEntity> overlapping = allBookings.stream()
                .filter(b -> b.getStartTime().toLocalDate().equals(date))
                .filter(b -> {
                    LocalTime bStart = b.getStartTime().toLocalTime();
                    LocalTime bEnd = b.getEndTime().toLocalTime();
                    return bStart.isBefore(availEnd) && bEnd.isAfter(availStart);
                })
                .sorted(Comparator.comparing(b -> b.getStartTime().toLocalTime()))
                .toList();

        if (overlapping.isEmpty()) {
            result.add(new AvailableSlotResponse(availability.getId(), date, availStart, availEnd));
            return result;
        }

        LocalTime cursor = availStart;
        for (BookingEntity booking : overlapping) {
            LocalTime bStart = booking.getStartTime().toLocalTime();
            LocalTime bEnd = booking.getEndTime().toLocalTime();

            if (cursor.isBefore(bStart)) {
                result.add(new AvailableSlotResponse(availability.getId(), date, cursor, bStart));
            }
            if (bEnd.isAfter(cursor)) {
                cursor = bEnd;
            }
        }

        if (cursor.isBefore(availEnd)) {
            result.add(new AvailableSlotResponse(availability.getId(), date, cursor, availEnd));
        }

        return result;
    }
}