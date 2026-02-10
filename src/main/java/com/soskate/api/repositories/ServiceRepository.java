package com.soskate.api.repositories;

import com.soskate.api.entities.ServiceEntity;
import com.soskate.api.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    /**
     * Finds all active services.
     *
     * @return list of active services
     */
    List<ServiceEntity> findByIsActiveTrue();

    /**
     * Finds services by type.
     *
     * @param type the service type (LESSON, RENTAL)
     * @return list of services of this type
     */
    List<ServiceEntity> findByType(ServiceType type);

    /**
     * Checks if a service with this name already exists.
     *
     * @param name the service name
     * @return true if the name exists
     */
    boolean existsByName(String name);
}