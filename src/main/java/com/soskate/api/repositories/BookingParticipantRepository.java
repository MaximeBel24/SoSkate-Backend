package com.soskate.api.repositories;

import com.soskate.api.entities.BookingParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingParticipantRepository extends JpaRepository<BookingParticipantEntity, Long> {

    List<BookingParticipantEntity> findByBookingId(Long bookingId);

    @Query("""
        SELECT COALESCE(SUM(bp.numberOfParticipants), 0)
        FROM BookingParticipantEntity bp
        WHERE bp.booking.id = :bookingId
        AND bp.status = 'CONFIRMED'
    """)
    int countConfirmedParticipants(@Param("bookingId") Long bookingId);

    @Query("SELECT bp FROM BookingParticipantEntity bp " +
            "JOIN FETCH bp.booking b " +
            "JOIN FETCH b.instructor " +
            "JOIN FETCH b.spot " +
            "JOIN FETCH b.service " +
            "WHERE bp.customer.id = :customerId " +
            "ORDER BY b.startTime DESC")
    List<BookingParticipantEntity> findAllByCustomerIdWithDetails(@Param("customerId") Long customerId);

    Optional<BookingParticipantEntity> findByIdAndCustomerId(Long id, Long customerId);

}
