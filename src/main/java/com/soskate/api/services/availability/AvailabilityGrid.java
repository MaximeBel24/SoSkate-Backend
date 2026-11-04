package com.soskate.api.services.availability;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * The 15-minute grid on which availabilities are stored.
 * Instructors and clients see ranges; only the database sees blocks.
 */
public final class AvailabilityGrid {

    public static final int BLOCK_MINUTES = 15;

    /** The shortest lesson lasts one hour, so a shorter availability could never be booked. */
    public static final int MINIMUM_RANGE_MINUTES = 60;

    private AvailabilityGrid() {
    }

    /**
     * Start of every block between two times, end excluded.
     */
    public static List<LocalDateTime> blocksBetween(LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> blocks = new ArrayList<>();
        for (LocalDateTime block = start; block.isBefore(end); block = block.plusMinutes(BLOCK_MINUTES)) {
            blocks.add(block);
        }
        return blocks;
    }

    /**
     * Merges blocks that follow each other into ranges, in chronological order.
     */
    public static List<AgendaRange> toRanges(Collection<LocalDateTime> blocks) {
        List<AgendaRange> ranges = new ArrayList<>();
        LocalDateTime rangeStart = null;
        LocalDateTime rangeEnd = null;
        for (LocalDateTime block : new TreeSet<>(blocks)) {
            if (!block.equals(rangeEnd)) {
                if (rangeStart != null) {
                    ranges.add(new AgendaRange(rangeStart, rangeEnd));
                }
                rangeStart = block;
            }
            rangeEnd = block.plusMinutes(BLOCK_MINUTES);
        }
        if (rangeStart != null) {
            ranges.add(new AgendaRange(rangeStart, rangeEnd));
        }
        return ranges;
    }
}