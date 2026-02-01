package com.soskate.api.repositories;

import com.soskate.api.entities.BookingParticipantEntity;
import com.soskate.api.enums.ParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingParticipantRepository extends JpaRepository<BookingParticipantEntity, Long> {

    List<BookingParticipantEntity> findByBookingId(Long bookingId);

    boolean existsByBookingIdAndCustomerId(Long bookingId, Long customerId);

    List<BookingParticipantEntity> findByBookingIdAndStatus(Long bookingId, ParticipantStatus status);

    @Query("""
        SELECT COALESCE(SUM(bp.numberOfParticipants), 0)
        FROM BookingParticipantEntity bp
        WHERE bp.booking.id = :bookingId
        AND bp.status = 'CONFIRMED'
    """)
    int countConfirmedParticipants(@Param("bookingId") Long bookingId);

    @Query("""
        SELECT bp FROM BookingParticipantEntity bp
        JOIN bp.booking b
        WHERE bp.customer.id = :customerId
        AND b.startTime > :now
        AND bp.status IN ('PENDING_PAYMENT', 'CONFIRMED')
        ORDER BY b.startTime
    """)
    List<BookingParticipantEntity> findUpcomingByCustomerId(
            @Param("customerId") Long customerId,
            @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT bp FROM BookingParticipantEntity bp
        JOIN bp.booking b
        WHERE bp.customer.id = :customerId
        AND bp.status = 'COMPLETED'
        ORDER BY b.startTime DESC
    """)
    List<BookingParticipantEntity> findCompletedByCustomerId(@Param("customerId") Long customerId);

    @Query("""
        SELECT bp FROM BookingParticipantEntity bp
        WHERE bp.customer.id = :customerId
        AND bp.status = 'INVITED'
        ORDER BY bp.createdAt DESC
    """)
    List<BookingParticipantEntity> findPendingInvitationsByCustomerId(
            @Param("customerId") Long customerId
    );

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