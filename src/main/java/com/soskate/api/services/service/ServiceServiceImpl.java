package com.soskate.api.services.service;

import com.soskate.api.dto.service.ServiceRequest;
import com.soskate.api.dto.service.ServiceResponse;
import com.soskate.api.entities.ServiceEntity;
import com.soskate.api.enums.ServiceType;
import com.soskate.api.exceptions.service.ServiceAlreadyExistsException;
import com.soskate.api.exceptions.service.ServiceNotFoundException;
import com.soskate.api.mappers.ServiceMapper;
import com.soskate.api.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the service/lesson management service.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    @Override
    @Transactional
    public ServiceResponse createService(ServiceRequest request) {
        log.info("Creating new service: {}", request.name());

        if (serviceRepository.existsByName(request.name())) {
            log.warn("Attempt to create a service with an existing name: {}", request.name());
            throw new ServiceAlreadyExistsException(request.name(), true);
        }

        ServiceEntity service = serviceMapper.toEntity(request);
        ServiceEntity savedService = serviceRepository.save(service);

        log.info("Service created successfully: ID {}", savedService.getId());

        return serviceMapper.toResponse(savedService);
    }

    @Override
    public List<ServiceResponse> getAllServices() {
        log.debug("Retrieving all services");

        return serviceRepository.findAll()
                .stream()
                .map(serviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponse> getActiveServices() {
        log.debug("Retrieving active services");

        return serviceRepository.findByIsActiveTrue()
                .stream()
                .map(serviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceResponse getServiceById(Long id) {
        log.debug("Retrieving service with ID: {}", id);

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));

        return serviceMapper.toResponse(service);
    }

    @Override
    public List<ServiceResponse> getServicesByType(ServiceType type) {
        log.debug("Retrieving services of type: {}", type);

        return serviceRepository.findByType(type)
                .stream()
                .map(serviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        log.info("Updating service ID: {}", id);

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));

        if (!service.getName().equals(request.name())
                && serviceRepository.existsByName(request.name())) {
            log.warn("Attempt to rename to an existing name: {}", request.name());
            throw new ServiceAlreadyExistsException(request.name(), true);
        }

        serviceMapper.updateEntityFromRequest(service, request);
        ServiceEntity updatedService = serviceRepository.save(service);

        log.info("Service updated successfully: ID {}", updatedService.getId());

        return serviceMapper.toResponse(updatedService);
    }

    @Override
    @Transactional
    public void deactivateService(Long id) {
        log.info("Deactivating service ID: {}", id);

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));

        service.setIsActive(false);
        serviceRepository.save(service);

        log.info("Service deactivated successfully: ID {}", id);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        log.info("Deleting service ID: {}", id);

        if (!serviceRepository.existsById(id)) {
            throw new ServiceNotFoundException(id);
        }

        serviceRepository.deleteById(id);

        log.info("Service deleted successfully: ID {}", id);
    }
}