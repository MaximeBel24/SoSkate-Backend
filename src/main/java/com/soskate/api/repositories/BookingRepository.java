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
     * Récupère les bookings non annulés d'un instructeur pour une période donnée.
     * Utilisé pour calculer les créneaux réellement disponibles (planning et réservation).
     *
     * @param instructorId ID de l'instructeur
     * @param start Début de la période (inclus)
     * @param end Fin de la période (exclus) - pour une date unique, utiliser date.atStartOfDay() et date.plusDays(1).atStartOfDay()
     * @return Liste des bookings triés par heure de début
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
     * Récupère les bookings qui sont passés mais qui n'ont pas le status COMPLETED
     */
    @Query("""
        SELECT b FROM BookingEntity b
        WHERE b.startTime < :now
        AND b.status != 'COMPLETED'
        AND b.status != 'CANCELLED'
    """)
    List<BookingEntity> findPassedAndNotCompleted(@Param("now") LocalDateTime now);
}