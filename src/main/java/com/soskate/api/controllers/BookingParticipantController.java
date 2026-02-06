package com.soskate.api.controllers;

import com.soskate.api.dto.booking.*;
import com.soskate.api.services.booking.BookingParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Participations", description = "Gestion des participations aux réservations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/customers/{customerId}")
public class BookingParticipantController {

    private final BookingParticipantService participantService;

    // ==================== Annulation ====================

    @Operation(
            summary = "Annuler une participation",
            description = "Annule la participation d'un client à une réservation"
    )
    @PostMapping("/participations/{participantId}/cancel")
    public ResponseEntity<ParticipantResponse> cancel(
            @PathVariable Long customerId,
            @PathVariable Long participantId,
            @Valid @RequestBody ParticipantCancelRequest request
    ) {
        ParticipantResponse response = participantService.cancel(customerId, participantId, request);
        return ResponseEntity.ok(response);
    }

    // ==================== Consultation ====================

    @Operation(
            summary = "Mes réservations",
            description = "Récupère toutes les réservations d'un client"
    )
    @GetMapping("/my-bookings")
    public ResponseEntity<List<MyBookingResponse>> getMyBookings(
            @PathVariable Long customerId
    ) {
        List<MyBookingResponse> bookings = participantService.getMyBookings(customerId);
        return ResponseEntity.ok(bookings);
    }

    @Operation(
            summary = "Modifier les notes",
            description = "Met à jour les notes d'une participation"
    )
    @PatchMapping("/participations/{participationId}/notes")
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
