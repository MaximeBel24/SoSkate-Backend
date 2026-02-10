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
 * Repository for spot management.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface SpotRepository extends JpaRepository<SpotEntity, Long> {

    /**
     * Finds all active spots.
     *
     * @return list of active spots
     */
    List<SpotEntity> findByIsActiveTrue();

    /**
     * Finds spots by city (active only).
     *
     * @param city the city name
     * @return list of active spots in this city
     */
    List<SpotEntity> findByCityContainingIgnoreCaseAndIsActiveTrue(String city);

    /**
     * Finds active indoor or outdoor spots.
     *
     * @param isIndoor true for indoor, false for outdoor
     * @return list of matching active spots
     */
    List<SpotEntity> findByIsIndoorAndIsActiveTrue(Boolean isIndoor);

    /**
     * Checks if a spot already exists with this name and address.
     *
     * @param name the spot name
     * @param address the spot address
     * @return true if a spot exists
     */
    boolean existsByNameAndAddress(String name, String address);

    /**
     * Finds spots near a GPS position (radius in km).
     * Uses the Haversine formula to calculate distance.
     *
     * @param latitude latitude of the reference point
     * @param longitude longitude of the reference point
     * @param radiusKm search radius in kilometers
     * @return list of spots within the radius
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