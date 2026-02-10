package com.soskate.api.repositories;

import com.soskate.api.entities.PlatformSettingsEntity;
import com.soskate.api.exceptions.common.PlatformSettingsNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing global platform settings.
 * Settings are stored in the database with a fixed ID (singleton pattern).
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Repository
public interface PlatformSettingsRepository extends JpaRepository<PlatformSettingsEntity, Long> {

    /**
     * Retrieves the platform settings.
     * Settings always have ID 1 (singleton in database).
     *
     * @return the platform settings
     * @throws PlatformSettingsNotFoundException if the settings are not configured
     */
    default PlatformSettingsEntity getSettings() {
        return findById(1L).orElseThrow(() ->
                new PlatformSettingsNotFoundException("Platform settings not found")
        );
    }
}
