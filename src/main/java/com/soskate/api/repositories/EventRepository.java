package com.soskate.api.repositories;

import com.soskate.api.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    /**
     * Trouve un événement par titre (insensible à la casse)
     */
    Optional<EventEntity> findOneByTitleIgnoreCase(String title);

    /**
     * Trouve tous les événements actifs
     */
    List<EventEntity> findByIsActiveTrue();

    /**
     * Trouve tous les événements d'un spot spécifique
     */
    List<EventEntity> findBySpotId(Long spotId);

    /**
     * Trouve tous les événements actifs d'un spot
     */
    List<EventEntity> findBySpotIdAndIsActiveTrue(Long spotId);

    /**
     * Trouve tous les événements à venir (qui n'ont pas encore commencé)
     */
    List<EventEntity> findByStartTimeAfterAndIsActiveTrue(LocalDateTime now);

    /**
     * Trouve tous les événements en cours (entre startTime et endTime)
     */
    @Query("SELECT e FROM EventEntity e WHERE e.startTime <= :now AND e.endTime >= :now AND e.isActive = true")
    List<EventEntity> findCurrentEvents(@Param("now") LocalDateTime now);

    /**
     * Trouve tous les événements passés
     */
    List<EventEntity> findByEndTimeBeforeAndIsActiveTrue(LocalDateTime now);

    /**
     * Trouve tous les événements dans une période donnée
     */
    @Query("SELECT e FROM EventEntity e WHERE e.startTime >= :startDate AND e.endTime <= :endDate AND e.isActive = true ORDER BY e.startTime")
    List<EventEntity> findEventsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    /**
     * Trouve tous les événements d'un spot dans une période donnée
     */
    @Query("SELECT e FROM EventEntity e WHERE e.spotId = :spotId AND e.startTime >= :startDate AND e.endTime <= :endDate AND e.isActive = true ORDER BY e.startTime")
    List<EventEntity> findEventsBySpotAndDateRange(@Param("spotId") Long spotId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Vérifie s'il y a des conflits de planning pour un spot
     * (événements qui se chevauchent sur le même spot)
     */
    @Query("SELECT e FROM EventEntity e WHERE e.spotId = :spotId AND e.isActive = true AND " +
            "((e.startTime <= :endTime AND e.endTime >= :startTime))")
    List<EventEntity> findConflictingEvents(@Param("spotId") Long spotId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * Vérifie s'il y a des conflits de planning en excluant un événement spécifique
     * (utile pour les mises à jour)
     */
    @Query("SELECT e FROM EventEntity e WHERE e.spotId = :spotId AND e.id != :excludeEventId AND e.isActive = true AND " +
            "((e.startTime <= :endTime AND e.endTime >= :startTime))")
    List<EventEntity> findConflictingEventsExcludingId(@Param("spotId") Long spotId,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime,
                                                       @Param("excludeEventId") Long excludeEventId);

    /**
     * Trouve les événements avec inscription payante
     */
    @Query("SELECT e FROM EventEntity e WHERE e.registrationPriceCents > 0 AND e.isActive = true")
    List<EventEntity> findPaidEvents();

    /**
     * Trouve les événements gratuits
     */
    @Query("SELECT e FROM EventEntity e WHERE (e.registrationPriceCents IS NULL OR e.registrationPriceCents = 0) AND e.isActive = true")
    List<EventEntity> findFreeEvents();

    /**
     * Compte le nombre d'événements actifs pour un spot
     */
    long countBySpotIdAndIsActiveTrue(Long spotId);

    /**
     * Trouve les événements par titre partiel (recherche)
     */
    List<EventEntity> findByTitleContainingIgnoreCaseAndIsActiveTrue(String titlePart);

    /**
     * Trouve tous les événements ordonnés par date de début
     */
    List<EventEntity> findByIsActiveTrueOrderByStartTimeAsc();
}