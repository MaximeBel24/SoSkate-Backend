package com.soskate.api.repositories;

import com.soskate.api.entities.PlatformSettingsEntity;
import com.soskate.api.exceptions.common.PlatformSettingsNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformSettingsRepository extends JpaRepository<PlatformSettingsEntity, Long> {

    default PlatformSettingsEntity getSettings() {
        return findById(1L).orElseThrow(() ->
                new PlatformSettingsNotFoundException("Platform settings not found")
        );
    }
}