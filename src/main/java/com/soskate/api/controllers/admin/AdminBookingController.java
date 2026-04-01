package com.soskate.api.controllers.admin;

import com.soskate.api.dto.admin.UpdateStatusRequest;
import com.soskate.api.dto.booking.BookingResponse;
import com.soskate.api.dto.booking.UpdateNotesRequest;
import com.soskate.api.services.admin.AdminBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - Booking", description = "Administrative booking management")
@Slf4j
@RestController
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private final AdminBookingService adminBookingService;

    @Operation(
            summary = "List all bookings",
            description = "Retrieve all bookings"
    )
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        log.info("GET /api/admin/bookings - Retrieving all bookings");

        List<BookingResponse> bookings = adminBookingService.getAllBookings();

        log.info("{} booking(s) found", bookings.size());

        return ResponseEntity.ok(bookings);
    }

    @Operation(
            summary = "Cancel booking",
            description = "Cancel booking by admin"
    )
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id) {
        log.info("PATCH /api/admin/bookings/{id}/cancel - Cancel booking");
        return ResponseEntity.ok(adminBookingService.cancelBooking(id));
    }

    @Operation(
            summary = "Get booking by id",
            description = "Retrieve specific booking with id"
    )
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        log.info("GET /api/admin/bookings/{id} - Get booking by id");
        return ResponseEntity.ok(adminBookingService.getBookingById(id));
    }

    @Operation(
            summary = "Update notes",
            description = "Update booking notes by admin"
    )
    @PatchMapping("/{id}/notes")
    public ResponseEntity<BookingResponse> updateNotes(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNotesRequest request
    ) {
        log.info("PATCH /api/admin/bookings/{id}/notes - Update notes");
        return ResponseEntity.ok(adminBookingService.updateNotes(id, request));
    }

    @Operation(
            summary = "Update Status",
            description = "Update booking status by admin"
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request
    ) {
        log.info("PATCH /api/admin/bookings/{id}/status - Update status");
        return ResponseEntity.ok(adminBookingService.updateStatus(id, request.status()));
    }

}
