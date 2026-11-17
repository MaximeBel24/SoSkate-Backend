package com.soskate.api.services.availability;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookableDayTest {

    private static final LocalDate DAY = LocalDate.of(2027, 3, 10);
    private static final long LESSON_ID = 7L;

    @Test
    void lessonsStartEveryHalfHourAndFitInTheRange() {
        BookableDay day = new BookableDay(free(at(14, 0), at(16, 0)), List.of());

        assertThat(day.lessonStarts(60)).containsExactly(at(14, 0), at(14, 30), at(15, 0));
    }

    @Test
    void aRangeStartingAtAQuarterOffersTheNextHalfHour() {
        BookableDay day = new BookableDay(free(at(14, 15), at(16, 0)), List.of());

        assertThat(day.lessonStarts(60)).containsExactly(at(14, 30), at(15, 0));
    }

    @Test
    void aLessonDoesNotSpanTwoRanges() {
        List<AgendaBlock> blocks = new ArrayList<>(free(at(10, 0), at(11, 0)));
        blocks.addAll(free(at(11, 15), at(12, 15)));
        BookableDay day = new BookableDay(blocks, List.of());

        assertThat(day.lessonStarts(90)).isEmpty();
        assertThat(day.isOnOffer(at(10, 30), 90)).isFalse();
    }

    @Test
    void takenBlocksStayOnOfferButCannotHostALesson() {
        BookableDay day = new BookableDay(taken(at(14, 0), at(15, 0)), List.of());

        assertThat(day.lessonStarts(60)).containsExactly(at(14, 0));
        assertThat(day.isOnOffer(at(14, 0), 60)).isTrue();
        assertThat(day.canHost(at(14, 0), 60)).isFalse();
    }

    @Test
    void aClosedDayOffersNothing() {
        BookableDay day = BookableDay.closed();

        assertThat(day.lessonStarts(60)).isEmpty();
        assertThat(day.isOnOffer(at(14, 0), 60)).isFalse();
        assertThat(day.canHost(at(14, 0), 60)).isFalse();
    }

    @Test
    void aLessonNeedsEveryBlockFree() {
        List<AgendaBlock> blocks = new ArrayList<>(free(at(14, 0), at(15, 0)));
        blocks.addAll(taken(at(15, 0), at(16, 0)));
        BookableDay day = new BookableDay(blocks, List.of());

        assertThat(day.canHost(at(14, 0), 60)).isTrue();
        assertThat(day.canHost(at(14, 30), 60)).isFalse();
    }

    @Test
    void aLessonRunningPastTheRangeIsNotOnOffer() {
        BookableDay day = new BookableDay(free(at(14, 0), at(15, 0)), List.of());

        assertThat(day.isOnOffer(at(14, 30), 60)).isFalse();
        assertThat(day.canHost(at(14, 30), 60)).isFalse();
    }

    @Test
    void aLessonMustStartOnTheHourOrHalfPast() {
        BookableDay day = new BookableDay(free(at(14, 0), at(16, 0)), List.of());

        assertThat(day.canHost(at(14, 30), 60)).isTrue();
        assertThat(day.canHost(at(14, 15), 60)).isFalse();
    }

    @Test
    void aBusyTimeBlocksEveryLessonOverlappingIt() {
        List<AgendaRange> busy = List.of(new AgendaRange(at(13, 45), at(15, 15)));
        BookableDay day = new BookableDay(free(at(10, 0), at(18, 0)), busy);

        assertThat(day.canHost(at(12, 30), 60)).isTrue();
        assertThat(day.canHost(at(13, 0), 60)).isFalse();
        assertThat(day.canHost(at(15, 0), 60)).isFalse();
        assertThat(day.canHost(at(15, 30), 60)).isTrue();
    }

    @Test
    void aLessonCanTouchABusyTime() {
        List<AgendaRange> busy = List.of(new AgendaRange(at(14, 0), at(15, 0)));
        BookableDay day = new BookableDay(free(at(10, 0), at(18, 0)), busy);

        assertThat(day.canHost(at(13, 0), 60)).isTrue();
        assertThat(day.canHost(at(15, 0), 60)).isTrue();
    }

    private static List<AgendaBlock> free(LocalDateTime start, LocalDateTime end) {
        return AvailabilityGrid.blocksBetween(start, end).stream()
                .map(block -> new AgendaBlock(block, null))
                .toList();
    }

    private static List<AgendaBlock> taken(LocalDateTime start, LocalDateTime end) {
        return AvailabilityGrid.blocksBetween(start, end).stream()
                .map(block -> new AgendaBlock(block, LESSON_ID))
                .toList();
    }

    private static LocalDateTime at(int hour, int minute) {
        return DAY.atTime(hour, minute);
    }
}
