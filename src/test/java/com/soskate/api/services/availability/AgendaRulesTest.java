package com.soskate.api.services.availability;

import com.soskate.api.entities.PlatformSettingsEntity;
import com.soskate.api.exceptions.booking.BookingException;
import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;
import com.soskate.api.repositories.PlatformSettingsRepository;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgendaRulesTest {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final LocalDate MONDAY = LocalDate.of(2027, 3, 1);
    private static final LocalDate THURSDAY = MONDAY.plusDays(3);

    private final AgendaRules rules = rulesAt(MONDAY.atTime(10, 0));

    @Test
    void theDelayIsCountedInCalendarDays() {
        assertThat(rulesAt(MONDAY.atTime(0, 5)).firstOpenDate()).isEqualTo(THURSDAY);
        assertThat(rulesAt(MONDAY.atTime(23, 55)).firstOpenDate()).isEqualTo(THURSDAY);
    }

    @Test
    void theOpenPeriodGoesFromTheDelayToTheHorizon() {
        assertThat(rules.isOpenDate(THURSDAY.minusDays(1))).isFalse();
        assertThat(rules.isOpenDate(THURSDAY)).isTrue();
        assertThat(rules.isOpenDate(LocalDate.of(2027, 6, 1))).isTrue();
        assertThat(rules.isOpenDate(LocalDate.of(2027, 6, 2))).isFalse();
    }

    @Test
    void aRangeMustBeAlignedAndInOrder() {
        assertThatCode(() -> rules.requireValidRange(time(14, 15), time(15, 45))).doesNotThrowAnyException();

        assertInvalid(() -> rules.requireValidRange(time(14, 10), time(15, 0)));
        assertInvalid(() -> rules.requireValidRange(time(14, 0), time(15, 5)));
        assertInvalid(() -> rules.requireValidRange(time(14, 0), time(14, 0)));
        assertInvalid(() -> rules.requireValidRange(time(15, 0), time(14, 0)));
    }

    @Test
    void aRangeMustFitInTheOpeningHours() {
        assertThatCode(() -> rules.requireWithinOpeningHours(time(8, 0), time(20, 0))).doesNotThrowAnyException();

        assertInvalid(() -> rules.requireWithinOpeningHours(time(7, 45), time(9, 0)));
        assertInvalid(() -> rules.requireWithinOpeningHours(time(19, 0), time(20, 15)));
    }

    @Test
    void aBlockMustStartInTheOpeningHours() {
        assertThat(rules.isWithinOpeningHours(THURSDAY.atTime(8, 0))).isTrue();
        assertThat(rules.isWithinOpeningHours(THURSDAY.atTime(19, 45))).isTrue();
        assertThat(rules.isWithinOpeningHours(THURSDAY.atTime(7, 45))).isFalse();
        assertThat(rules.isWithinOpeningHours(THURSDAY.atTime(20, 0))).isFalse();
    }

    @Test
    void onlyTheLessonDurationsAreAllowed() {
        for (int minutes : List.of(60, 90, 120, 180, 240)) {
            assertThatCode(() -> rules.requireValidDuration(minutes)).doesNotThrowAnyException();
        }
        for (int minutes : List.of(0, 45, 150, 300)) {
            assertThatThrownBy(() -> rules.requireValidDuration(minutes)).isInstanceOf(BookingException.class);
        }
    }

    private static AgendaRules rulesAt(LocalDateTime now) {
        PlatformSettingsRepository settings = mock(PlatformSettingsRepository.class);
        when(settings.getSettings()).thenReturn(PlatformSettingsEntity.builder().build());
        return new AgendaRules(settings, Clock.fixed(now.atZone(PARIS).toInstant(), PARIS));
    }

    private static void assertInvalid(ThrowingCallable call) {
        assertThatThrownBy(call)
                .isInstanceOf(ApiException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.INVALID_AVAILABILITY);
    }

    private static LocalTime time(int hour, int minute) {
        return LocalTime.of(hour, minute);
    }
}
