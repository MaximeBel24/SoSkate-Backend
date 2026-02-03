package com.soskate.api.controllers;

import com.soskate.api.dto.booking.*;
import com.soskate.api.services.booking.BookingParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingParticipantController {

    private final BookingParticipantService participantService;

    // ==================== Annulation ====================

    @PostMapping("/customers/{customerId}/participations/{participantId}/cancel")
    public ResponseEntity<ParticipantResponse> cancel(
            @PathVariable Long customerId,
            @PathVariable Long participantId,
            @Valid @RequestBody ParticipantCancelRequest request
    ) {
        ParticipantResponse response = participantService.cancel(customerId, participantId, request);
        return ResponseEntity.ok(response);
    }

    // ==================== Consultation ====================

    /**
     * Récupère toutes les réservations d'un customer (pour "Mes réservations")
     */
    @GetMapping("/customers/{customerId}/my-bookings")
    public ResponseEntity<List<MyBookingResponse>> getMyBookings(
            @PathVariable Long customerId
    ) {
        List<MyBookingResponse> bookings = participantService.getMyBookings(customerId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Modifie les notes d'une réservation
     */
    @PatchMapping("/customers/{customerId}/participations/{participationId}/notes")
    public ResponseEntity<MyBookingResponse> updateNotes(
            @PathVariable Long customerId,
            @PathVariable Long participationId,
            @RequestBody @Valid UpdateNotesRequest request
    ) {
        MyBookingResponse response = participantService.updateNotes(
                customerId,
                participationId,
                request.notes()
        );
        return ResponseEntity.ok(response);
    }
}
