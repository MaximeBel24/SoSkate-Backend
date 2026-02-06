package com.soskate.api.controllers;

import com.soskate.api.dto.booking.BookingCreateRequest;
import com.soskate.api.dto.booking.BookingResponse;
import com.soskate.api.services.booking.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bookings", description = "Gestion des réservations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Operation(
            summary = "Créer une réservation",
            description = "Crée une nouvelle réservation pour un client"
    )
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingCreateRequest request
    ) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Récupère une réservation",
            description = "Récupère une réservation par son identifiant"
    )
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Annule une réservation",
            description = "Annule une réservation par son instructeur"
    )
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelByInstructor(
            // TODO : récupérer instructorId du contexte de sécurité

            @RequestParam Long instructorId,
            @PathVariable Long bookingId
    ) {
        BookingResponse response = bookingService.cancelBooking(instructorId, bookingId);
        return ResponseEntity.ok(response);
    }
}