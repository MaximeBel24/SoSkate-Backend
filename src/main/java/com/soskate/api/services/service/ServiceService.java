package com.soskate.api.services.service;

import com.soskate.api.dto.service.ServiceRequest;
import com.soskate.api.dto.service.ServiceResponse;
import com.soskate.api.enums.ServiceType;

import java.util.List;

/**
 * Service for skateboard service/lesson management.
 *
 * @author SoSkate Team
 * @version 1.0
 */
public interface ServiceService {

    /**
     * Creates a new service.
     *
     * @param requestDTO the service data to create
     * @return the created service
     */
    ServiceResponse createService(ServiceRequest requestDTO);

    /**
     * Retrieves all services.
     *
     * @return list of all services
     */
    List<ServiceResponse> getAllServices();

    /**
     * Retrieves only active services.
     *
     * @return list of active services
     */
    List<ServiceResponse> getActiveServices();

    /**
     * Retrieves a service by its ID.
     *
     * @param id the service identifier
     * @return the found service
     */
    ServiceResponse getServiceById(Long id);

    /**
     * Retrieves services by type.
     *
     * @param type the service type (LESSON, RENTAL)
     * @return list of services of this type
     */
    List<ServiceResponse> getServicesByType(ServiceType type);

    /**
     * Updates an existing service.
     *
     * @param id the service identifier
     * @param requestDTO the new data
     * @return the updated service
     */
    ServiceResponse updateService(Long id, ServiceRequest requestDTO);

    /**
     * Deactivates a service (soft delete).
     *
     * @param id the service identifier
     */
    void deactivateService(Long id);

    /**
     * Permanently deletes a service.
     *
     * @param id the service identifier
     */
    void deleteService(Long id);
}