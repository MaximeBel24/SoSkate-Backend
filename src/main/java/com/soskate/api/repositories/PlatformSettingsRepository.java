package com.soskate.api.repositories;

import com.soskate.api.entities.PlatformSettingsEntity;
import com.soskate.api.exceptions.common.PlatformSettingsNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour la gestion des paramètres globaux de la plateforme.
 * Les settings sont stockés en base avec un ID fixe (singleton pattern).
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface PlatformSettingsRepository extends JpaRepository<PlatformSettingsEntity, Long> {

    /**
     * Récupère les paramètres de la plateforme.
     * Les settings ont toujours l'ID 1 (singleton en base).
     *
     * @return les paramètres de la plateforme
     * @throws PlatformSettingsNotFoundException si les settings ne sont pas configurés
     */
    default PlatformSettingsEntity getSettings() {
        return findById(1L).orElseThrow(() ->
                new PlatformSettingsNotFoundException("Platform settings not found")
        );
    }
}
