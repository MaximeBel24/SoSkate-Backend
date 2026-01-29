package com.soskate.api.services.booking;

import com.soskate.api.dto.booking.BookingCreateRequest;
import com.soskate.api.dto.booking.BookingResponse;

import java.util.List;

/**
 * Service interface for booking operations.
 */
public interface BookingService {

    /**
     * Creates a new booking.
     *
     * @param customerId The customer creating the booking
     * @param request    The booking details
     * @return The created booking
     */
    BookingResponse create(Long customerId, BookingCreateRequest request);

    /**
     * Gets a booking by ID.
     *
     * @param id The booking ID
     * @return The booking
     */
    BookingResponse getById(Long id);

    /**
     * Gets all bookings for an instructor.
     *
     * @param instructorId The instructor ID
     * @return List of bookings
     */
    List<BookingResponse> getByInstructor(Long instructorId);

    /**
     * Gets upcoming bookings for an instructor.
     *
     * @param instructorId The instructor ID
     * @return List of upcoming bookings
     */
    List<BookingResponse> getUpcomingByInstructor(Long instructorId);

    /**
     * Gets passed bookings for an instructor.
     *
     * @param instructorId The instructor ID
     * @return List of passed bookings
     */
    List<BookingResponse> getPassedByInstructor(Long instructorId);

    /**
     * Gets upcoming bookings at a spot.
     *
     * @param spotId The spot ID
     * @return List of upcoming bookings
     */
    List<BookingResponse> getUpcomingBySpot(Long spotId);

    /**
     * Marks a booking as completed.
     *
     * @param instructorId The instructor ID (for authorization)
     * @param bookingId    The booking ID
     * @return The updated booking
     */
    BookingResponse complete(Long instructorId, Long bookingId);

    /**
     * Cancels a booking.
     *
     * @param instructorId The instructor ID (for authorization)
     * @param bookingId    The booking ID
     * @return The updated booking
     */
    BookingResponse cancel(Long instructorId, Long bookingId);


}
