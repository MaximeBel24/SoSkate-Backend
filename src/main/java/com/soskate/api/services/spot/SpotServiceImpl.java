package com.soskate.api.services.spot;

import com.soskate.api.dtos.spot.SpotListDTO;
import com.soskate.api.dtos.spot.SpotRequestDTO;
import com.soskate.api.dtos.spot.SpotResponseDTO;
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
    public SpotResponseDTO createSpot(SpotRequestDTO requestDTO) {
        log.info("Création d'un nouveau spot : {}", requestDTO.name());

        if (spotRepository.existsByNameAndAddress(requestDTO.name(), requestDTO.address())) {
            log.warn("Tentative de création d'un spot déjà existant : {} à {}",
                    requestDTO.name(), requestDTO.address());
            throw new DuplicateSpotException(requestDTO.name(), requestDTO.address());
        }

        validateCoordinates(requestDTO.latitude(), requestDTO.longitude());

        SpotEntity spot = SpotMapper.spotRequestDTOtoSpotEntity(requestDTO);
        SpotEntity savedSpot = spotRepository.save(spot);

        log.info("Spot créé avec succès : ID {}", savedSpot.getId());

        return SpotMapper.spotEntityToSpotResponseDTO(savedSpot);
    }

    @Override
    public List<SpotResponseDTO> getAllSpots() {
        log.debug("Récupération de tous les spots");

        return spotRepository.findAll()
                .stream()
                .map(SpotMapper::spotEntityToSpotResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpotResponseDTO> getActiveSpots() {
        log.debug("Récupération des spots actifs");

        return spotRepository.findByIsActiveTrue()
                .stream()
                .map(SpotMapper::spotEntityToSpotResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpotListDTO> getActiveSpotsForMap() {
        log.debug("Récupération des spots actifs pour la carte");

        return spotRepository.findByIsActiveTrue()
                .stream()
                .map(SpotMapper::spotEntityToSpotListDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SpotResponseDTO getSpotById(Long id) {
        log.debug("Récupération du spot avec l'ID : {}", id);

        SpotEntity spot = spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        return SpotMapper.spotEntityToSpotResponseDTO(spot);
    }

    @Override
    public List<SpotResponseDTO> getSpotsByCity(String city) {
        log.debug("Récupération des spots dans la ville : {}", city);

        return spotRepository.findByCityContainingIgnoreCaseAndIsActiveTrue(city)
                .stream()
                .map(SpotMapper::spotEntityToSpotResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpotResponseDTO> getSpotsByType(Boolean isIndoor) {
        log.debug("Récupération des spots {} actifs", isIndoor ? "indoor" : "outdoor");

        return spotRepository.findByIsIndoorAndIsActiveTrue(isIndoor)
                .stream()
                .map(SpotMapper::spotEntityToSpotResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpotListDTO> getSpotsNearby(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
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
                .map(SpotMapper::spotEntityToSpotListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SpotResponseDTO updateSpot(Long id, SpotRequestDTO requestDTO) {
        log.info("Mise à jour du spot ID : {}", id);

        SpotEntity spot = spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        if ((!spot.getName().equals(requestDTO.name()) || !spot.getAddress().equals(requestDTO.address()))
                && spotRepository.existsByNameAndAddress(requestDTO.name(), requestDTO.address())) {
            log.warn("Tentative de modification vers un nom/adresse déjà existant : {} à {}",
                    requestDTO.name(), requestDTO.address());
            throw new DuplicateSpotException(requestDTO.name(), requestDTO.address());
        }

        validateCoordinates(requestDTO.latitude(), requestDTO.longitude());

        SpotMapper.updateEntityFromDTO(spot, requestDTO);
        SpotEntity updatedSpot = spotRepository.save(spot);

        log.info("Spot mis à jour avec succès : ID {}", updatedSpot.getId());

        return SpotMapper.spotEntityToSpotResponseDTO(updatedSpot);
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