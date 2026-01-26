package com.soskate.api.dto.availability;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Représente un créneau réellement disponible (après soustraction des bookings).
 * Utilisé pour l'affichage du planning instructeur et la réservation côté client.
 */
public record AvailableSlotResponse(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {}