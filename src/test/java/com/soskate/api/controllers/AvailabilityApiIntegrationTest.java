package com.soskate.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soskate.api.AbstractIntegrationTest;
import com.soskate.api.entities.*;
import com.soskate.api.enums.*;
import com.soskate.api.repositories.*;
import com.soskate.api.security.CustomUserDetailsService;
import com.soskate.api.security.JwtService;
import com.soskate.api.services.availability.AgendaBlock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AvailabilityApiIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private Clock clock;
    @Autowired private JwtService jwtService;
    @Autowired private CustomUserDetailsService userDetailsService;
    @Autowired private InstructorRepository instructorRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private SpotRepository spotRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private BookingParticipantRepository participantRepository;
    @Autowired private AvailabilityBlockRepository blockRepository;

    private InstructorEntity coach;
    private LocalDate today;
    private LocalDate day;

    @BeforeEach
    void setUp() {
        coach = saveInstructor(InstructorStatus.ACTIVE, false);
        today = LocalDate.now(clock);
        day = today.plusDays(14);
    }

    // ==================== Security ====================

    @Test
    void anyoneCanReadAvailabilities() throws Exception {
        mockMvc.perform(get(url("/availabilities"))).andExpect(status().isOk());
    }

    @Test
    void writingRequiresBeingLoggedIn() throws Exception {
        mockMvc.perform(json(post(url("/availabilities")), addBody(day, 14, 16)))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(json(post(url("/availabilities/copy-week")), Map.of("weekStart", monday(), "weeks", 1)))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(json(put(url("/availabilities")), updateBody(14, 16, 15, 17, false)))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(json(post(url("/availabilities/removal")), removalBody(List.of(day), 14, 16, false)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anotherInstructorCannotWrite() throws Exception {
        InstructorEntity other = saveInstructor(InstructorStatus.ACTIVE, false);

        mockMvc.perform(as(other, json(post(url("/availabilities")), addBody(day, 14, 16))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
        mockMvc.perform(as(other, json(post(url("/availabilities/copy-week")), Map.of("weekStart", monday(), "weeks", 1))))
                .andExpect(status().isForbidden());
        mockMvc.perform(as(other, json(put(url("/availabilities")), updateBody(14, 16, 15, 17, false))))
                .andExpect(status().isForbidden());
        mockMvc.perform(as(other, json(post(url("/availabilities/removal")), removalBody(List.of(day), 14, 16, false))))
                .andExpect(status().isForbidden());
    }

    @Test
    void anAdminCanWrite() throws Exception {
        InstructorEntity admin = saveInstructor(InstructorStatus.ACTIVE, true);

        mockMvc.perform(as(admin, json(post(url("/availabilities")), addBody(day, 14, 16))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocksCreated").value(8));
    }

    @Test
    void theOldRoutesAreGone() throws Exception {
        mockMvc.perform(as(coach, get(url("/available")).param("from", day.toString()).param("to", day.toString())))
                .andExpect(status().isNotFound());
        mockMvc.perform(as(coach, delete(url("/availabilities/1"))))
                .andExpect(status().isNotFound());
    }

    // ==================== Adding ====================

    @Test
    void addedBlocksAreReadAsMergedRanges() throws Exception {
        add(day, 14, 16).andExpect(status().isOk()).andExpect(jsonPath("$.blocksCreated").value(8));
        add(day, 16, 17).andExpect(status().isOk()).andExpect(jsonPath("$.blocksCreated").value(4));
        add(day, 18, 19).andExpect(status().isOk());

        read(day, day)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].date").value(day.toString()))
                .andExpect(jsonPath("$[0].startTime").value("14:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("17:00:00"))
                .andExpect(jsonPath("$[1].startTime").value("18:00:00"))
                .andExpect(jsonPath("$[1].endTime").value("19:00:00"));
    }

    @Test
    void readingWithoutDatesGoesFromTodayToTheEndOfTheOpenPeriod() throws Exception {
        add(day, 14, 16);
        saveBlocks(today.minusDays(1), 14, 16, null);

        mockMvc.perform(get(url("/availabilities")))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].date").value(day.toString()));
    }

    @Test
    void addingTheSameHoursTwiceReportsThemAsCovered() throws Exception {
        add(day, 14, 16);

        add(day, 14, 16)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocksCreated").value(0))
                .andExpect(jsonPath("$.alreadyCoveredDates[0]").value(day.toString()));
    }

    @Test
    void onlyTheMissingBlocksAreCreated() throws Exception {
        add(day, 14, 16);

        add(day, 15, 17)
                .andExpect(jsonPath("$.blocksCreated").value(4))
                .andExpect(jsonPath("$.alreadyCoveredDates.length()").value(0));
    }

    @Test
    void datesOutsideTheOpenPeriodAreSkipped() throws Exception {
        LocalDate tooSoon = today.plusDays(2);
        LocalDate firstOpen = today.plusDays(3);
        LocalDate lastOpen = today.plusMonths(3);
        LocalDate tooFar = lastOpen.plusDays(1);

        mockMvc.perform(as(coach, json(post(url("/availabilities")),
                        addBody(List.of(tooSoon, firstOpen, lastOpen, tooFar), 14, 16))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocksCreated").value(16))
                .andExpect(jsonPath("$.skippedDates[0]").value(tooSoon.toString()))
                .andExpect(jsonPath("$.skippedDates[1]").value(tooFar.toString()));
    }

    @Test
    void lessThanOneHourIsSkippedUnlessItExtendsARange() throws Exception {
        LocalDate other = day.plusDays(1);
        add(day, 14, 15);

        Map<String, Object> body = new HashMap<>(addBody(List.of(day, other), 15, 16));
        body.put("endTime", LocalTime.of(15, 30));

        mockMvc.perform(as(coach, json(post(url("/availabilities")), body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocksCreated").value(2))
                .andExpect(jsonPath("$.skippedDates[0]").value(other.toString()));
    }

    @Test
    void invalidHoursAreRefused() throws Exception {
        add(day, 7, 9).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_AVAILABILITY"));
        add(day, 19, 21).andExpect(status().isBadRequest());
        add(day, 16, 14).andExpect(status().isBadRequest());

        Map<String, Object> unaligned = new HashMap<>(addBody(day, 14, 16));
        unaligned.put("startTime", LocalTime.of(14, 10));
        mockMvc.perform(as(coach, json(post(url("/availabilities")), unaligned)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void theOpeningHoursAreIncluded() throws Exception {
        add(day, 8, 9).andExpect(jsonPath("$.blocksCreated").value(4));
        add(day, 19, 20).andExpect(jsonPath("$.blocksCreated").value(4));
    }

    @Test
    void atLeastOneDateIsRequired() throws Exception {
        mockMvc.perform(as(coach, json(post(url("/availabilities")), addBody(List.of(), 14, 16))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    void anInactiveInstructorCannotAdd() throws Exception {
        InstructorEntity invited = saveInstructor(InstructorStatus.INVITED, false);
        InstructorEntity admin = saveInstructor(InstructorStatus.ACTIVE, true);

        mockMvc.perform(as(admin, json(post("/instructors/" + invited.getId() + "/availabilities"), addBody(day, 14, 16))))
                .andExpect(status().isBadRequest());
    }

    // ==================== Repeating a week ====================

    @Test
    void aWeekIsRepeatedOnTheFollowingWeeks() throws Exception {
        LocalDate monday = monday();
        LocalDate wednesday = monday.plusDays(2);
        add(monday, 14, 16);
        add(wednesday, 10, 12);

        copyWeek(monday, 2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocksCreated").value(32))
                .andExpect(jsonPath("$.skippedDates.length()").value(0));

        read(monday, monday.plusWeeks(3))
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[2].date").value(monday.plusWeeks(1).toString()))
                .andExpect(jsonPath("$[2].startTime").value("14:00:00"))
                .andExpect(jsonPath("$[5].date").value(wednesday.plusWeeks(2).toString()))
                .andExpect(jsonPath("$[5].startTime").value("10:00:00"));

        copyWeek(monday, 2)
                .andExpect(jsonPath("$.blocksCreated").value(0))
                .andExpect(jsonPath("$.alreadyCoveredDates.length()").value(4));
    }

    @Test
    void aBookedHourIsRepeatedAsAFreeOne() throws Exception {
        LocalDate monday = monday();
        saveBlocks(monday, 14, 15, saveLesson(monday, 14, 15));

        copyWeek(monday, 1).andExpect(jsonPath("$.blocksCreated").value(4));

        assertThat(blocksOn(monday.plusWeeks(1))).hasSize(4).allMatch(AgendaBlock::isFree);
    }

    @Test
    void weeksBeyondTheHorizonAreSkipped() throws Exception {
        LocalDate lastMonday = today.plusMonths(3).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        add(lastMonday, 14, 16);

        copyWeek(lastMonday, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocksCreated").value(0))
                .andExpect(jsonPath("$.skippedDates[0]").value(lastMonday.plusWeeks(1).toString()));
    }

    @Test
    void hoursThatAreNoLongerOpenAreSkipped() throws Exception {
        LocalDate monday = monday();
        saveBlocks(monday, 6, 8, null);

        copyWeek(monday, 1)
                .andExpect(jsonPath("$.blocksCreated").value(0))
                .andExpect(jsonPath("$.skippedDates[0]").value(monday.plusWeeks(1).toString()));
    }

    @Test
    void aWeekMustStartOnAMonday() throws Exception {
        copyWeek(monday().plusDays(1), 1)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_AVAILABILITY"));
    }

    @Test
    void aWeekIsRepeatedAtMostTwelveTimes() throws Exception {
        copyWeek(monday(), 13).andExpect(status().isBadRequest());
        copyWeek(monday(), 0).andExpect(status().isBadRequest());
    }

    // ==================== Changing a range ====================

    @Test
    void aRangeCanBeMoved() throws Exception {
        add(day, 14, 16);

        update(14, 16, 15, 17, false)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applied").value(true));

        read(day, day)
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].startTime").value("15:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("17:00:00"));
    }

    @Test
    void changingAnUnknownRangeIsRefused() throws Exception {
        update(14, 16, 15, 17, false)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("AVAILABILITY_NOT_FOUND"));
    }

    @Test
    void aRangeCannotBecomeShorterThanOneHour() throws Exception {
        add(day, 14, 16);

        Map<String, Object> body = new HashMap<>(updateBody(14, 16, 14, 15, false));
        body.put("newEndTime", LocalTime.of(14, 30));

        mockMvc.perform(as(coach, json(put(url("/availabilities")), body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_AVAILABILITY"));
        read(day, day).andExpect(jsonPath("$[0].endTime").value("16:00:00"));
    }

    @Test
    void aRangeCannotGrowOutsideTheOpenPeriod() throws Exception {
        LocalDate tooSoon = today.plusDays(1);
        saveBlocks(tooSoon, 14, 16, null);

        Map<String, Object> body = new HashMap<>(updateBody(14, 16, 14, 18, false));
        body.put("date", tooSoon);

        mockMvc.perform(as(coach, json(put(url("/availabilities")), body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void aRangeCannotMoveOutsideTheOpeningHours() throws Exception {
        add(day, 8, 10);

        update(8, 10, 7, 10, false)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_AVAILABILITY"));
    }

    @Test
    void shorteningARangeOverALessonWaitsForConfirmation() throws Exception {
        add(day, 14, 18);
        BookingEntity lesson = saveLesson(day, 16, 17);
        book(lesson, day, 16, 17);

        update(14, 18, 14, 16, false)
                .andExpect(jsonPath("$.applied").value(false))
                .andExpect(jsonPath("$.coursesToCancel[0].bookingId").value(lesson.getId()));
        read(day, day).andExpect(jsonPath("$[0].endTime").value("18:00:00"));

        update(14, 18, 14, 16, true).andExpect(jsonPath("$.applied").value(true));
        read(day, day).andExpect(jsonPath("$[0].endTime").value("16:00:00"));
        assertThat(bookingRepository.findById(lesson.getId()).orElseThrow().getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void aChangeWaitingForConfirmationAddsNothing() throws Exception {
        add(day, 14, 18);
        book(saveLesson(day, 14, 15), day, 14, 15);

        update(14, 18, 15, 19, false).andExpect(jsonPath("$.applied").value(false));

        read(day, day)
                .andExpect(jsonPath("$[0].startTime").value("14:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("18:00:00"));
    }

    // ==================== Removing hours ====================

    @Test
    void removingFreeHoursAppliesImmediately() throws Exception {
        add(day, 14, 19);

        remove(List.of(day), 16, 17, false)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applied").value(true))
                .andExpect(jsonPath("$.coursesToCancel.length()").value(0));

        read(day, day)
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].endTime").value("16:00:00"))
                .andExpect(jsonPath("$[1].startTime").value("17:00:00"));
    }

    @Test
    void removingInvalidHoursIsRefused() throws Exception {
        remove(List.of(day), 16, 14, false)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_AVAILABILITY"));
    }

    @Test
    void removingHoursOfALessonWritesNothingBeforeConfirmation() throws Exception {
        add(day, 14, 18);
        BookingEntity lesson = saveLesson(day, 15, 16);
        book(lesson, day, 15, 16);

        remove(List.of(day), 15, 16, false)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applied").value(false))
                .andExpect(jsonPath("$.coursesToCancel[0].bookingId").value(lesson.getId()))
                .andExpect(jsonPath("$.coursesToCancel[0].spotName").value(lesson.getSpot().getName()));

        assertThat(bookingRepository.findById(lesson.getId()).orElseThrow().getStatus()).isEqualTo(BookingStatus.OPEN);
        assertThat(blocksOn(day)).hasSize(16);
    }

    @Test
    void aConfirmedRemovalCancelsTheWholeLessonAndFreesItsOtherBlocks() throws Exception {
        // Available 14:00-18:00, lesson 15:00-16:00, absence 15:30-18:00.
        add(day, 14, 18);
        BookingEntity lesson = saveLesson(day, 15, 16);
        book(lesson, day, 15, 16);
        BookingParticipantEntity participant = saveParticipant(lesson);

        Map<String, Object> body = new HashMap<>(removalBody(List.of(day), 15, 18, true));
        body.put("startTime", LocalTime.of(15, 30));

        mockMvc.perform(as(coach, json(post(url("/availabilities/removal")), body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applied").value(true))
                .andExpect(jsonPath("$.coursesToCancel[0].bookingId").value(lesson.getId()));

        assertThat(bookingRepository.findById(lesson.getId()).orElseThrow().getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(participantRepository.findById(participant.getId()).orElseThrow().getStatus()).isEqualTo(ParticipantStatus.REFUNDED);
        assertThat(blocksOn(day)).hasSize(6).allMatch(AgendaBlock::isFree);
        read(day, day)
                .andExpect(jsonPath("$[0].startTime").value("14:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("15:30:00"));
    }

    @Test
    void aLeftoverIsErasedOnlyOnceConfirmed() throws Exception {
        add(day, 14, 18);

        Map<String, Object> body = new HashMap<>(removalBody(List.of(day), 14, 18, false));
        body.put("startTime", LocalTime.of(14, 30));

        mockMvc.perform(as(coach, json(post(url("/availabilities/removal")), body)))
                .andExpect(jsonPath("$.applied").value(false))
                .andExpect(jsonPath("$.alsoRemoved[0].startTime").value("14:00:00"))
                .andExpect(jsonPath("$.alsoRemoved[0].endTime").value("14:30:00"));
        assertThat(blocksOn(day)).hasSize(16);

        body.put("confirmed", true);
        mockMvc.perform(as(coach, json(post(url("/availabilities/removal")), body)))
                .andExpect(jsonPath("$.applied").value(true));
        assertThat(blocksOn(day)).isEmpty();
    }

    @Test
    void theSameHoursAreRemovedOnEveryDate() throws Exception {
        LocalDate next = day.plusDays(7);
        LocalDate untouched = day.plusDays(14);
        mockMvc.perform(as(coach, json(post(url("/availabilities")), addBody(List.of(day, next, untouched), 14, 16))));

        remove(List.of(day, next), 14, 16, false).andExpect(jsonPath("$.applied").value(true));

        assertThat(blocksOn(day)).isEmpty();
        assertThat(blocksOn(next)).isEmpty();
        assertThat(blocksOn(untouched)).hasSize(8);
    }

    @Test
    void aRemovalOverSeveralDatesWaitsIfOneOfThemNeedsConfirmation() throws Exception {
        LocalDate next = day.plusDays(7);
        mockMvc.perform(as(coach, json(post(url("/availabilities")), addBody(List.of(day, next), 14, 16))));
        book(saveLesson(next, 14, 15), next, 14, 15);

        remove(List.of(day, next), 14, 16, false).andExpect(jsonPath("$.applied").value(false));

        assertThat(blocksOn(day)).hasSize(8);
    }

    @Test
    void pastBlocksAreKept() throws Exception {
        LocalDate yesterday = today.minusDays(1);
        saveBlocks(yesterday, 14, 16, null);

        remove(List.of(yesterday), 14, 16, true).andExpect(jsonPath("$.applied").value(true));

        assertThat(blocksOn(yesterday)).hasSize(8);
    }

    // ==================== Concurrency ====================

    @Test
    void simultaneousAddsDoNotCollide() throws Exception {
        String token = tokenOf(coach);
        String body = objectMapper.writeValueAsString(addBody(day, 14, 16));
        Callable<Integer> addSameHours = () -> mockMvc.perform(post(url("/availabilities"))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().getResponse().getStatus();

        List<Future<Integer>> statuses = new ArrayList<>();
        try (ExecutorService executor = Executors.newFixedThreadPool(4)) {
            for (int i = 0; i < 4; i++) {
                statuses.add(executor.submit(addSameHours));
            }
        }
        for (Future<Integer> requestStatus : statuses) {
            assertThat(requestStatus.get()).isEqualTo(200);
        }

        assertThat(blocksOn(day)).hasSize(8);
    }

    // ==================== Helpers ====================

    private ResultActions add(LocalDate date, int startHour, int endHour) throws Exception {
        return mockMvc.perform(as(coach, json(post(url("/availabilities")), addBody(date, startHour, endHour))));
    }

    private ResultActions copyWeek(LocalDate weekStart, int weeks) throws Exception {
        return mockMvc.perform(as(coach, json(post(url("/availabilities/copy-week")),
                Map.of("weekStart", weekStart, "weeks", weeks))));
    }

    private ResultActions update(int currentStart, int currentEnd, int newStart, int newEnd, boolean confirmed) throws Exception {
        return mockMvc.perform(as(coach, json(put(url("/availabilities")),
                updateBody(currentStart, currentEnd, newStart, newEnd, confirmed))));
    }

    private ResultActions remove(List<LocalDate> dates, int startHour, int endHour, boolean confirmed) throws Exception {
        return mockMvc.perform(as(coach, json(post(url("/availabilities/removal")),
                removalBody(dates, startHour, endHour, confirmed))));
    }

    private ResultActions read(LocalDate from, LocalDate to) throws Exception {
        return mockMvc.perform(get(url("/availabilities")).param("from", from.toString()).param("to", to.toString()));
    }

    private Map<String, Object> addBody(LocalDate date, int startHour, int endHour) {
        return addBody(List.of(date), startHour, endHour);
    }

    private Map<String, Object> addBody(List<LocalDate> dates, int startHour, int endHour) {
        return Map.of("dates", dates, "startTime", LocalTime.of(startHour, 0), "endTime", LocalTime.of(endHour, 0));
    }

    private Map<String, Object> updateBody(int currentStart, int currentEnd, int newStart, int newEnd, boolean confirmed) {
        return Map.of(
                "date", day,
                "currentStartTime", LocalTime.of(currentStart, 0),
                "currentEndTime", LocalTime.of(currentEnd, 0),
                "newStartTime", LocalTime.of(newStart, 0),
                "newEndTime", LocalTime.of(newEnd, 0),
                "confirmed", confirmed
        );
    }

    private Map<String, Object> removalBody(List<LocalDate> dates, int startHour, int endHour, boolean confirmed) {
        return Map.of(
                "dates", dates,
                "startTime", LocalTime.of(startHour, 0),
                "endTime", LocalTime.of(endHour, 0),
                "confirmed", confirmed
        );
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder request, Object body) throws Exception {
        return request.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(body));
    }

    private MockHttpServletRequestBuilder as(UserEntity user, MockHttpServletRequestBuilder request) {
        return request.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOf(user));
    }

    private String tokenOf(UserEntity user) {
        return jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
    }

    private String url(String path) {
        return "/instructors/" + coach.getId() + path;
    }

    private LocalDate monday() {
        return today.plusDays(7).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
    }

    private List<AgendaBlock> blocksOn(LocalDate date) {
        return blockRepository.findAgendaBlocks(coach.getId(), date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    private InstructorEntity saveInstructor(InstructorStatus status, boolean admin) {
        return instructorRepository.save(InstructorEntity.builder()
                .email("coach-" + UUID.randomUUID() + "@soskate.test")
                .password("not-used")
                .firstName("Test")
                .lastName("Coach")
                .status(status)
                .isAdmin(admin)
                .deleted(false)
                .build());
    }

    private BookingEntity saveLesson(LocalDate date, int startHour, int endHour) {
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
                .startTime(date.atTime(startHour, 0))
                .endTime(date.atTime(endHour, 0))
                .durationMinutes((endHour - startHour) * 60)
                .build());
    }

    private BookingParticipantEntity saveParticipant(BookingEntity lesson) {
        CustomerEntity customer = customerRepository.save(CustomerEntity.builder()
                .email("rider-" + UUID.randomUUID() + "@soskate.test")
                .password("not-used")
                .deleted(false)
                .build());
        return participantRepository.save(BookingParticipantEntity.builder()
                .booking(lesson)
                .customer(customer)
                .numberOfParticipants(1)
                .status(ParticipantStatus.CONFIRMED)
                .amountCents(4000)
                .invitedBy(InvitedBy.SELF)
                .build());
    }

    private void book(BookingEntity lesson, LocalDate date, int startHour, int endHour) {
        List<AvailabilityBlockEntity> blocks = blockRepository.findAll().stream()
                .filter(block -> block.getStartAt().isAfter(date.atTime(startHour, 0).minusSeconds(1))
                        && block.getStartAt().isBefore(date.atTime(endHour, 0)))
                .filter(block -> block.getInstructor().getId().equals(coach.getId()))
                .toList();
        blocks.forEach(block -> block.setBooking(lesson));
        blockRepository.saveAll(blocks);
    }

    private void saveBlocks(LocalDate date, int startHour, int endHour, BookingEntity lesson) {
        List<AvailabilityBlockEntity> blocks = new ArrayList<>();
        for (LocalDateTime block = date.atTime(startHour, 0); block.isBefore(date.atTime(endHour, 0)); block = block.plusMinutes(15)) {
            blocks.add(AvailabilityBlockEntity.builder().instructor(coach).startAt(block).booking(lesson).build());
        }
        blockRepository.saveAll(blocks);
    }
}
