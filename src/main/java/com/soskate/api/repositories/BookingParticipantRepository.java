package com.soskate.api.repositories;

import com.soskate.api.entities.BookingParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing booking participations.
 * Handles the relationship between customers and their bookings.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface BookingParticipantRepository extends JpaRepository<BookingParticipantEntity, Long> {

    /**
     * Finds all participations for a given booking.
     *
     * @param bookingId the booking ID
     * @return list of participations
     */
    List<BookingParticipantEntity> findByBookingId(Long bookingId);

    /**
     * Counts the total number of confirmed participants for a booking.
     * Uses COALESCE to return 0 if there are no participants.
     *
     * @param bookingId the booking ID
     * @return total number of confirmed participants
     */
    @Query("""
        SELECT COALESCE(SUM(bp.numberOfParticipants), 0)
        FROM BookingParticipantEntity bp
        WHERE bp.booking.id = :bookingId
        AND bp.status = 'CONFIRMED'
    """)
    int countConfirmedParticipants(@Param("bookingId") Long bookingId);

    /**
     * Retrieves all participations for a customer with full details.
     * Uses JOIN FETCH to avoid N+1 problems.
     *
     * @param customerId the customer ID
     * @return list of participations with booking, instructor, spot, and service
     */
    @Query("SELECT bp FROM BookingParticipantEntity bp " +
            "JOIN FETCH bp.booking b " +
            "JOIN FETCH b.instructor " +
            "JOIN FETCH b.spot " +
            "JOIN FETCH b.service " +
            "WHERE bp.customer.id = :customerId " +
            "ORDER BY b.startTime DESC")
    List<BookingParticipantEntity> findAllByCustomerIdWithDetails(@Param("customerId") Long customerId);

    /**
     * Finds a participation by its ID and the customer ID.
     * Used to verify that a customer is accessing their own participation.
     *
     * @param id the participation ID
     * @param customerId the customer ID
     * @return Optional containing the participation if it exists and belongs to the customer
     */
    Optional<BookingParticipantEntity> findByIdAndCustomerId(Long id, Long customerId);

}
