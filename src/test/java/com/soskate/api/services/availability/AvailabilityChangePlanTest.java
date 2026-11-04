package com.soskate.api.services.availability;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AvailabilityChangePlanTest {

    private static final LocalDate DAY = LocalDate.of(2027, 3, 10);
    private static final Long LESSON = 7L;

    @Test
    void anAbsenceInTheMiddleDeletesOnlyItsBlocks() {
        List<AgendaBlock> day = free(at(14, 0), at(19, 0));

        AvailabilityChangePlan plan = remove(day, at(16, 0), at(17, 0));

        assertThat(plan.blocksToDelete()).containsExactlyInAnyOrderElementsOf(blocks(at(16, 0), at(17, 0)));
        assertThat(plan.needsConfirmation()).isFalse();
    }

    @Test
    void aBlockThatDoesNotExistIsIgnored() {
        List<AgendaBlock> day = free(at(14, 0), at(16, 0));

        AvailabilityChangePlan plan = remove(day, at(10, 0), at(12, 0));

        assertThat(plan.blocksToDelete()).isEmpty();
        assertThat(plan.needsConfirmation()).isFalse();
    }

    @Test
    void aLeftoverOfLessThanOneHourGoesToo() {
        List<AgendaBlock> day = free(at(14, 0), at(18, 0));

        AvailabilityChangePlan plan = remove(day, at(14, 30), at(18, 0));

        assertThat(plan.alsoRemoved()).containsExactly(new AgendaRange(at(14, 0), at(14, 30)));
        assertThat(plan.blocksToDelete()).containsExactlyInAnyOrderElementsOf(blocks(at(14, 0), at(18, 0)));
        assertThat(plan.needsConfirmation()).isTrue();
    }

    @Test
    void aLeftoverOfExactlyOneHourStays() {
        List<AgendaBlock> day = free(at(14, 0), at(18, 0));

        AvailabilityChangePlan plan = remove(day, at(15, 0), at(18, 0));

        assertThat(plan.alsoRemoved()).isEmpty();
        assertThat(plan.blocksToDelete()).containsExactlyInAnyOrderElementsOf(blocks(at(15, 0), at(18, 0)));
    }

    @Test
    void bothSidesOfAnAbsenceCanBeTooShort() {
        List<AgendaBlock> day = free(at(14, 0), at(15, 30));

        AvailabilityChangePlan plan = remove(day, at(14, 30), at(15, 0));

        assertThat(plan.alsoRemoved()).containsExactly(
                new AgendaRange(at(14, 0), at(14, 30)),
                new AgendaRange(at(15, 0), at(15, 30))
        );
        assertThat(plan.blocksToDelete()).containsExactlyInAnyOrderElementsOf(blocks(at(14, 0), at(15, 30)));
    }

    @Test
    void anAbsenceTouchingALessonCancelsItEntirely() {
        // Available 15:00-19:00, lesson 15:30-16:30, absence from 16:00: the lesson is not shortened.
        List<AgendaBlock> day = new ArrayList<>(free(at(15, 0), at(15, 30)));
        day.addAll(booked(at(15, 30), at(16, 30)));
        day.addAll(free(at(16, 30), at(19, 0)));

        AvailabilityChangePlan plan = remove(day, at(16, 0), at(19, 0));

        assertThat(plan.bookingsToCancel()).containsExactly(LESSON);
        assertThat(plan.needsConfirmation()).isTrue();
    }

    @Test
    void theFreedPartOfACancelledLessonStaysIfLongEnough() {
        List<AgendaBlock> day = new ArrayList<>(free(at(14, 0), at(15, 30)));
        day.addAll(booked(at(15, 30), at(16, 30)));

        AvailabilityChangePlan plan = remove(day, at(16, 0), at(16, 30));

        // 14:00-16:00 stays: it now includes the first half hour of the cancelled lesson.
        assertThat(plan.bookingsToCancel()).containsExactly(LESSON);
        assertThat(plan.alsoRemoved()).isEmpty();
        assertThat(plan.blocksToDelete()).containsExactlyInAnyOrderElementsOf(blocks(at(16, 0), at(16, 30)));
    }

    @Test
    void anAbsenceNextToALessonLeavesItAlone() {
        List<AgendaBlock> day = new ArrayList<>(booked(at(14, 0), at(15, 0)));
        day.addAll(free(at(15, 0), at(18, 0)));

        AvailabilityChangePlan plan = remove(day, at(15, 0), at(18, 0));

        assertThat(plan.bookingsToCancel()).isEmpty();
        assertThat(plan.alsoRemoved()).isEmpty();
    }

    @Test
    void addedBlocksCanSaveWhatWouldBeALeftover() {
        // 14:00-16:00 moved to 13:30-14:30.
        List<AgendaBlock> day = free(at(14, 0), at(16, 0));

        AvailabilityChangePlan plan = AvailabilityChangePlan.of(
                day,
                blocks(at(14, 30), at(16, 0)),
                blocks(at(13, 30), at(14, 0))
        );

        assertThat(plan.alsoRemoved()).isEmpty();
        assertThat(plan.blocksToDelete()).containsExactlyInAnyOrderElementsOf(blocks(at(14, 30), at(16, 0)));
    }

    private static AvailabilityChangePlan remove(List<AgendaBlock> day, LocalDateTime from, LocalDateTime to) {
        return AvailabilityChangePlan.of(day, blocks(from, to), List.of());
    }

    private static List<LocalDateTime> blocks(LocalDateTime from, LocalDateTime to) {
        return AvailabilityGrid.blocksBetween(from, to);
    }

    private static List<AgendaBlock> free(LocalDateTime from, LocalDateTime to) {
        return blocks(from, to).stream().map(start -> new AgendaBlock(start, null)).toList();
    }

    private static List<AgendaBlock> booked(LocalDateTime from, LocalDateTime to) {
        return blocks(from, to).stream().map(start -> new AgendaBlock(start, LESSON)).toList();
    }

    private static LocalDateTime at(int hour, int minute) {
        return DAY.atTime(hour, minute);
    }
}