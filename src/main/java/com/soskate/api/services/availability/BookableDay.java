package com.soskate.api.services.availability;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * What can be booked on one day, for one spot. The slots shown to a client and the check of a booking
 * both come from here, so that a slot shown as free cannot be refused afterwards.
 *
 * @param blocks    the blocks on offer, free or taken
 * @param busyTimes the lessons of the day, widened by the travel time to and from the requested spot
 */
public record BookableDay(
        List<AgendaBlock> blocks,
        List<AgendaRange> busyTimes
) {

    /**
     * A day on which nothing can be booked, whatever the blocks say.
     */
    public static BookableDay closed() {
        return new BookableDay(List.of(), List.of());
    }

    /**
     * Where a lesson of this duration could start, whether or not it is still free.
     */
    public List<LocalDateTime> lessonStarts(int durationMinutes) {
        List<LocalDateTime> starts = new ArrayList<>();
        for (AgendaRange range : AvailabilityGrid.toRanges(blockTimes())) {
            LocalDateTime start = AvailabilityGrid.nextLessonStart(range.start());
            while (!start.plusMinutes(durationMinutes).isAfter(range.end())) {
                starts.add(start);
                start = start.plusMinutes(AvailabilityGrid.LESSON_START_MINUTES);
            }
        }
        return starts;
    }

    /**
     * Whether the instructor offers the whole lesson, even if it is no longer free.
     */
    public boolean isOnOffer(LocalDateTime start, int durationMinutes) {
        return blockTimes().containsAll(AvailabilityGrid.blocksBetween(start, start.plusMinutes(durationMinutes)));
    }

    public boolean canHost(LocalDateTime start, int durationMinutes) {
        LocalDateTime end = start.plusMinutes(durationMinutes);
        AgendaRange lesson = new AgendaRange(start, end);
        Set<LocalDateTime> freeBlocks = blocks.stream()
                .filter(AgendaBlock::isFree)
                .map(AgendaBlock::startAt)
                .collect(Collectors.toSet());

        return AvailabilityGrid.isLessonStart(start.toLocalTime())
                && freeBlocks.containsAll(AvailabilityGrid.blocksBetween(start, end))
                && busyTimes.stream().noneMatch(lesson::overlaps);
    }

    private Set<LocalDateTime> blockTimes() {
        return blocks.stream().map(AgendaBlock::startAt).collect(Collectors.toSet());
    }
}
