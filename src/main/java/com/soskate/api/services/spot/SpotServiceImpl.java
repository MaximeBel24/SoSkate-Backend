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
 * Implémentation du service de gestion des spots.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpotServiceImpl implements SpotService {

    private final SpotRepository spotRepository;

    @Override
    @Transactional
    public SpotResponse createSpot(SpotRequest request) {
        log.info("Création d'un nouveau spot : {}", request.name());

        if (spotRepository.existsByNameAndAddress(request.name(), request.address())) {
            log.warn("Tentative de création d'un spot déjà existant : {} à {}",
                    request.name(), request.address());
            throw new DuplicateSpotException(request.name(), request.address());
        }

        validateCoordinates(request.latitude(), request.longitude());

        SpotEntity spot = SpotMapper.toEntity(request);
        SpotEntity savedSpot = spotRepository.save(spot);

        log.info("Spot créé avec succès : ID {}", savedSpot.getId());

        return SpotMapper.toResponse(savedSpot);
    }

    @Override
    public List<SpotResponse> getAllSpots() {
        log.debug("Récupération de tous les spots");

        return spotRepository.findAll()
                .stream()
                .map(SpotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpotResponse> getActiveSpots() {
        log.debug("Récupération des spots actifs");

        return spotRepository.findByIsActiveTrue()
                .stream()
                .map(SpotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SpotResponse getSpotById(Long id) {
        log.debug("Récupération du spot avec l'ID : {}", id);

        SpotEntity spot = spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        return SpotMapper.toResponse(spot);
    }

    @Override
    public List<SpotResponse> getSpotsByCity(String city) {
        log.debug("Récupération des spots dans la ville : {}", city);

        return spotRepository.findByCityContainingIgnoreCaseAndIsActiveTrue(city)
                .stream()
                .map(SpotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpotResponse> getSpotsByType(Boolean isIndoor) {
        log.debug("Récupération des spots {} actifs", isIndoor ? "indoor" : "outdoor");

        return spotRepository.findByIsIndoorAndIsActiveTrue(isIndoor)
                .stream()
                .map(SpotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpotResponse> getSpotsNearby(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        log.debug("Recherche de spots dans un rayon de {} km autour de ({}, {})",
                radiusKm, latitude, longitude);

        // Valider les coordonnées
        validateCoordinates(latitude, longitude);

        // Valider le rayon
        if (radiusKm <= 0 || radiusKm > 100) {
            throw new InvalidCoordinatesException(
                    "Le rayon de recherche doit être compris entre 0 et 100 km"
            );
        }

        List<SpotEntity> spots = spotRepository.findSpotsNearby(latitude, longitude, radiusKm);

        log.info("{} spot(s) trouvé(s) dans un rayon de {} km", spots.size(), radiusKm);

        return spots.stream()
                .map(SpotMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SpotResponse updateSpot(Long id, SpotRequest request) {
        log.info("Mise à jour du spot ID : {}", id);

        SpotEntity spotEntity = spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        if ((!spotEntity.getName().equals(request.name()) || !spotEntity.getAddress().equals(request.address()))
                && spotRepository.existsByNameAndAddress(request.name(), request.address())) {
            log.warn("Tentative de modification vers un nom/adresse déjà existant : {} à {}",
                    request.name(), request.address());
            throw new DuplicateSpotException(request.name(), request.address());
        }

        validateCoordinates(request.latitude(), request.longitude());

        SpotMapper.updateEntityFromRequest(spotEntity, request);
        SpotEntity updatedSpot = spotRepository.save(spotEntity);

        log.info("Spot mis à jour avec succès : ID {}", updatedSpot.getId());

        return SpotMapper.toResponse(updatedSpot);
    }

    @Override
    @Transactional
    public void deactivateSpot(Long id) {
        log.info("Désactivation du spot ID : {}", id);

        SpotEntity spot = spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        spot.setIsActive(false);
        spotRepository.save(spot);

        log.info("Spot désactivé avec succès : ID {}", id);
    }

    @Override
    @Transactional
    public void deleteSpot(Long id) {
        log.info("Suppression du spot ID : {}", id);

        if (!spotRepository.existsById(id)) {
            throw new SpotNotFoundException(id);
        }

        spotRepository.deleteById(id);

        log.info("Spot supprimé avec succès : ID {}", id);
    }

    /**
     * Valide les coordonnées GPS.
     *
     * @param latitude latitude à valider
     * @param longitude longitude à valider
     * @throws InvalidCoordinatesException si les coordonnées sont invalides
     */
    private void validateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            throw new InvalidCoordinatesException("La latitude et la longitude sont obligatoires");
        }

        if (latitude.compareTo(new BigDecimal("-90")) < 0 || latitude.compareTo(new BigDecimal("90")) > 0) {
            throw new InvalidCoordinatesException(
                    String.format("La latitude doit être comprise entre -90 et 90 (valeur fournie : %s)", latitude)
            );
        }

        if (longitude.compareTo(new BigDecimal("-180")) < 0 || longitude.compareTo(new BigDecimal("180")) > 0) {
            throw new InvalidCoordinatesException(
                    String.format("La longitude doit être comprise entre -180 et 180 (valeur fournie : %s)", longitude)
            );
        }
    }
}