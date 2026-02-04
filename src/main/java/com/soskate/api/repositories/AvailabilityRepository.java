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
 * Repository pour la gestion des disponibilités des instructeurs.
 * Gère les créneaux horaires pendant lesquels un instructeur peut recevoir des réservations.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface AvailabilityRepository extends JpaRepository<AvailabilityEntity, Long> {

    /**
     * Trouve les disponibilités d'un instructeur avec un statut donné.
     *
     * @param instructorId l'ID de l'instructeur
     * @param status le statut recherché
     * @return liste des disponibilités correspondantes
     */
    List<AvailabilityEntity> findByInstructorIdAndStatus(
            Long instructorId,
            AvailabilityStatus status
    );

    /**
     * Trouve les disponibilités actives d'un instructeur pour une date donnée.
     *
     * @param instructorId l'ID de l'instructeur
     * @param date la date recherchée
     * @return liste des disponibilités triées par heure de début
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
     * Récupère les disponibilités d'un instructeur pour une plage de dates.
     *
     * @param instructorId l'ID de l'instructeur
     * @param startDate date de début (incluse)
     * @param endDate date de fin (incluse)
     * @return liste des disponibilités triées par date et heure de début
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
     * Vérifie si une disponibilité chevauche une plage horaire existante.
     * Utilisé pour éviter les doublons lors de la création de disponibilités.
     *
     * @param instructorId l'ID de l'instructeur
     * @param date la date à vérifier
     * @param startTime l'heure de début du créneau
     * @param endTime l'heure de fin du créneau
     * @return true si un chevauchement existe
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
