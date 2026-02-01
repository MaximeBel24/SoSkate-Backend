package com.soskate.api.repositories;

import com.soskate.api.entities.ServiceEntity;
import com.soskate.api.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    /**
     * Trouve tous les services actifs.
     *
     * @return liste des services actifs
     */
    List<ServiceEntity> findByIsActiveTrue();

    /**
     * Trouve les services par type.
     *
     * @param type le type de service (LESSON, RENTAL)
     * @return liste des services de ce type
     */
    List<ServiceEntity> findByType(ServiceType type);

    /**
     * Vérifie si un service avec ce nom existe déjà.
     *
     * @param name le nom du service
     * @return true si le nom existe
     */
    boolean existsByName(String name);
}