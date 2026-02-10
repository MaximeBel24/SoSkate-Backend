package com.soskate.api.services.spot;

import com.soskate.api.dto.spot.SpotRequest;
import com.soskate.api.dto.spot.SpotResponse;
import com.soskate.api.entities.SpotEntity;
import com.soskate.api.exceptions.spot.DuplicateSpotException;
import com.soskate.api.exceptions.spot.InvalidCoordinatesException;
import com.soskate.api.exceptions.spot.SpotNotFoundException;
import com.soskate.api.mappers.SpotMapper;
import com.soskate.api.repositories.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the spot management service.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpotServiceImpl implements SpotService {

    private final SpotRepository spotRepository;
    private final SpotMapper spotMapper;

    @Override
    @Transactional
    public SpotResponse createSpot(SpotRequest request) {
        log.info("Creating new spot: {}", request.name());

        if (spotRepository.existsByNameAndAddress(request.name(), request.address())) {
            log.warn("Attempt to create an already existing spot: {} at {}",
                    request.name(), request.address());
            throw new DuplicateSpotException(request.name(), request.address());
        }

        validateCoordinates(request.latitude(), request.longitude());

        SpotEntity spot = spotMapper.toEntity(request);
        SpotEntity savedSpot = spotRepository.save(spot);

        log.info("Spot created successfully: ID {}", savedSpot.getId());

        return spotMapper.toResponse(savedSpot);
    }

    @Override
    public List<SpotResponse> getAllSpots() {
        log.debug("Retrieving all spots");

        return spotRepository.findAll()
                .stream()
                .map(spotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpotResponse> getActiveSpots() {
        log.debug("Retrieving active spots");

        return spotRepository.findByIsActiveTrue()
                .stream()
                .map(spotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SpotResponse getSpotById(Long id) {
        log.debug("Retrieving spot with ID: {}", id);

        SpotEntity spot = spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        return spotMapper.toResponse(spot);
    }

    @Override
    public List<SpotResponse> getSpotsByCity(String city) {
        log.debug("Retrieving spots in city: {}", city);

        return spotRepository.findByCityContainingIgnoreCaseAndIsActiveTrue(city)
                .stream()
                .map(spotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpotResponse> getSpotsByType(Boolean isIndoor) {
        log.debug("Retrieving active {} spots", isIndoor ? "indoor" : "outdoor");

        return spotRepository.findByIsIndoorAndIsActiveTrue(isIndoor)
                .stream()
                .map(spotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpotResponse> getSpotsNearby(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        log.debug("Searching for spots within a {} km radius around ({}, {})",
                radiusKm, latitude, longitude);

        // Validate coordinates
        validateCoordinates(latitude, longitude);

        // Validate radius
        if (radiusKm <= 0 || radiusKm > 100) {
            throw new InvalidCoordinatesException(
                    "Search radius must be between 0 and 100 km"
            );
        }

        List<SpotEntity> spots = spotRepository.findSpotsNearby(latitude, longitude, radiusKm);

        log.info("{} spot(s) found within a {} km radius", spots.size(), radiusKm);

        return spots.stream()
                .map(spotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SpotResponse updateSpot(Long id, SpotRequest request) {
        log.info("Updating spot ID: {}", id);

        SpotEntity spotEntity = spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        if ((!spotEntity.getName().equals(request.name()) || !spotEntity.getAddress().equals(request.address()))
                && spotRepository.existsByNameAndAddress(request.name(), request.address())) {
            log.warn("Attempt to update to an already existing name/address: {} at {}",
                    request.name(), request.address());
            throw new DuplicateSpotException(request.name(), request.address());
        }

        validateCoordinates(request.latitude(), request.longitude());

        spotMapper.updateEntityFromRequest(spotEntity, request);
        SpotEntity updatedSpot = spotRepository.save(spotEntity);

        log.info("Spot updated successfully: ID {}", updatedSpot.getId());

        return spotMapper.toResponse(updatedSpot);
    }

    @Override
    @Transactional
    public void deactivateSpot(Long id) {
        log.info("Deactivating spot ID: {}", id);

        SpotEntity spot = spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        spot.setIsActive(false);
        spotRepository.save(spot);

        log.info("Spot deactivated successfully: ID {}", id);
    }

    @Override
    @Transactional
    public void deleteSpot(Long id) {
        log.info("Deleting spot ID: {}", id);

        if (!spotRepository.existsById(id)) {
            throw new SpotNotFoundException(id);
        }

        spotRepository.deleteById(id);

        log.info("Spot deleted successfully: ID {}", id);
    }

    /**
     * Validates GPS coordinates.
     *
     * @param latitude latitude to validate
     * @param longitude longitude to validate
     * @throws InvalidCoordinatesException if coordinates are invalid
     */
    private void validateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            throw new InvalidCoordinatesException("Latitude and longitude are required");
        }

        if (latitude.compareTo(new BigDecimal("-90")) < 0 || latitude.compareTo(new BigDecimal("90")) > 0) {
            throw new InvalidCoordinatesException(
                    String.format("Latitude must be between -90 and 90 (provided value: %s)", latitude)
            );
        }

        if (longitude.compareTo(new BigDecimal("-180")) < 0 || longitude.compareTo(new BigDecimal("180")) > 0) {
            throw new InvalidCoordinatesException(
                    String.format("Longitude must be between -180 and 180 (provided value: %s)", longitude)
            );
        }
    }
}