package com.soskate.api.controllers;

import com.soskate.api.dto.booking.BookingCreateRequest;
import com.soskate.api.dto.booking.BookingResponse;
import com.soskate.api.security.CustomUserDetails;
import com.soskate.api.services.booking.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bookings", description = "Booking management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @Operation(
            summary = "Create a booking",
            description = "Creates a new booking for a customer"
    )
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingCreateRequest request
    ) {
        log.info("POST /api/bookings - Creating a booking");
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Retrieve a booking",
            description = "Retrieves a booking by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        log.info("GET /api/bookings/{} - Retrieving booking", id);
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cancel a booking",
            description = "Cancels a booking by its instructor"
    )
    @PatchMapping("/{bookingId}/cancel")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<BookingResponse> cancelByInstructor(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long bookingId
    ) {
            Long instructorId = userDetails.getUserEntity().getId();
            log.info("PATCH /api/bookings/{}/cancel - Cancellation by instructor {}", bookingId, instructorId);
            BookingResponse response = bookingService.cancelBooking(instructorId, bookingId);
            return ResponseEntity.ok(response);
    }
}