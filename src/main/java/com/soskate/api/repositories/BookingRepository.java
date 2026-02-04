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
 * Repository pour la gestion des réservations (bookings).
 * Fournit les requêtes pour le planning des instructeurs et les réservations clients.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    /**
     * Trouve toutes les réservations d'un instructeur.
     *
     * @param instructorId l'ID de l'instructeur
     * @return liste de toutes les réservations
     */
    List<BookingEntity> findByInstructorId(Long instructorId);

    /**
     * Trouve les réservations non annulées d'un instructeur pour une date donnée.
     * Utilisé pour la validation des conflits lors de la création de booking.
     *
     * @param instructorId l'ID de l'instructeur
     * @param date la date recherchée
     * @return liste des réservations non annulées
     */
    @Query("SELECT b FROM BookingEntity b WHERE b.instructor.id = :instructorId " +
            "AND DATE(b.startTime) = :date " +
            "AND b.status != 'CANCELLED'")
    List<BookingEntity> findByInstructorAndDateNotCancelled(
            @Param("instructorId") Long instructorId,
            @Param("date") LocalDate date
    );

    /**
     * Trouve les réservations à venir d'un instructeur.
     * Inclut les statuts OPEN, FULL et CONFIRMED.
     *
     * @param instructorId l'ID de l'instructeur
     * @param now la date/heure actuelle
     * @return liste des réservations futures triées par date
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
     * Trouve les réservations passées et complétées d'un instructeur.
     *
     * @param instructorId l'ID de l'instructeur
     * @param now la date/heure actuelle
     * @return liste des réservations passées triées par date
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
     * Récupère les bookings non annulés d'un instructeur pour une période donnée.
     * Utilisé pour calculer les créneaux réellement disponibles (planning et réservation).
     *
     * @param instructorId l'ID de l'instructeur
     * @param start début de la période (inclus)
     * @param end fin de la période (exclus)
     * @return liste des bookings triés par heure de début
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
     * Récupère les bookings passés qui n'ont pas encore été marqués comme COMPLETED.
     * Utilisé par les jobs de mise à jour automatique des statuts.
     *
     * @param now la date/heure actuelle
     * @return liste des bookings à mettre à jour
     */
    @Query("""
        SELECT b FROM BookingEntity b
        WHERE b.startTime < :now
        AND b.status != 'COMPLETED'
        AND b.status != 'CANCELLED'
    """)
    List<BookingEntity> findPassedAndNotCompleted(@Param("now") LocalDateTime now);
}
