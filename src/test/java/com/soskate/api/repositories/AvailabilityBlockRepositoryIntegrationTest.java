package com.soskate.api.repositories;

import com.soskate.api.AbstractIntegrationTest;
import com.soskate.api.entities.*;
import com.soskate.api.enums.InstructorStatus;
import com.soskate.api.enums.ServiceType;
import com.soskate.api.services.availability.AgendaBlock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AvailabilityBlockRepositoryIntegrationTest extends AbstractIntegrationTest {

    private static final LocalDate DAY = LocalDate.of(2027, 3, 10);

    @Autowired private AvailabilityBlockRepository blockRepository;
    @Autowired private InstructorRepository instructorRepository;
    @Autowired private SpotRepository spotRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private PlatformSettingsRepository settingsRepository;

    private InstructorEntity coach;

    @BeforeEach
    void setUp() {
        coach = saveInstructor();
    }

    @Test
    void theSettingsHaveTheirDefaultValues() {
        PlatformSettingsEntity settings = settingsRepository.getSettings();

        assertThat(settings.getMinDaysInAdvance()).isEqualTo(3);
        assertThat(settings.getAvailabilityHorizonMonths()).isEqualTo(3);
        assertThat(settings.getOpeningTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(settings.getClosingTime()).isEqualTo(LocalTime.of(20, 0));
    }

    @Test
    void aBlockStaysAtTheHourItWasWritten() {
        save(coach, at(14, 15), null);

        assertThat(blocksOf(coach)).containsExactly(new AgendaBlock(at(14, 15), null));
    }

    @Test
    void blocksAreReadInOrderOverAPeriodWithoutItsEnd() {
        save(coach, at(15, 0), null);
        save(coach, at(14, 0), null);
        save(coach, at(16, 0), null);

        assertThat(blockRepository.findAgendaBlocks(coach.getId(), at(14, 0), at(16, 0)))
                .extracting(AgendaBlock::startAt)
                .containsExactly(at(14, 0), at(15, 0));
    }

    @Test
    void aBlockKnowsTheSessionOccupyingIt() {
        BookingEntity lesson = saveLesson();
        save(coach, at(14, 0), lesson);
        save(coach, at(14, 15), null);

        assertThat(blocksOf(coach)).containsExactly(
                new AgendaBlock(at(14, 0), lesson.getId()),
                new AgendaBlock(at(14, 15), null)
        );
    }

    @Test
    void anInstructorCannotHaveTheSameBlockTwice() {
        save(coach, at(14, 0), null);

        assertThatThrownBy(() -> save(coach, at(14, 0), null))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void twoInstructorsCanHaveTheSameBlock() {
        InstructorEntity other = saveInstructor();

        save(coach, at(14, 0), null);
        save(other, at(14, 0), null);

        assertThat(blocksOf(other)).hasSize(1);
    }

    @Test
    void aBlockMustStartOnAQuarterOfAnHour() {
        assertThatThrownBy(() -> save(coach, at(14, 10), null))
                .hasMessageContaining("chk_availability_blocks_quarter_hour");
    }

    @Test
    @Transactional
    void deletingBlocksOnlyTouchesThatInstructor() {
        InstructorEntity other = saveInstructor();
        save(coach, at(14, 0), null);
        save(coach, at(15, 0), null);
        save(other, at(14, 0), null);

        int deleted = blockRepository.deleteBlocks(coach.getId(), List.of(at(14, 0)));

        assertThat(deleted).isEqualTo(1);
        assertThat(blocksOf(coach)).extracting(AgendaBlock::startAt).containsExactly(at(15, 0));
        assertThat(blocksOf(other)).hasSize(1);
    }

    @Test
    @Transactional
    void releasingASessionFreesOnlyItsBlocks() {
        BookingEntity released = saveLesson();
        BookingEntity kept = saveLesson();
        save(coach, at(14, 0), released);
        save(coach, at(15, 0), kept);

        int freed = blockRepository.releaseBooking(released.getId());

        assertThat(freed).isEqualTo(1);
        assertThat(blocksOf(coach)).containsExactly(
                new AgendaBlock(at(14, 0), null),
                new AgendaBlock(at(15, 0), kept.getId())
        );
    }

    @Test
    @Transactional
    void takingBlocksLeavesThoseAlreadyTaken() {
        InstructorEntity other = saveInstructor();
        BookingEntity first = saveLesson();
        BookingEntity second = saveLesson();
        save(coach, at(14, 0), first);
        save(coach, at(14, 15), null);
        save(other, at(14, 15), null);

        int taken = blockRepository.takeBlocks(second, coach.getId(), List.of(at(14, 0), at(14, 15)));

        assertThat(taken).isEqualTo(1);
        assertThat(blocksOf(coach)).containsExactly(
                new AgendaBlock(at(14, 0), first.getId()),
                new AgendaBlock(at(14, 15), second.getId())
        );
        assertThat(blocksOf(other)).containsExactly(new AgendaBlock(at(14, 15), null));
    }

    private List<AgendaBlock> blocksOf(InstructorEntity instructor) {
        return blockRepository.findAgendaBlocks(instructor.getId(), DAY.atStartOfDay(), DAY.plusDays(1).atStartOfDay());
    }

    private void save(InstructorEntity instructor, LocalDateTime startAt, BookingEntity lesson) {
        blockRepository.saveAndFlush(AvailabilityBlockEntity.builder()
                .instructor(instructor)
                .startAt(startAt)
                .booking(lesson)
                .build());
    }

    private InstructorEntity saveInstructor() {
        return instructorRepository.save(InstructorEntity.builder()
                .email("coach-" + UUID.randomUUID() + "@soskate.test")
                .password("not-used")
                .status(InstructorStatus.ACTIVE)
                .isAdmin(false)
                .deleted(false)
                .build());
    }

    private BookingEntity saveLesson() {
        SpotEntity spot = spotRepository.save(SpotEntity.builder()
                .name("Spot " + UUID.randomUUID())
                .latitude(new BigDecimal("48.856600"))
                .longitude(new BigDecimal("2.352200"))
                .isIndoor(false)
                .isActive(true)
                .build());
        ServiceEntity service = serviceRepository.save(ServiceEntity.builder()
                .name("Service " + UUID.randomUUID())
                .type(ServiceType.values()[0])
                .basePriceCents(4000)
                .isActive(true)
                .build());
        return bookingRepository.save(BookingEntity.builder()
                .instructor(coach)
                .spot(spot)
                .service(service)
                .startTime(at(14, 0))
                .endTime(at(15, 0))
                .build());
    }

    private static LocalDateTime at(int hour, int minute) {
        return DAY.atTime(hour, minute);
    }
}
