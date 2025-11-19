package com.soskate.api.services.spot;

import com.soskate.api.dto.spot.SpotRequestDTO;
import com.soskate.api.dto.spot.SpotResponseDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service pour la gestion des spots de skateboard.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public interface SpotService {

    /**
     * Crée un nouveau spot.
     *
     * @param requestDTO les données du spot à créer
     * @return le spot créé
     */
    SpotResponseDTO createSpot(SpotRequestDTO requestDTO);

    /**
     * Récupère tous les spots.
     *
     * @return liste de tous les spots
     */
    List<SpotResponseDTO> getAllSpots();

    /**
     * Récupère uniquement les spots actifs.
     *
     * @return liste des spots actifs
     */
    List<SpotResponseDTO> getActiveSpots();

    /**
     * Récupère un spot par son ID.
     *
     * @param id l'identifiant du spot
     * @return le spot trouvé
     */
    SpotResponseDTO getSpotById(Long id);

    /**
     * Récupère les spots par ville.
     *
     * @param city le nom de la ville
     * @return liste des spots dans cette ville
     */
    List<SpotResponseDTO> getSpotsByCity(String city);

    /**
     * Récupère les spots indoor ou outdoor.
     *
     * @param isIndoor true pour indoor, false pour outdoor
     * @return liste des spots correspondants
     */
    List<SpotResponseDTO> getSpotsByType(Boolean isIndoor);

    /**
     * Récupère les spots à proximité d'une position GPS.
     *
     * @param latitude latitude du point de référence
     * @param longitude longitude du point de référence
     * @param radiusKm rayon de recherche en kilomètres
     * @return liste des spots dans le rayon
     */
    List<SpotResponseDTO> getSpotsNearby(BigDecimal latitude, BigDecimal longitude, double radiusKm);

    /**
     * Met à jour un spot existant.
     *
     * @param id l'identifiant du spot
     * @param requestDTO les nouvelles données
     * @return le spot mis à jour
     */
    SpotResponseDTO updateSpot(Long id, SpotRequestDTO requestDTO);

    /**
     * Désactive un spot (soft delete).
     *
     * @param id l'identifiant du spot
     */
    void deactivateSpot(Long id);

    /**
     * Supprime définitivement un spot.
     *
     * @param id l'identifiant du spot
     */
    void deleteSpot(Long id);
}