package com.soskate.api.services.availability;

import com.soskate.api.dto.availability.*;
import com.soskate.api.entities.BookingEntity;
import com.soskate.api.entities.InstructorEntity;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.exceptions.booking.AvailabilityNotFoundException;
import com.soskate.api.exceptions.booking.BookingException;
import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;
import com.soskate.api.exceptions.instructor.InstructorNotFoundException;
import com.soskate.api.repositories.AvailabilityBlockRepository;
import com.soskate.api.repositories.BookingRepository;
import com.soskate.api.repositories.InstructorRepository;
import com.soskate.api.services.booking.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Availabilities are stored as blocks but instructors and clients only ever see ranges.
 * Every write locks the instructor, so that two changes to one agenda run one after the other.
 * The lock must come before any read: MySQL freezes what a transaction sees at its first read,
 * so a read before the lock would miss what the previous change has just written.
 */
@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityBlockRepository blockRepository;
    private final InstructorRepository instructorRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final AgendaRules rules;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityRangeResponse> getRanges(Long instructorId, LocalDate from, LocalDate to) {
        LocalDate first = from != null ? from : rules.today();
        LocalDate last = to != null ? to : rules.lastOpenDate();

        List<LocalDateTime> blocks = blockRepository
                .findAgendaBlocks(instructorId, first.atStartOfDay(), last.plusDays(1).atStartOfDay())
                .stream()
                .map(AgendaBlock::startAt)
                .toList();

        return AvailabilityGrid.toRanges(blocks).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public AvailabilityAddResponse add(Long instructorId, AvailabilityAddRequest request) {
        rules.requireValidRange(request.startTime(), request.endTime());
        InstructorEntity instructor = lockActiveInstructor(instructorId);
        rules.requireWithinOpeningHours(request.startTime(), request.endTime());

        AddSummary summary = new AddSummary();
        for (LocalDate date : new TreeSet<>(request.dates())) {
            addToDay(instructor, date, blocksOf(date, request.startTime(), request.endTime()), summary);
        }
        return summary.toResponse();
    }

    @Override
    @Transactional
    public AvailabilityAddResponse copyWeek(Long instructorId, AvailabilityCopyWeekRequest request) {
        LocalDate weekStart = request.weekStart();
        if (!DayOfWeek.MONDAY.equals(weekStart.getDayOfWeek())) {
            throw new ApiException(ErrorCode.INVALID_AVAILABILITY, "A week starts on a Monday");
        }
        InstructorEntity instructor = lockActiveInstructor(instructorId);

        Map<LocalDate, List<LocalDateTime>> week = blockRepository
                .findAgendaBlocks(instructorId, weekStart.atStartOfDay(), weekStart.plusWeeks(1).atStartOfDay())
                .stream()
                .map(AgendaBlock::startAt)
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, TreeMap::new, Collectors.toList()));

        AddSummary summary = new AddSummary();
        for (int weeks = 1; weeks <= request.weeks(); weeks++) {
            for (Map.Entry<LocalDate, List<LocalDateTime>> day : week.entrySet()) {
                int offset = weeks;
                List<LocalDateTime> wanted = day.getValue().stream().map(block -> block.plusWeeks(offset)).toList();
                addToDay(instructor, day.getKey().plusWeeks(offset), wanted, summary);
            }
        }
        return summary.toResponse();
    }

    @Override
    @Transactional
    public AvailabilityChangeResponse update(Long instructorId, AvailabilityUpdateRequest request) {
        rules.requireValidRange(request.currentStartTime(), request.currentEndTime());
        rules.requireValidRange(request.newStartTime(), request.newEndTime());
        InstructorEntity instructor = lockInstructor(instructorId);
        rules.requireWithinOpeningHours(request.newStartTime(), request.newEndTime());

        LocalDate date = request.date();
        List<AgendaBlock> day = dayBlocks(instructorId, date);
        List<LocalDateTime> current = blocksOf(date, request.currentStartTime(), request.currentEndTime());
        List<LocalDateTime> wanted = blocksOf(date, request.newStartTime(), request.newEndTime());

        if (!missingBlocks(current, day).isEmpty()) {
            throw new AvailabilityNotFoundException("Availability not found");
        }

        List<LocalDateTime> added = missingBlocks(wanted, day);
        if (!added.isEmpty() && !rules.isOpenDate(date)) {
            throw new ApiException(
                    ErrorCode.INVALID_AVAILABILITY,
                    "Availabilities can be added from " + rules.firstOpenDate() + " to " + rules.lastOpenDate()
            );
        }

        List<LocalDateTime> removed = current.stream().filter(block -> !wanted.contains(block)).toList();
        AvailabilityChangePlan plan = AvailabilityChangePlan.of(changeableBlocks(day), removed, added);
        if (plan.cutsShort(wanted)) {
            throw new ApiException(ErrorCode.INVALID_AVAILABILITY, "An availability lasts at least one hour");
        }

        AvailabilityChangeResponse response = applyUnlessToConfirm(instructorId, List.of(plan), request.confirmed());
        if (response.applied()) {
            saveBlocks(instructor, added);
        }
        return response;
    }

    @Override
    @Transactional
    public AvailabilityChangeResponse remove(Long instructorId, AvailabilityRemovalRequest request) {
        rules.requireValidRange(request.startTime(), request.endTime());
        lockInstructor(instructorId);

        List<AvailabilityChangePlan> plans = new TreeSet<>(request.dates()).stream()
                .map(date -> AvailabilityChangePlan.of(
                        changeableBlocks(dayBlocks(instructorId, date)),
                        blocksOf(date, request.startTime(), request.endTime()),
                        List.of()
                ))
                .toList();

        return applyUnlessToConfirm(instructorId, plans, request.confirmed());
    }

    /**
     * A date the rules forbid is reported rather than refused, so that one bad date does not
     * block the rest of a repetition.
     */
    private void addToDay(InstructorEntity instructor, LocalDate date, List<LocalDateTime> wanted, AddSummary summary) {
        if (!rules.isOpenDate(date) || !wanted.stream().allMatch(rules::isWithinOpeningHours)) {
            summary.skippedDates.add(date);
            return;
        }

        List<AgendaBlock> day = dayBlocks(instructor.getId(), date);
        List<LocalDateTime> missing = missingBlocks(wanted, day);
        if (missing.isEmpty()) {
            summary.alreadyCoveredDates.add(date);
            return;
        }
        if (AvailabilityChangePlan.of(day, List.of(), missing).cutsShort(missing)) {
            summary.skippedDates.add(date);
            return;
        }

        saveBlocks(instructor, missing);
        summary.blocksCreated += missing.size();
    }

    /**
     * Writes nothing while a lesson would be cancelled or a leftover erased without the
     * instructor having been warned. The response says what the change implies either way.
     */
    private AvailabilityChangeResponse applyUnlessToConfirm(
            Long instructorId,
            List<AvailabilityChangePlan> plans,
            boolean confirmed
    ) {
        List<Long> bookingIds = plans.stream()
                .flatMap(plan -> plan.bookingsToCancel().stream())
                .toList();
        List<AvailabilityChangeResponse.CourseToCancel> courses = bookingRepository.findAllById(bookingIds).stream()
                .sorted(Comparator.comparing(BookingEntity::getStartTime))
                .map(this::toCourseToCancel)
                .toList();
        List<AvailabilityRangeResponse> alsoRemoved = plans.stream()
                .flatMap(plan -> plan.alsoRemoved().stream())
                .map(this::toResponse)
                .toList();

        boolean applied = confirmed || plans.stream().noneMatch(AvailabilityChangePlan::needsConfirmation);
        if (applied) {
            bookingIds.forEach(bookingId -> bookingService.cancelBooking(instructorId, bookingId));
            for (AvailabilityChangePlan plan : plans) {
                if (!plan.blocksToDelete().isEmpty()) {
                    blockRepository.deleteBlocks(instructorId, plan.blocksToDelete());
                }
            }
        }
        return new AvailabilityChangeResponse(applied, courses, alsoRemoved);
    }

    private InstructorEntity lockInstructor(Long instructorId) {
        return instructorRepository.findByIdForUpdate(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException("Instructor not found"));
    }

    private InstructorEntity lockActiveInstructor(Long instructorId) {
        InstructorEntity instructor = lockInstructor(instructorId);
        if (instructor.getStatus() != InstructorStatus.ACTIVE) {
            throw new BookingException("Instructor must be active to create an availability");
        }
        return instructor;
    }

    private List<AgendaBlock> dayBlocks(Long instructorId, LocalDate date) {
        return blockRepository.findAgendaBlocks(instructorId, date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    private List<AgendaBlock> changeableBlocks(List<AgendaBlock> day) {
        return AvailabilityChangePlan.changeableBlocks(day, rules.now());
    }

    private List<LocalDateTime> blocksOf(LocalDate date, LocalTime start, LocalTime end) {
        return AvailabilityGrid.blocksBetween(date.atTime(start), date.atTime(end));
    }

    private List<LocalDateTime> missingBlocks(List<LocalDateTime> wanted, List<AgendaBlock> day) {
        Set<LocalDateTime> existing = day.stream().map(AgendaBlock::startAt).collect(Collectors.toSet());
        return wanted.stream().filter(block -> !existing.contains(block)).toList();
    }

    /**
     * In one batch: a whole term can be thousands of blocks, and the instructor stays locked meanwhile.
     */
    private void saveBlocks(InstructorEntity instructor, List<LocalDateTime> blocks) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO availability_blocks (instructor_id, start_at) VALUES (?, ?)",
                blocks,
                blocks.size(),
                (statement, startAt) -> {
                    statement.setLong(1, instructor.getId());
                    statement.setObject(2, startAt);
                }
        );
    }

    private AvailabilityRangeResponse toResponse(AgendaRange range) {
        return new AvailabilityRangeResponse(
                range.start().toLocalDate(),
                range.start().toLocalTime(),
                range.end().toLocalTime()
        );
    }

    private AvailabilityChangeResponse.CourseToCancel toCourseToCancel(BookingEntity booking) {
        return new AvailabilityChangeResponse.CourseToCancel(
                booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getSpot().getName()
        );
    }

    private static class AddSummary {
        private int blocksCreated;
        private final List<LocalDate> alreadyCoveredDates = new ArrayList<>();
        private final List<LocalDate> skippedDates = new ArrayList<>();

        private AvailabilityAddResponse toResponse() {
            return new AvailabilityAddResponse(blocksCreated, alreadyCoveredDates, skippedDates);
        }
    }
}
