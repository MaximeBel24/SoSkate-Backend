package com.soskate.api.services.spot;

import com.soskate.api.dto.spot.SpotRequest;
import com.soskate.api.dto.spot.SpotResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for skateboard spot management.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public interface SpotService {

    /**
     * Creates a new spot.
     *
     * @param requestDTO the spot data to create
     * @return the created spot
     */
    SpotResponse createSpot(SpotRequest requestDTO);

    /**
     * Retrieves all spots.
     *
     * @return list of all spots
     */
    List<SpotResponse> getAllSpots();

    /**
     * Retrieves only active spots.
     *
     * @return list of active spots
     */
    List<SpotResponse> getActiveSpots();

    /**
     * Retrieves a spot by its ID.
     *
     * @param id the spot identifier
     * @return the found spot
     */
    SpotResponse getSpotById(Long id);

    /**
     * Retrieves spots by city.
     *
     * @param city the city name
     * @return list of spots in this city
     */
    List<SpotResponse> getSpotsByCity(String city);

    /**
     * Retrieves indoor or outdoor spots.
     *
     * @param isIndoor true for indoor, false for outdoor
     * @return list of matching spots
     */
    List<SpotResponse> getSpotsByType(Boolean isIndoor);

    /**
     * Retrieves spots near a GPS position.
     *
     * @param latitude latitude of the reference point
     * @param longitude longitude of the reference point
     * @param radiusKm search radius in kilometers
     * @return list of spots within the radius
     */
    List<SpotResponse> getSpotsNearby(BigDecimal latitude, BigDecimal longitude, double radiusKm);

    /**
     * Updates an existing spot.
     *
     * @param id the spot identifier
     * @param requestDTO the new data
     * @return the updated spot
     */
    SpotResponse updateSpot(Long id, SpotRequest requestDTO);

    /**
     * Deactivates a spot (soft delete).
     *
     * @param id the spot identifier
     */
    void deactivateSpot(Long id);

    /**
     * Permanently deletes a spot.
     *
     * @param id the spot identifier
     */
    void deleteSpot(Long id);
}