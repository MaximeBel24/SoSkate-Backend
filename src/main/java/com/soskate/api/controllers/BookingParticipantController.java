package com.soskate.api.controllers;

import com.soskate.api.dto.booking.ParticipantCancelRequest;
import com.soskate.api.dto.booking.ParticipantConfirmRequest;
import com.soskate.api.dto.booking.ParticipantInviteRequest;
import com.soskate.api.dto.booking.ParticipantResponse;
import com.soskate.api.services.booking.BookingParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingParticipantController {

    private final BookingParticipantService participantService;

    // ==================== Invitations (Instructeur) ====================

    @PostMapping("/bookings/{bookingId}/participants/invite")
    public ResponseEntity<ParticipantResponse> inviteByInstructor(
            @PathVariable Long bookingId,
            @Valid @RequestBody ParticipantInviteRequest request
    ) {
        ParticipantResponse response = participantService.inviteByInstructor(bookingId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== Invitations (Participant) ====================

    @PostMapping("/customers/{customerId}/bookings/{bookingId}/invite")
    public ResponseEntity<ParticipantResponse> inviteByParticipant(
            @PathVariable Long customerId,
            @PathVariable Long bookingId,
            @Valid @RequestBody ParticipantInviteRequest request
    ) {
        ParticipantResponse response = participantService.inviteByParticipant(
                bookingId,
                customerId,
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== Gestion des invitations (Customer) ====================

    @GetMapping("/customers/{customerId}/invitations")
    public ResponseEntity<List<ParticipantResponse>> getPendingInvitations(
            @PathVariable Long customerId
    ) {
        List<ParticipantResponse> response = participantService.getPendingInvitations(customerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/customers/{customerId}/invitations/{participantId}/accept")
    public ResponseEntity<ParticipantResponse> acceptInvitation(
            @PathVariable Long customerId,
            @PathVariable Long participantId
    ) {
        ParticipantResponse response = participantService.acceptInvitation(customerId, participantId);
        return ResponseEntity.ok(response);
    }

    // ==================== Confirmation et paiement ====================

    @PostMapping("/customers/{customerId}/participations/{participantId}/confirm")
    public ResponseEntity<ParticipantResponse> confirm(
            @PathVariable Long customerId,
            @PathVariable Long participantId,
            @Valid @RequestBody ParticipantConfirmRequest request
    ) {
        ParticipantResponse response = participantService.confirm(customerId, participantId, request);
        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/bookings/{bookingId}/participants")
    public ResponseEntity<List<ParticipantResponse>> getByBooking(@PathVariable Long bookingId) {
        List<ParticipantResponse> response = participantService.getByBooking(bookingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customers/{customerId}/bookings")
    public ResponseEntity<List<ParticipantResponse>> getUpcomingByCustomer(
            @PathVariable Long customerId,
            @RequestParam(required = false, defaultValue = "false") Boolean upcomingOnly
    ) {
        List<ParticipantResponse> response;

        if (upcomingOnly) {
            response = participantService.getUpcomingByCustomer(customerId);
        } else {
            response = participantService.getCompletedByCustomer(customerId);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/customers/{customerId}/bookings/history")
    public ResponseEntity<List<ParticipantResponse>> getHistoryByCustomer(
            @PathVariable Long customerId
    ) {
        List<ParticipantResponse> response = participantService.getCompletedByCustomer(customerId);
        return ResponseEntity.ok(response);
    }
}