package com.soskate.api.controllers;

import com.soskate.api.dto.booking.BookingCreateRequest;
import com.soskate.api.dto.booking.BookingResponse;
import com.soskate.api.services.booking.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // ==================== Création de réservation ====================

    @PostMapping("/customers/{customerId}/bookings")
    public ResponseEntity<BookingResponse> createBooking(
            @PathVariable Long customerId,
            @Valid @RequestBody BookingCreateRequest request
    ) {
        BookingResponse response = bookingService.createBooking(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== Consultation des réservations ====================

    @GetMapping("/bookings/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instructors/{instructorId}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByInstructor(
            @PathVariable Long instructorId,
            @RequestParam(required = false) String filter
    ) {
        List<BookingResponse> response = switch(filter) {
          case "upcoming" -> bookingService.getUpcomingBookingsByInstructor(instructorId);
          case "passed" -> bookingService.getPassedBookingsByInstructor(instructorId);
          case null, default -> bookingService.getBookingsByInstructor(instructorId);
        };

        return ResponseEntity.ok(response);
    }

    @PostMapping("/instructors/{instructorId}/bookings/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelByInstructor(
            @PathVariable Long instructorId,
            @PathVariable Long bookingId
    ) {
        BookingResponse response = bookingService.cancelBooking(instructorId, bookingId);
        return ResponseEntity.ok(response);
    }
}