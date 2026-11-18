package com.soskate.api.services.availability;

import com.soskate.api.dto.booking.AvailableSlotsResponse;
import com.soskate.api.dto.booking.TimeSlotResponse;
import com.soskate.api.entities.BookingEntity;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.entities.SpotEntity;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.instructor.InstructorNotFoundException;
import com.soskate.api.exceptions.spot.SpotNotFoundException;
import com.soskate.api.repositories.AvailabilityBlockRepository;
import com.soskate.api.repositories.BookingRepository;
import com.soskate.api.repositories.InstructorRepository;
import com.soskate.api.repositories.InstructorSpotRepository;
import com.soskate.api.repositories.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of SlotCalculationService.
 * Calculates bookable slots from the instructor's blocks and existing bookings.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SlotCalculationServiceImpl implements SlotCalculationService {

    private final AvailabilityBlockRepository blockRepository;
    private final BookingRepository bookingRepository;
    private final InstructorRepository instructorRepository;
    private final InstructorSpotRepository instructorSpotRepository;
    private final SpotRepository spotRepository;
    private final BufferCalculationService bufferCalculationService;
    private final AgendaRules rules;

    @Override
    @Transactional(readOnly = true)
    public AvailableSlotsResponse calculateAvailableSlots(
            Long instructorId,
            Long spotId,
            LocalDate date,
            Integer durationMinutes
    ) {
        log.debug("Calculating available slots: instructor={}, spot={}, date={}, duration={}min",
                instructorId, spotId, date, durationMinutes);
        rules.requireValidDuration(durationMinutes);

        InstructorEntity instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException(instructorId));
        SpotEntity spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new SpotNotFoundException("Spot not found"));

        BookableDay day = bookableDay(instructor, spot, date);

        List<TimeSlotResponse> slots = day.lessonStarts(durationMinutes).stream()
                .map(start -> new TimeSlotResponse(
                        start.toLocalTime(),
                        start.plusMinutes(durationMinutes).toLocalTime(),
                        day.canHost(start, durationMinutes)
                ))
                .toList();

        log.debug("{} slot(s) calculated for instructor {} on {}", slots.size(), instructorId, date);
        return new AvailableSlotsResponse(
                instructorId,
                spotId,
                date,
                durationMinutes,
                slots
        );
    }

    @Override
    public BookableDay bookableDay(InstructorEntity instructor, SpotEntity spot, LocalDate date) {
        boolean bookable = instructor.getStatus() == InstructorStatus.ACTIVE
                && instructorSpotRepository.existsByInstructorIdAndSpotId(instructor.getId(), spot.getId())
                && rules.isOpenDate(date);
        if (!bookable) {
            return BookableDay.closed();
        }

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime nextDayStart = date.plusDays(1).atStartOfDay();

        List<AgendaBlock> blocks = blockRepository.findAgendaBlocks(instructor.getId(), dayStart, nextDayStart).stream()
                .filter(block -> rules.isWithinOpeningHours(block.startAt()))
                .toList();

        List<AgendaRange> busyTimes = bookingRepository
                .findByInstructorIdAndStartTimeBetween(instructor.getId(), dayStart, nextDayStart).stream()
                .map(lesson -> withTravelTime(lesson, spot))
                .toList();

        return new BookableDay(blocks, busyTimes);
    }

    private AgendaRange withTravelTime(BookingEntity lesson, SpotEntity requestedSpot) {
        int travelMinutes = bufferCalculationService.calculateBuffer(lesson.getSpot(), requestedSpot);
        return new AgendaRange(
                lesson.getStartTime().minusMinutes(travelMinutes),
                lesson.getEndTime().plusMinutes(travelMinutes)
        );
    }
}
