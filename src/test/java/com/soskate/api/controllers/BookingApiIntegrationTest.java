package com.soskate.api.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soskate.api.AbstractIntegrationTest;
import com.soskate.api.entities.*;
import com.soskate.api.enums.*;
import com.soskate.api.repositories.*;
import com.soskate.api.security.CustomUserDetailsService;
import com.soskate.api.security.JwtService;
import com.soskate.api.services.availability.AgendaBlock;
import com.soskate.api.services.availability.AvailabilityGrid;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingApiIntegrationTest extends AbstractIntegrationTest {

    private static final BigDecimal LATITUDE = new BigDecimal("48.856600");
    private static final BigDecimal LONGITUDE = new BigDecimal("2.352200");
    private static final String FREE_STARTS = "$.slots[?(@.available == true)].startTime";
    private static final String TAKEN_STARTS = "$.slots[?(@.available == false)].startTime";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private Clock clock;
    @Autowired private JwtService jwtService;
    @Autowired private CustomUserDetailsService userDetailsService;
    @Autowired private InstructorRepository instructorRepository;
    @Autowired private InstructorSpotRepository instructorSpotRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private SpotRepository spotRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private BookingParticipantRepository participantRepository;
    @Autowired private AvailabilityBlockRepository blockRepository;

    private InstructorEntity coach;
    private CustomerEntity rider;
    private SpotEntity spot;
    /** About ten metres away: the travel time is the minimum, 15 minutes. */
    private SpotEntity nearbySpot;
    private ServiceEntity lessonType;
    private LocalDate today;
    private LocalDate day;

    @BeforeEach
    void setUp() {
        coach = saveInstructor(InstructorStatus.ACTIVE, false);
        rider = saveCustomer();
        spot = saveSpot(LATITUDE);
        nearbySpot = saveSpot(LATITUDE.add(new BigDecimal("0.000100")));
        teachAt(spot);
        teachAt(nearbySpot);
        lessonType = saveService();
        today = LocalDate.now(clock);
        day = today.plusDays(14);
    }

    // ==================== Slots ====================

    @Test
    void slotsStartEveryHalfHourAndKeepTheirFormat() throws Exception {
        offer(day, 14, 16);

        slots(day, 60, spot)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instructorId").value(coach.getId()))
                .andExpect(jsonPath("$.spotId").value(spot.getId()))
                .andExpect(jsonPath("$.date").value(day.toString()))
                .andExpect(jsonPath("$.durationMinutes").value(60))
                .andExpect(jsonPath("$.slots[*].startTime", contains("14:00:00", "14:30:00", "15:00:00")))
                .andExpect(jsonPath("$.slots[*].endTime", contains("15:00:00", "15:30:00", "16:00:00")))
                .andExpect(jsonPath("$.slots[*].available", contains(true, true, true)));
    }

    @Test
    void aBookedLessonMakesItsSlotsUnavailable() throws Exception {
        offer(day, 14, 18);
        bookedLesson(day.atTime(15, 0), 60, spot);

        slots(day, 60, spot)
                .andExpect(jsonPath(FREE_STARTS, contains("14:00:00", "16:00:00", "16:30:00", "17:00:00")))
                .andExpect(jsonPath(TAKEN_STARTS, contains("14:30:00", "15:00:00", "15:30:00")));
    }

    @Test
    void theTravelTimeIsKeptAroundALessonElsewhere() throws Exception {
        offer(day, 12, 17);
        bookedLesson(day.atTime(14, 0), 60, spot);

        slots(day, 60, nearbySpot)
                .andExpect(jsonPath(FREE_STARTS, contains("12:00:00", "12:30:00", "15:30:00", "16:00:00")))
                .andExpect(jsonPath(TAKEN_STARTS, contains("13:00:00", "13:30:00", "14:00:00", "14:30:00", "15:00:00")));
    }

    @Test
    void noSlotsBeforeTheDelayOrBeyondTheHorizon() throws Exception {
        LocalDate lastOpen = today.plusMonths(3);
        for (LocalDate date : List.of(today.plusDays(2), today.plusDays(3), lastOpen, lastOpen.plusDays(1))) {
            offer(date, 14, 15);
        }

        slots(today.plusDays(2), 60, spot).andExpect(jsonPath("$.slots.length()").value(0));
        slots(today.plusDays(3), 60, spot).andExpect(jsonPath(FREE_STARTS, contains("14:00:00")));
        slots(lastOpen, 60, spot).andExpect(jsonPath(FREE_STARTS, contains("14:00:00")));
        slots(lastOpen.plusDays(1), 60, spot).andExpect(jsonPath("$.slots.length()").value(0));
    }

    @Test
    void noSlotsWithAnInactiveInstructor() throws Exception {
        offer(day, 14, 16);
        setInstructorStatus(InstructorStatus.INVITED);

        slots(day, 60, spot)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slots.length()").value(0));
    }

    @Test
    void noSlotsAtASpotTheInstructorDoesNotTeachAt() throws Exception {
        offer(day, 14, 16);

        slots(day, 60, saveSpot(LATITUDE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slots.length()").value(0));
    }

    @Test
    void blocksOutsideTheOpeningHoursAreNotOffered() throws Exception {
        offer(day, 6, 9);

        slots(day, 60, spot).andExpect(jsonPath("$.slots[*].startTime", contains("08:00:00")));
    }

    @Test
    void slotsRefuseAnInvalidDuration() throws Exception {
        offer(day, 14, 18);

        for (int minutes : List.of(150, 0, -60)) {
            slots(day, minutes, spot)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("BOOKING_ERROR"));
        }
    }

    @Test
    void slotsRefuseAnUnknownInstructorOrSpot() throws Exception {
        mockMvc.perform(as(rider, get("/instructors/0/bookable")
                        .param("spotId", spot.getId().toString())
                        .param("date", day.toString())
                        .param("durationMinutes", "60")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("INSTRUCTOR_NOT_FOUND"));
        mockMvc.perform(as(rider, get("/instructors/" + coach.getId() + "/bookable")
                        .param("spotId", "0")
                        .param("date", day.toString())
                        .param("durationMinutes", "60")))
                .andExpect(status().isNotFound());
    }

    @Test
    void whatTheSlotsShowIsWhatABookingGets() throws Exception {
        offer(day, 12, 17);
        bookedLesson(day.atTime(14, 0), 60, spot);

        JsonNode slots = objectMapper.readTree(
                slots(day, 60, nearbySpot).andReturn().getResponse().getContentAsString()
        ).get("slots");
        int free = 0;
        int taken = 0;

        for (JsonNode slot : slots) {
            LocalDateTime start = day.atTime(LocalTime.parse(slot.get("startTime").asText()));
            if (slot.get("available").asBoolean()) {
                free++;
                long lesson = bookedLesson(start, 60, nearbySpot);
                mockMvc.perform(as(coach, patch("/bookings/" + lesson + "/cancel"))).andExpect(status().isOk());
            } else {
                taken++;
                book(start, 60, nearbySpot).andExpect(status().isConflict());
            }
        }

        assertThat(free).isEqualTo(4);
        assertThat(taken).isEqualTo(5);
    }

    // ==================== Booking ====================

    @Test
    void aBookingTakesItsBlocksAndConfirmsTheCustomer() throws Exception {
        offer(day, 14, 17);

        String body = book(day.atTime(14, 0), 90, spot)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("FULL"))
                .andExpect(jsonPath("$.endTime").value(day.atTime(15, 30).toString() + ":00"))
                .andReturn().getResponse().getContentAsString();
        long lesson = objectMapper.readTree(body).get("id").asLong();

        assertThat(takenBlocksOn(day)).containsExactlyElementsOf(
                AvailabilityGrid.blocksBetween(day.atTime(14, 0), day.atTime(15, 30)).stream()
                        .map(block -> new AgendaBlock(block, lesson))
                        .toList()
        );
        assertThat(blocksOn(day)).hasSize(12);
        assertThat(participantRepository.countConfirmedParticipants(lesson)).isEqualTo(1);
    }

    @Test
    void theDelayIsCountedInCalendarDays() throws Exception {
        offer(today.plusDays(2), 14, 15);
        offer(today.plusDays(3), 14, 15);

        book(today.plusDays(2).atTime(14, 0), 60, spot)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BOOKING_TOO_SOON"));
        book(today.plusDays(3).atTime(14, 0), 60, spot).andExpect(status().isCreated());
    }

    @Test
    void aBookingBeyondTheHorizonIsRefused() throws Exception {
        LocalDate lastOpen = today.plusMonths(3);
        offer(lastOpen, 14, 15);
        offer(lastOpen.plusDays(1), 14, 15);

        book(lastOpen.plusDays(1).atTime(14, 0), 60, spot)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSTRUCTOR_NOT_AVAILABLE"));
        book(lastOpen.atTime(14, 0), 60, spot).andExpect(status().isCreated());
    }

    @Test
    void aLessonWithoutAvailabilityIsRefused() throws Exception {
        book(day.atTime(14, 0), 60, spot)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSTRUCTOR_NOT_AVAILABLE"));
    }

    @Test
    void aLessonRunningPastTheEndOfARangeIsRefusedAndLeavesNothingBehind() throws Exception {
        offer(day, 14, 15);

        book(day.atTime(14, 30), 60, spot)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSTRUCTOR_NOT_AVAILABLE"));

        assertThat(bookingRepository.findByInstructorId(coach.getId())).isEmpty();
        assertThat(takenBlocksOn(day)).isEmpty();
    }

    @Test
    void aLessonOutsideTheOpeningHoursIsRefused() throws Exception {
        offer(day, 6, 9);

        book(day.atTime(7, 0), 60, spot)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSTRUCTOR_NOT_AVAILABLE"));
    }

    @Test
    void aLessonMustStartOnTheHourOrHalfPast() throws Exception {
        offer(day, 14, 16);

        book(day.atTime(14, 15), 60, spot)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BOOKING_ERROR"));
        book(day.atTime(14, 0, 30), 60, spot).andExpect(status().isBadRequest());
    }

    @Test
    void aBookingRefusesAnInvalidDuration() throws Exception {
        offer(day, 14, 18);

        book(day.atTime(14, 0), 150, spot)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BOOKING_ERROR"));
    }

    @Test
    void aTakenSlotIsRefused() throws Exception {
        offer(day, 14, 16);
        bookedLesson(day.atTime(14, 0), 60, spot);

        book(day.atTime(14, 30), 60, spot)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("SLOT_NOT_AVAILABLE"));
    }

    @Test
    void twoLessonsCanFollowEachOtherAtTheSameSpot() throws Exception {
        offer(day, 14, 16);

        book(day.atTime(14, 0), 60, spot).andExpect(status().isCreated());
        book(day.atTime(15, 0), 60, spot).andExpect(status().isCreated());
    }

    @Test
    void theTravelTimeIsKeptBetweenTwoSpots() throws Exception {
        offer(day, 14, 17);
        bookedLesson(day.atTime(14, 0), 60, spot);

        book(day.atTime(15, 0), 60, nearbySpot)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("SLOT_NOT_AVAILABLE"));
        book(day.atTime(15, 30), 60, nearbySpot).andExpect(status().isCreated());
    }

    @Test
    void aLessonWithAnInactiveInstructorIsRefused() throws Exception {
        offer(day, 14, 16);
        setInstructorStatus(InstructorStatus.INVITED);

        book(day.atTime(14, 0), 60, spot)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BOOKING_ERROR"));
    }

    @Test
    void aLessonAtASpotTheInstructorDoesNotTeachAtIsRefused() throws Exception {
        offer(day, 14, 16);

        book(day.atTime(14, 0), 60, saveSpot(LATITUDE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INSTRUCTOR_NOT_AT_SPOT"));
    }

    @Test
    void tooManyParticipantsAreRefused() throws Exception {
        offer(day, 14, 16);

        mockMvc.perform(as(rider, json(post("/bookings"), bookingBody(day.atTime(14, 0), 60, spot, 2))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BOOKING_ERROR"));

        assertThat(takenBlocksOn(day)).isEmpty();
    }

    @Test
    void anUnknownInstructorIsRefused() throws Exception {
        Map<String, Object> body = Map.of(
                "customerId", rider.getId(),
                "instructorId", 0,
                "spotId", spot.getId(),
                "serviceId", lessonType.getId(),
                "startTime", day.atTime(14, 0),
                "durationMinutes", 60,
                "numberOfParticipants", 1
        );

        mockMvc.perform(as(rider, json(post("/bookings"), body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("INSTRUCTOR_NOT_FOUND"));
    }

    // ==================== Concurrency ====================

    @Test
    void onlyOneOfSeveralSimultaneousBookingsGetsTheSlot() throws Exception {
        offer(day, 14, 16);
        Map<String, Object> sameLesson = bookingBody(day.atTime(14, 0), 60, spot, 1);

        List<Integer> statuses = bookTogether(List.of(sameLesson, sameLesson, sameLesson, sameLesson));

        assertThat(statuses).containsExactlyInAnyOrder(201, 409, 409, 409);
        assertThat(bookingRepository.findByInstructorId(coach.getId())).hasSize(1);
        assertThat(takenBlocksOn(day)).hasSize(4);
    }

    @Test
    void theTravelTimeHoldsBetweenSimultaneousBookings() throws Exception {
        for (int i = 0; i < 8; i++) {
            LocalDate date = day.plusDays(i);
            offer(date, 14, 16);

            List<Integer> statuses = bookTogether(List.of(
                    bookingBody(date.atTime(14, 0), 60, spot, 1),
                    bookingBody(date.atTime(15, 0), 60, nearbySpot, 1)
            ));

            assertThat(statuses).containsExactlyInAnyOrder(201, 409);
        }
    }

    // ==================== Cancelling ====================

    @Test
    void aCustomerCancellingFreesTheSlot() throws Exception {
        offer(day, 14, 16);
        long lesson = bookedLesson(day.atTime(14, 0), 60, spot);

        cancelAsCustomer(lesson).andExpect(status().isOk());

        assertThat(statusOf(lesson)).isEqualTo(BookingStatus.CANCELLED);
        assertThat(takenBlocksOn(day)).isEmpty();
        slots(day, 60, spot).andExpect(jsonPath("$.slots[?(@.available == false)]").isEmpty());
        book(day.atTime(14, 0), 60, spot).andExpect(status().isCreated());
    }

    @Test
    void anInstructorCancellingFreesTheSlot() throws Exception {
        offer(day, 14, 16);
        long lesson = bookedLesson(day.atTime(14, 0), 60, spot);

        mockMvc.perform(as(coach, patch("/bookings/" + lesson + "/cancel"))).andExpect(status().isOk());

        assertThat(statusOf(lesson)).isEqualTo(BookingStatus.CANCELLED);
        assertThat(takenBlocksOn(day)).isEmpty();
        book(day.atTime(14, 0), 60, spot).andExpect(status().isCreated());
    }

    @Test
    void anAdminCancellingFreesTheSlot() throws Exception {
        InstructorEntity admin = saveInstructor(InstructorStatus.ACTIVE, true);
        offer(day, 14, 16);
        long lesson = bookedLesson(day.atTime(14, 0), 60, spot);

        mockMvc.perform(as(admin, patch("/admin/bookings/" + lesson + "/cancel"))).andExpect(status().isOk());

        assertThat(statusOf(lesson)).isEqualTo(BookingStatus.CANCELLED);
        assertThat(takenBlocksOn(day)).isEmpty();
        book(day.atTime(14, 0), 60, spot).andExpect(status().isCreated());
    }

    @Test
    void anAdminSettingTheStatusToCancelledFreesTheSlot() throws Exception {
        InstructorEntity admin = saveInstructor(InstructorStatus.ACTIVE, true);
        offer(day, 14, 16);
        long lesson = bookedLesson(day.atTime(14, 0), 60, spot);

        mockMvc.perform(as(admin, json(patch("/admin/bookings/" + lesson + "/status"), Map.of("status", "CANCELLED"))))
                .andExpect(status().isOk());

        assertThat(takenBlocksOn(day)).isEmpty();
    }

    @Test
    void anotherStatusKeepsTheSlot() throws Exception {
        InstructorEntity admin = saveInstructor(InstructorStatus.ACTIVE, true);
        offer(day, 14, 16);
        long lesson = bookedLesson(day.atTime(14, 0), 60, spot);

        mockMvc.perform(as(admin, json(patch("/admin/bookings/" + lesson + "/status"), Map.of("status", "CONFIRMED"))))
                .andExpect(status().isOk());

        assertThat(takenBlocksOn(day)).hasSize(4);
    }

    @Test
    void aCompletedLessonStaysCompletedWhenTheCustomerCancels() throws Exception {
        offer(day, 14, 16);
        long lesson = bookedLesson(day.atTime(14, 0), 60, spot);
        BookingEntity booking = bookingRepository.findById(lesson).orElseThrow();
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        cancelAsCustomer(lesson).andExpect(status().isOk());

        assertThat(statusOf(lesson)).isEqualTo(BookingStatus.COMPLETED);
        assertThat(takenBlocksOn(day)).hasSize(4);
    }

    @Test
    void cancellingOneLessonLeavesTheOthers() throws Exception {
        offer(day, 14, 16);
        long cancelled = bookedLesson(day.atTime(14, 0), 60, spot);
        long kept = bookedLesson(day.atTime(15, 0), 60, spot);

        cancelAsCustomer(cancelled).andExpect(status().isOk());

        assertThat(statusOf(kept)).isEqualTo(BookingStatus.FULL);
        assertThat(takenBlocksOn(day)).hasSize(4).allMatch(block -> block.bookingId().equals(kept));
    }

    @Test
    void aConfirmedAbsenceCancelsARealBooking() throws Exception {
        offer(day, 14, 18);
        long lesson = bookedLesson(day.atTime(14, 0), 60, spot);

        Map<String, Object> absence = Map.of(
                "dates", List.of(day),
                "startTime", LocalTime.of(14, 0),
                "endTime", LocalTime.of(15, 0),
                "confirmed", false
        );
        mockMvc.perform(as(coach, json(post("/instructors/" + coach.getId() + "/availabilities/removal"), absence)))
                .andExpect(jsonPath("$.applied").value(false))
                .andExpect(jsonPath("$.coursesToCancel[0].bookingId").value(lesson));
        assertThat(statusOf(lesson)).isEqualTo(BookingStatus.FULL);

        Map<String, Object> confirmed = new HashMap<>(absence);
        confirmed.put("confirmed", true);
        mockMvc.perform(as(coach, json(post("/instructors/" + coach.getId() + "/availabilities/removal"), confirmed)))
                .andExpect(jsonPath("$.applied").value(true));

        assertThat(statusOf(lesson)).isEqualTo(BookingStatus.CANCELLED);
        assertThat(participantRepository.findByBookingId(lesson).get(0).getStatus()).isEqualTo(ParticipantStatus.REFUNDED);
        assertThat(blocksOn(day)).hasSize(12).allMatch(AgendaBlock::isFree);
    }

    // ==================== Helpers ====================

    private ResultActions slots(LocalDate date, int durationMinutes, SpotEntity where) throws Exception {
        return mockMvc.perform(as(rider, get("/instructors/" + coach.getId() + "/bookable")
                .param("spotId", where.getId().toString())
                .param("date", date.toString())
                .param("durationMinutes", String.valueOf(durationMinutes))));
    }

    private ResultActions book(LocalDateTime start, int durationMinutes, SpotEntity where) throws Exception {
        return mockMvc.perform(as(rider, json(post("/bookings"), bookingBody(start, durationMinutes, where, 1))));
    }

    private long bookedLesson(LocalDateTime start, int durationMinutes, SpotEntity where) throws Exception {
        String body = book(start, durationMinutes, where)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    private ResultActions cancelAsCustomer(long lesson) throws Exception {
        Long participation = participantRepository.findByBookingId(lesson).get(0).getId();
        return mockMvc.perform(as(rider, json(
                post("/customers/" + rider.getId() + "/participations/" + participation + "/cancel"),
                Map.of("reason", "sick")
        )));
    }

    /**
     * Sends the bookings at the same moment and returns their HTTP statuses.
     */
    private List<Integer> bookTogether(List<Map<String, Object>> bodies) throws Exception {
        String token = tokenOf(rider);
        CountDownLatch go = new CountDownLatch(1);
        List<Future<Integer>> futures = new ArrayList<>();

        try (ExecutorService executor = Executors.newFixedThreadPool(bodies.size())) {
            for (Map<String, Object> body : bodies) {
                String content = objectMapper.writeValueAsString(body);
                futures.add(executor.submit(() -> {
                    go.await();
                    return mockMvc.perform(post("/bookings")
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                            .andReturn().getResponse().getStatus();
                }));
            }
            go.countDown();
        }

        List<Integer> statuses = new ArrayList<>();
        for (Future<Integer> future : futures) {
            statuses.add(future.get());
        }
        return statuses;
    }

    private Map<String, Object> bookingBody(LocalDateTime start, int durationMinutes, SpotEntity where, int participants) {
        return Map.of(
                "customerId", rider.getId(),
                "instructorId", coach.getId(),
                "spotId", where.getId(),
                "serviceId", lessonType.getId(),
                "startTime", start,
                "durationMinutes", durationMinutes,
                "numberOfParticipants", participants
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

    private BookingStatus statusOf(long lesson) {
        return bookingRepository.findById(lesson).orElseThrow().getStatus();
    }

    private List<AgendaBlock> blocksOn(LocalDate date) {
        return blockRepository.findAgendaBlocks(coach.getId(), date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    private List<AgendaBlock> takenBlocksOn(LocalDate date) {
        return blocksOn(date).stream().filter(block -> !block.isFree()).toList();
    }

    private void offer(LocalDate date, int startHour, int endHour) {
        blockRepository.saveAll(AvailabilityGrid.blocksBetween(date.atTime(startHour, 0), date.atTime(endHour, 0)).stream()
                .map(block -> AvailabilityBlockEntity.builder().instructor(coach).startAt(block).build())
                .toList());
    }

    private void setInstructorStatus(InstructorStatus status) {
        coach.setStatus(status);
        coach = instructorRepository.save(coach);
    }

    private void teachAt(SpotEntity where) {
        instructorSpotRepository.save(InstructorSpotEntity.builder().instructor(coach).spot(where).build());
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

    private CustomerEntity saveCustomer() {
        return customerRepository.save(CustomerEntity.builder()
                .email("rider-" + UUID.randomUUID() + "@soskate.test")
                .password("not-used")
                .firstName("Test")
                .lastName("Rider")
                .deleted(false)
                .build());
    }

    private SpotEntity saveSpot(BigDecimal latitude) {
        return spotRepository.save(SpotEntity.builder()
                .name("Spot " + UUID.randomUUID())
                .latitude(latitude)
                .longitude(LONGITUDE)
                .isIndoor(false)
                .isActive(true)
                .build());
    }

    private ServiceEntity saveService() {
        return serviceRepository.save(ServiceEntity.builder()
                .name("Service " + UUID.randomUUID())
                .type(ServiceType.values()[0])
                .basePriceCents(4000)
                .isActive(true)
                .build());
    }
}
