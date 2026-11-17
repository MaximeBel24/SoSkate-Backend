package com.soskate.api.services.availability;

import com.soskate.api.entities.PlatformSettingsEntity;
import com.soskate.api.exceptions.booking.BookingException;
import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;
import com.soskate.api.repositories.PlatformSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * The rules of the agenda, and the only place where it reads the current time.
 */
@Component
@RequiredArgsConstructor
public class AgendaRules {

    private static final Set<Integer> LESSON_DURATIONS = Set.of(60, 90, 120, 180, 240);

    private final PlatformSettingsRepository settingsRepository;
    private final Clock clock;

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }

    /**
     * Counted in calendar days: on Monday, at any hour, the first open day is Thursday.
     */
    public LocalDate firstOpenDate() {
        return today().plusDays(settings().getMinDaysInAdvance());
    }

    public LocalDate lastOpenDate() {
        return today().plusMonths(settings().getAvailabilityHorizonMonths());
    }

    public boolean isOpenDate(LocalDate date) {
        return !date.isBefore(firstOpenDate()) && !date.isAfter(lastOpenDate());
    }

    public boolean isWithinOpeningHours(LocalDateTime block) {
        LocalTime time = block.toLocalTime();
        return !time.isBefore(settings().getOpeningTime()) && time.isBefore(settings().getClosingTime());
    }

    public void requireValidRange(LocalTime start, LocalTime end) {
        if (!AvailabilityGrid.isAligned(start) || !AvailabilityGrid.isAligned(end)) {
            throw new ApiException(ErrorCode.INVALID_AVAILABILITY, "Times must fall on a quarter of an hour");
        }
        if (!start.isBefore(end)) {
            throw new ApiException(ErrorCode.INVALID_AVAILABILITY, "The start time must be before the end time");
        }
    }

    public void requireWithinOpeningHours(LocalTime start, LocalTime end) {
        PlatformSettingsEntity settings = settings();
        if (start.isBefore(settings.getOpeningTime()) || end.isAfter(settings.getClosingTime())) {
            throw new ApiException(
                    ErrorCode.INVALID_AVAILABILITY,
                    "Lessons take place between " + settings.getOpeningTime() + " and " + settings.getClosingTime()
            );
        }
    }

    public void requireValidDuration(int durationMinutes) {
        if (!LESSON_DURATIONS.contains(durationMinutes)) {
            throw new BookingException("Invalid duration. Allowed values: 60, 90, 120, 180, 240 minutes");
        }
    }

    private PlatformSettingsEntity settings() {
        return settingsRepository.getSettings();
    }
}
