package com.soskate.api.repositories;

import com.soskate.api.entities.AvailabilityEntity;
import com.soskate.api.enums.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository for managing instructor availabilities.
 * Handles time slots during which an instructor can receive bookings.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface AvailabilityRepository extends JpaRepository<AvailabilityEntity, Long> {

    /**
     * Finds availabilities for an instructor with a given status.
     *
     * @param instructorId the instructor ID
     * @param status the status to search for
     * @return list of matching availabilities
     */
    List<AvailabilityEntity> findByInstructorIdAndStatus(
            Long instructorId,
            AvailabilityStatus status
    );

    /**
     * Finds active availabilities for an instructor on a given date.
     *
     * @param instructorId the instructor ID
     * @param date the date to search
     * @return list of availabilities sorted by start time
     */
    @Query("""
        SELECT a FROM AvailabilityEntity a
        WHERE a.instructor.id = :instructorId
        AND a.date = :date
        AND a.status = 'AVAILABLE'
        ORDER BY a.startTime
    """)
    List<AvailabilityEntity> findAvailableByInstructorAndDate(
            @Param("instructorId") Long instructorId,
            @Param("date") LocalDate date
    );

    /**
     * Retrieves availabilities for an instructor within a date range.
     *
     * @param instructorId the instructor ID
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return list of availabilities sorted by date and start time
     */
    @Query("""
        SELECT a FROM AvailabilityEntity a
        WHERE a.instructor.id = :instructorId
        AND a.date >= :startDate
        AND a.date <= :endDate
        AND a.status = 'AVAILABLE'
        ORDER BY a.date, a.startTime
    """)
    List<AvailabilityEntity> findAvailableByInstructorAndDateRange(
            @Param("instructorId") Long instructorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Checks whether an availability overlaps with an existing time range.
     * Used to prevent duplicates when creating availabilities.
     *
     * @param instructorId the instructor ID
     * @param date the date to check
     * @param startTime the start time of the slot
     * @param endTime the end time of the slot
     * @return true if an overlap exists
     */
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM AvailabilityEntity a
        WHERE a.instructor.id = :instructorId
        AND a.date = :date
        AND a.startTime < :endTime
        AND a.endTime > :startTime
        AND a.status = 'AVAILABLE'
    """)
    boolean existsOverlapping(
            @Param("instructorId") Long instructorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
