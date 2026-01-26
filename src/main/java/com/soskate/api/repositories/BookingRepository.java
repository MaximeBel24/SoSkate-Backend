package com.soskate.api.repositories;

import com.soskate.api.entities.BookingEntity;
import com.soskate.api.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByInstructorId(Long instructorId);

    @Query("SELECT b FROM BookingEntity b WHERE b.instructor.id = :instructorId " +
            "AND DATE(b.startTime) = :date " +
            "AND b.status != 'CANCELLED'")
    List<BookingEntity> findByInstructorAndDateNotCancelled(
            @Param("instructorId") Long instructorId,
            @Param("date") LocalDate date
    );

    @Query("""
        SELECT b FROM BookingEntity b
        WHERE b.instructor.id = :instructorId
        AND b.startTime >= :startTime
        AND b.endTime <= :endTime
        AND b.status NOT IN ('CANCELLED')
    """)
    List<BookingEntity> findByInstructorAndTimeRangeNotCancelled(
            @Param("instructorId") Long instructorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

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

    @Query("""
        SELECT b FROM BookingEntity b
        WHERE b.spot.id = :spotId
        AND b.startTime > :now
        AND b.status IN ('OPEN', 'FULL', 'CONFIRMED')
        ORDER BY b.startTime
    """)
    List<BookingEntity> findUpcomingBySpotId(
            @Param("spotId") Long spotId,
            @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT b FROM BookingEntity b
        WHERE b.instructor.id = :instructorId
        AND b.status = 'COMPLETED'
        ORDER BY b.startTime DESC
    """)
    List<BookingEntity> findCompletedByInstructorId(@Param("instructorId") Long instructorId);

    /**
     * Récupère les bookings d'un instructeur pour une période donnée.
     * Utilisé pour calculer les créneaux réellement disponibles.
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
     * Récupère les bookings d'un instructeur pour une date spécifique.
     */
    @Query("""
        SELECT b FROM BookingEntity b
        WHERE b.instructor.id = :instructorId
        AND CAST(b.startTime AS LocalDate) = :date
        AND b.status != 'CANCELLED'
        ORDER BY b.startTime
    """)
    List<BookingEntity> findByInstructorIdAndDate(
            @Param("instructorId") Long instructorId,
            @Param("date") java.time.LocalDate date
    );

    // ============================================
    // AUTRES MÉTHODES UTILES
    // ============================================

    /**
     * Vérifie si un booking existe déjà pour un créneau donné.
     */
    @Query("""
        SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
        FROM BookingEntity b
        WHERE b.instructor.id = :instructorId
        AND b.startTime < :endTime
        AND b.endTime > :startTime
        AND b.status != 'CANCELLED'
    """)
    boolean existsOverlappingBooking(
            @Param("instructorId") Long instructorId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}