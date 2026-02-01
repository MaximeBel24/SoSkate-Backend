package com.soskate.api.repositories;

import com.soskate.api.entities.SpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des spots.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface SpotRepository extends JpaRepository<SpotEntity, Long> {

    /**
     * Trouve tous les spots actifs.
     *
     * @return liste des spots actifs
     */
    List<SpotEntity> findByIsActiveTrue();

    /**
     * Trouve les spots par ville et actifs uniquement.
     *
     * @param city le nom de la ville
     * @return liste des spots actifs dans cette ville
     */
    List<SpotEntity> findByCityContainingIgnoreCaseAndIsActiveTrue(String city);

    /**
     * Trouve les spots actifs indoor ou outdoor.
     *
     * @param isIndoor true pour indoor, false pour outdoor
     * @return liste des spots actifs correspondants
     */
    List<SpotEntity> findByIsIndoorAndIsActiveTrue(Boolean isIndoor);

    /**
     * Vérifie si un spot existe déjà avec ce nom et cette adresse.
     *
     * @param name le nom du spot
     * @param address l'adresse du spot
     * @return true si un spot existe
     */
    boolean existsByNameAndAddress(String name, String address);

    /**
     * Trouve les spots à proximité d'une position GPS (rayon en km).
     * Utilise la formule de Haversine pour calculer la distance.
     *
     * @param latitude latitude du point de référence
     * @param longitude longitude du point de référence
     * @param radiusKm rayon de recherche en kilomètres
     * @return liste des spots dans le rayon
     */
    @Query("SELECT s FROM SpotEntity s WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
            "cos(radians(s.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
            "sin(radians(s.latitude)))) <= :radiusKm " +
            "AND s.isActive = true")
    List<SpotEntity> findSpotsNearby(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusKm") double radiusKm
    );
}