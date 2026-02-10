package com.soskate.api.controllers;

import com.soskate.api.dto.booking.*;
import com.soskate.api.services.booking.BookingParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Participations", description = "Booking participation management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/customers/{customerId}")
@Slf4j
public class BookingParticipantController {

    private final BookingParticipantService participantService;

    // ==================== Cancellation ====================

    @Operation(
            summary = "Cancel a participation",
            description = "Cancels a customer's participation in a booking"
    )
    @PostMapping("/participations/{participantId}/cancel")
    @PreAuthorize("@userSecurity.isOwner(#customerId) or @userSecurity.isAdmin()")
    public ResponseEntity<ParticipantResponse> cancel(
            @PathVariable Long customerId,
            @PathVariable Long participantId,
            @Valid @RequestBody ParticipantCancelRequest request
    ) {
        log.info("POST /api/customers/{}/participations/{}/cancel - Cancelling participation", customerId, participantId);
        ParticipantResponse response = participantService.cancel(customerId, participantId, request);
        return ResponseEntity.ok(response);
    }

    // ==================== Retrieval ====================

    @Operation(
            summary = "My bookings",
            description = "Retrieves all bookings for a customer"
    )
    @GetMapping("/my-bookings")
    @PreAuthorize("@userSecurity.isOwner(#customerId) or @userSecurity.isAdmin()")
    public ResponseEntity<List<MyBookingResponse>> getMyBookings(
            @PathVariable Long customerId
    ) {
        log.info("GET /api/customers/{}/my-bookings - Retrieving bookings", customerId);
        List<MyBookingResponse> bookings = participantService.getMyBookings(customerId);
        return ResponseEntity.ok(bookings);
    }

    @Operation(
            summary = "Update notes",
            description = "Updates the notes of a participation"
    )
    @PatchMapping("/participations/{participationId}/notes")
    @PreAuthorize("@userSecurity.isOwner(#customerId) or @userSecurity.isAdmin()")
    public ResponseEntity<MyBookingResponse> updateNotes(
            @PathVariable Long customerId,
            @PathVariable Long participationId,
            @RequestBody @Valid UpdateNotesRequest request
    ) {
        log.info("PATCH /api/customers/{}/participations/{}/notes - Updating notes", customerId, participationId);
        MyBookingResponse response = participantService.updateNotes(
                customerId,
                participationId,
                request.notes()
        );
        return ResponseEntity.ok(response);
    }
}
