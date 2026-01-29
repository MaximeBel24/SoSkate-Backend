package com.soskate.api.controllers;

import com.soskate.api.dto.booking.AvailableSlotsResponse;
import com.soskate.api.dto.booking.BookingCreateRequest;
import com.soskate.api.dto.booking.BookingResponse;
import com.soskate.api.services.booking.BookingService;
import com.soskate.api.services.booking.SlotCalculationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final SlotCalculationService slotCalculationService;

    // ==================== Création de réservation ====================

    @PostMapping("/customers/{customerId}/bookings")
    public ResponseEntity<BookingResponse> create(
            @PathVariable Long customerId,
            @Valid @RequestBody BookingCreateRequest request
    ) {
        BookingResponse response = bookingService.create(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== Consultation des créneaux disponibles ====================

    @GetMapping("/instructors/{instructorId}/available-slots")
    public ResponseEntity<AvailableSlotsResponse> getAvailableSlots(
            @PathVariable Long instructorId,
            @RequestParam Long spotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer durationMinutes
    ) {
        AvailableSlotsResponse response = slotCalculationService.calculateAvailableSlots(
                instructorId,
                spotId,
                date,
                durationMinutes
        );
        return ResponseEntity.ok(response);
    }

    // ==================== Consultation des réservations ====================

    @GetMapping("/bookings/{id}")
    public ResponseEntity<BookingResponse> getById(@PathVariable Long id) {
        BookingResponse response = bookingService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instructors/{instructorId}/bookings")
    public ResponseEntity<List<BookingResponse>> getByInstructor(
            @PathVariable Long instructorId,
            @RequestParam(required = false) String filter
    ) {
        List<BookingResponse> response = switch(filter) {
          case "upcoming" -> bookingService.getUpcomingByInstructor(instructorId);
          case "passed" -> bookingService.getPassedByInstructor(instructorId);
          case null, default -> bookingService.getByInstructor(instructorId);
        };

        return ResponseEntity.ok(response);
    }

    @GetMapping("/spots/{spotId}/bookings")
    public ResponseEntity<List<BookingResponse>> getBySpot(@PathVariable Long spotId) {
        List<BookingResponse> response = bookingService.getUpcomingBySpot(spotId);
        return ResponseEntity.ok(response);
    }

    // ==================== Actions sur les réservations (Instructeur) ====================

    @PostMapping("/instructors/{instructorId}/bookings/{bookingId}/complete")
    public ResponseEntity<BookingResponse> complete(
            @PathVariable Long instructorId,
            @PathVariable Long bookingId
    ) {
        BookingResponse response = bookingService.complete(instructorId, bookingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/instructors/{instructorId}/bookings/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelByInstructor(
            @PathVariable Long instructorId,
            @PathVariable Long bookingId
    ) {
        BookingResponse response = bookingService.cancel(instructorId, bookingId);
        return ResponseEntity.ok(response);
    }
}