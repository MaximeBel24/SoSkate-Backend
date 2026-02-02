package com.soskate.api.services.availability;

import com.soskate.api.dto.booking.AvailableSlotsResponse;
import com.soskate.api.dto.booking.TimeSlotResponse;
import com.soskate.api.entities.AvailabilityEntity;
import com.soskate.api.entities.BookingEntity;
import com.soskate.api.entities.SpotEntity;
import com.soskate.api.exceptions.spot.SpotNotFoundException;
import com.soskate.api.repositories.AvailabilityRepository;
import com.soskate.api.repositories.BookingRepository;
import com.soskate.api.repositories.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of SlotCalculationService.
 * Calculates available booking slots based on instructor availability and existing bookings.
 */
@Service
@RequiredArgsConstructor
public class SlotCalculationServiceImpl implements SlotCalculationService {

    private final AvailabilityRepository availabilityRepository;
    private final BookingRepository bookingRepository;
    private final SpotRepository spotRepository;
    private final BufferCalculationService bufferCalculationService;

    private static final int SLOT_INCREMENT_MINUTES = 30;

    @Override
    public AvailableSlotsResponse calculateAvailableSlots(
            Long instructorId,
            Long spotId,
            LocalDate date,
            Integer durationMinutes
    ) {
        SpotEntity requestedSpot = spotRepository.findById(spotId)
                .orElseThrow(() -> new SpotNotFoundException("Spot non trouvé"));

        List<AvailabilityEntity> availabilities = availabilityRepository
                .findAvailableByInstructorAndDateRange(instructorId, date, date);

        List<BookingEntity> existingBookings = bookingRepository
                .findByInstructorIdAndStartTimeBetween(
                        instructorId,
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay()
                );

        List<TimeSlotResponse> slots = new ArrayList<>();

        for (AvailabilityEntity availability : availabilities) {
            LocalTime cursor = availability.getStartTime();
            LocalTime availEnd = availability.getEndTime();

            while (!cursor.plusMinutes(durationMinutes).isAfter(availEnd)) {
                LocalTime slotStart = cursor;
                LocalTime slotEnd = cursor.plusMinutes(durationMinutes);

                boolean isFree = isSlotFree(
                        slotStart,
                        slotEnd,
                        existingBookings,
                        requestedSpot
                );

                slots.add(new TimeSlotResponse(slotStart, slotEnd, isFree));

                cursor = cursor.plusMinutes(SLOT_INCREMENT_MINUTES);
            }
        }

        return new AvailableSlotsResponse(
                instructorId,
                spotId,
                date,
                durationMinutes,
                slots
        );
    }

    private boolean isSlotFree(
            LocalTime slotStart,
            LocalTime slotEnd,
            List<BookingEntity> existingBookings,
            SpotEntity requestedSpot
    ) {
        for (BookingEntity booking : existingBookings) {
            int buffer = bufferCalculationService.calculateBuffer(booking.getSpot(), requestedSpot);

            LocalTime bookingStart = booking.getStartTime().toLocalTime();
            LocalTime bookingEnd = booking.getEndTime().toLocalTime();

            LocalTime blockedStart = bookingStart.minusMinutes(buffer);
            LocalTime blockedEnd = bookingEnd.plusMinutes(buffer);

            boolean overlaps = slotStart.isBefore(blockedEnd) && slotEnd.isAfter(blockedStart);

            if (overlaps) {
                return false;
            }
        }
        return true;
    }
}