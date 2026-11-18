package com.soskate.api.services.availability;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * What a change to one day of availabilities implies, computed before anything is written so that
 * the instructor can be warned first.
 *
 * @param bookingsToCancel sessions with at least one removed block: a lesson is never shortened, so they
 *                         are cancelled entirely, and their other blocks become free
 * @param blocksToDelete   existing blocks to delete: the removed ones and the leftovers
 * @param alsoRemoved      what the change would leave but no lesson could fit in, so it goes too
 */
public record AvailabilityChangePlan(
        Set<Long> bookingsToCancel,
        Set<LocalDateTime> blocksToDelete,
        List<AgendaRange> alsoRemoved
) {

    public static AvailabilityChangePlan of(
            List<AgendaBlock> dayBlocks,
            Collection<LocalDateTime> removed,
            Collection<LocalDateTime> added
    ) {
        Set<LocalDateTime> removedBlocks = new HashSet<>(removed);

        Set<Long> bookingsToCancel = dayBlocks.stream()
                .filter(block -> !block.isFree() && removedBlocks.contains(block.startAt()))
                .map(AgendaBlock::bookingId)
                .collect(Collectors.toSet());

        Set<LocalDateTime> existing = dayBlocks.stream()
                .map(AgendaBlock::startAt)
                .collect(Collectors.toSet());

        Set<LocalDateTime> remaining = new HashSet<>(existing);
        remaining.addAll(added);
        remaining.removeAll(removedBlocks);

        List<AgendaRange> tooShort = AvailabilityGrid.toRanges(remaining).stream()
                .filter(range -> range.isShorterThan(AvailabilityGrid.MINIMUM_RANGE_MINUTES))
                .toList();

        Set<LocalDateTime> blocksToDelete = existing.stream()
                .filter(block -> removedBlocks.contains(block)
                        || tooShort.stream().anyMatch(range -> range.contains(block)))
                .collect(Collectors.toSet());

        return new AvailabilityChangePlan(bookingsToCancel, blocksToDelete, tooShort);
    }

    /**
     * The blocks a change may touch: past blocks are kept, and a lesson that has started is never cancelled.
     */
    public static List<AgendaBlock> changeableBlocks(List<AgendaBlock> dayBlocks, LocalDateTime now) {
        Set<Long> startedBookings = dayBlocks.stream()
                .filter(block -> !block.isFree() && block.startAt().isBefore(now))
                .map(AgendaBlock::bookingId)
                .collect(Collectors.toSet());

        return dayBlocks.stream()
                .filter(block -> !block.startAt().isBefore(now) && !startedBookings.contains(block.bookingId()))
                .toList();
    }

    /**
     * Whether some of these blocks end up in a range too short to keep.
     */
    public boolean cutsShort(Collection<LocalDateTime> blocks) {
        return alsoRemoved.stream().anyMatch(range -> blocks.stream().anyMatch(range::contains));
    }

    public boolean needsConfirmation() {
        return !bookingsToCancel.isEmpty() || !alsoRemoved.isEmpty();
    }
}