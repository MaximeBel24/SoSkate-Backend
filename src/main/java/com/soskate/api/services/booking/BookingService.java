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
    BookingResponse createBooking(Long customerId, BookingCreateRequest request);

    /**
     * Gets a booking by ID.
     *
     * @param id The booking ID
     * @return The booking
     */
    BookingResponse getBookingById(Long id);

    /**
     * Gets all bookings for an instructor.
     *
     * @param instructorId The instructor ID
     * @return List of bookings
     */
    List<BookingResponse> getBookingsByInstructor(Long instructorId);

    /**
     * Gets upcoming bookings for an instructor.
     *
     * @param instructorId The instructor ID
     * @return List of upcoming bookings
     */
    List<BookingResponse> getUpcomingBookingsByInstructor(Long instructorId);

    /**
     * Gets passed bookings for an instructor.
     *
     * @param instructorId The instructor ID
     * @return List of passed bookings
     */
    List<BookingResponse> getPassedBookingsByInstructor(Long instructorId);

    /**
     * Cancels a booking.
     *
     * @param instructorId The instructor ID (for authorization)
     * @param bookingId    The booking ID
     * @return The updated booking
     */
    BookingResponse cancelBooking(Long instructorId, Long bookingId);


}
