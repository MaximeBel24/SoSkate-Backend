package com.soskate.api.services.availability;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AvailabilityGridTest {

    private static final LocalDate DAY = LocalDate.of(2027, 3, 10);

    @Test
    void oneHourIsFourBlocksWithoutItsEnd() {
        assertThat(AvailabilityGrid.blocksBetween(at(14, 0), at(15, 0)))
                .containsExactly(at(14, 0), at(14, 15), at(14, 30), at(14, 45));
    }

    @Test
    void consecutiveBlocksMergeIntoOneRange() {
        List<LocalDateTime> blocks = AvailabilityGrid.blocksBetween(at(14, 0), at(15, 0));

        assertThat(AvailabilityGrid.toRanges(blocks))
                .containsExactly(new AgendaRange(at(14, 0), at(15, 0)));
    }

    @Test
    void aGapSeparatesTwoRangesWhateverTheOrder() {
        List<LocalDateTime> blocks = new ArrayList<>(AvailabilityGrid.blocksBetween(at(17, 0), at(18, 0)));
        blocks.addAll(AvailabilityGrid.blocksBetween(at(14, 0), at(16, 0)));

        assertThat(AvailabilityGrid.toRanges(blocks)).containsExactly(
                new AgendaRange(at(14, 0), at(16, 0)),
                new AgendaRange(at(17, 0), at(18, 0))
        );
    }

    @Test
    void noBlockMeansNoRange() {
        assertThat(AvailabilityGrid.toRanges(List.of())).isEmpty();
    }

    private static LocalDateTime at(int hour, int minute) {
        return DAY.atTime(hour, minute);
    }
}