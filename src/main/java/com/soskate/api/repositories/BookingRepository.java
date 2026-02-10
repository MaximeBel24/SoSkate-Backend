package com.soskate.api.repositories;

import com.soskate.api.entities.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing bookings.
 * Provides queries for instructor scheduling and customer bookings.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    /**
     * Finds all bookings for an instructor.
     *
     * @param instructorId the instructor ID
     * @return list of all bookings
     */
    List<BookingEntity> findByInstructorId(Long instructorId);

    /**
     * Finds non-cancelled bookings for an instructor on a given date.
     * Used for conflict validation when creating a booking.
     *
     * @param instructorId the instructor ID
     * @param date the date to search
     * @return list of non-cancelled bookings
     */
    @Query("SELECT b FROM BookingEntity b WHERE b.instructor.id = :instructorId " +
            "AND DATE(b.startTime) = :date " +
            "AND b.status != 'CANCELLED'")
    List<BookingEntity> findByInstructorAndDateNotCancelled(
            @Param("instructorId") Long instructorId,
            @Param("date") LocalDate date
    );

    /**
     * Finds upcoming bookings for an instructor.
     * Includes statuses OPEN, FULL, and CONFIRMED.
     *
     * @param instructorId the instructor ID
     * @param now the current date/time
     * @return list of future bookings sorted by date
     */
    @Query("""
        SELECT b FROM BookingEntity b
        WHERE b.instructor.id = :instructorId
        AND b.startTime > :now
        AND b.status IN ('OPEN', 'FULL', 'CONFIRMED')
        ORDER BY b.startTime
    """)
    List<BookingEntity> findUpcomingByInstructorId(
            @Param("instructorId") Long instructorId,
            @Param("now") LocalDateTime now
    );

    /**
     * Finds past and completed bookings for an instructor.
     *
     * @param instructorId the instructor ID
     * @param now the current date/time
     * @return list of past bookings sorted by date
     */
    @Query("""
        SELECT b FROM BookingEntity b
        WHERE b.instructor.id = :instructorId
        AND b.startTime < :now
        AND b.status IN ('COMPLETED')
        ORDER BY b.startTime
    """)
    List<BookingEntity> findPassedByInstructorId(
            @Param("instructorId") Long instructorId,
            @Param("now") LocalDateTime now
    );

    /**
     * Retrieves non-cancelled bookings for an instructor within a given period.
     * Used to calculate actually available time slots (scheduling and booking).
     *
     * @param instructorId the instructor ID
     * @param start start of the period (inclusive)
     * @param end end of the period (exclusive)
     * @return list of bookings sorted by start time
     */
    @Query("""
        SELECT b FROM BookingEntity b
        WHERE b.instructor.id = :instructorId
        AND b.startTime >= :start
        AND b.startTime < :end
        AND b.status != 'CANCELLED'
        ORDER BY b.startTime
    """)
    List<BookingEntity> findByInstructorIdAndStartTimeBetween(
            @Param("instructorId") Long instructorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Retrieves past bookings that have not yet been marked as COMPLETED.
     * Used by automatic status update jobs.
     *
     * @param now the current date/time
     * @return list of bookings to update
     */
    @Query("""
        SELECT b FROM BookingEntity b
        WHERE b.startTime < :now
        AND b.status != 'COMPLETED'
        AND b.status != 'CANCELLED'
    """)
    List<BookingEntity> findPassedAndNotCompleted(@Param("now") LocalDateTime now);
}
