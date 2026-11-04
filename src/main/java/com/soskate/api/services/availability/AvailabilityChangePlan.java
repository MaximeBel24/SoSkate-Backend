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
                .filter(range -> range.minutes() < AvailabilityGrid.MINIMUM_RANGE_MINUTES)
                .toList();

        Set<LocalDateTime> blocksToDelete = existing.stream()
                .filter(block -> removedBlocks.contains(block)
                        || tooShort.stream().anyMatch(range -> range.contains(block)))
                .collect(Collectors.toSet());

        return new AvailabilityChangePlan(bookingsToCancel, blocksToDelete, tooShort);
    }

    public boolean needsConfirmation() {
        return !bookingsToCancel.isEmpty() || !alsoRemoved.isEmpty();
    }
}