package com.soskate.api.services.service;

import com.soskate.api.dtos.service.ServiceResponseDto;
import com.soskate.api.dtos.service.ServiceRequestDto;
import com.soskate.api.entities.ServiceEntity;
import com.soskate.api.exceptions.service.ServiceAlreadyExistsException;
import com.soskate.api.exceptions.service.ServiceDataRetrievalException;
import com.soskate.api.exceptions.service.ServiceNotFoundException;
import com.soskate.api.mappers.ServiceMapper;
import com.soskate.api.repositories.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceServiceImpl implements ServiceService {

    private final Logger log = LoggerFactory.getLogger(ServiceServiceImpl.class);

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    public ServiceServiceImpl(ServiceRepository serviceRepository, ServiceMapper serviceMapper) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponseDto> getAllServices() {
        log.info("Invoking getAllServices()");
        try {
            List<ServiceEntity> services = serviceRepository.findAll();
            log.debug("Found {} services in database", services.size());

            return services.stream()
                    .map(serviceMapper::serviceToServiceDto)
                    .toList();
        } catch (DataAccessException e) {
            log.error("Database error while retrieving all services", e);
            throw ServiceDataRetrievalException.forOperation("retrieve all services", e);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving all services", e);
            throw new ServiceDataRetrievalException("Unexpected error occurred while retrieving services", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponseDto getServiceById(Long id) {
        log.info("Invoking getServiceById with id={}", id);

        if (id == null) {
            log.warn("Attempted to get service with null id");
            throw new IllegalArgumentException("Service id cannot be null");
        }

        try {
            Optional<ServiceEntity> service = serviceRepository.findById(id);
            if (service.isEmpty()) {
                log.warn("Could not find service with id={}", id);
                throw new ServiceNotFoundException(id);
            }

            log.debug("Successfully found service: {}", service.get().getName());
            return serviceMapper.serviceToServiceDto(service.get());

        } catch (ServiceNotFoundException e) {
            // Re-lance l'exception métier sans la wrapper
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while retrieving service with id={}", id, e);
            throw ServiceDataRetrievalException.forServiceId(id, e);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving service with id={}", id, e);
            throw new ServiceDataRetrievalException(
                    String.format("Unexpected error occurred while retrieving service with id %d", id), e);
        }
    }

    @Override
    @Transactional
    public ServiceResponseDto createService(ServiceRequestDto serviceToCreate) {
        log.info("Invoking createService with serviceToCreate={}", serviceToCreate);

        if (serviceToCreate == null) {
            log.warn("Attempted to create service with null DTO");
            throw new IllegalArgumentException("ServiceRequestDto cannot be null");
        }

        try {
            // Vérification d'unicité
            Optional<ServiceEntity> existingService = serviceRepository.findOneByNameIgnoreCaseAndType(
                    serviceToCreate.name(),
                    serviceToCreate.type()
            );

            if (existingService.isPresent()) {
                log.warn("Service to create with name='{}' and type='{}' already exists with id={}",
                        serviceToCreate.name(), serviceToCreate.type(), existingService.get().getId());
                throw new ServiceAlreadyExistsException(
                        serviceToCreate.name(),
                        serviceToCreate.type()
                );
            }

            // Création de l'entité
            ServiceEntity serviceToRegister = new ServiceEntity(
                    serviceToCreate.name(),
                    serviceToCreate.type(),
                    serviceToCreate.description(),
                    serviceToCreate.durationMin(),
                    serviceToCreate.basePriceCents(),
                    serviceToCreate.isActive()
            );

            log.debug("Saving new service entity: {}", serviceToRegister.getName());
            ServiceEntity registeredService = serviceRepository.save(serviceToRegister);
            log.info("Successfully created service with id={}", registeredService.getId());

            return serviceMapper.serviceToServiceDto(registeredService);

        } catch (ServiceAlreadyExistsException e) {
            // Re-lance l'exception métier sans la wrapper
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while creating service: {}", serviceToCreate, e);
            throw ServiceDataRetrievalException.forOperation("create service", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating service: {}", serviceToCreate, e);
            throw new ServiceDataRetrievalException("Unexpected error occurred while creating service", e);
        }
    }

    @Override
    @Transactional
    public ServiceResponseDto updateService(Long id, ServiceRequestDto serviceToUpdate) {
        log.info("Invoking updateService with id={} and serviceToUpdate={}", id, serviceToUpdate);

        if (id == null) {
            log.warn("Attempted to update service with null id");
            throw new IllegalArgumentException("Service id cannot be null");
        }

        if (serviceToUpdate == null) {
            log.warn("Attempted to update service with null DTO");
            throw new IllegalArgumentException("ServiceRequestDto cannot be null");
        }

        try {
            // Vérification d'existence
            Optional<ServiceEntity> existingService = serviceRepository.findById(id);
            if (existingService.isEmpty()) {
                log.warn("Could not find service to update with id={}", id);
                throw new ServiceNotFoundException(id);
            }

            // Vérification d'unicité (exclure le service actuel)
            Optional<ServiceEntity> potentiallyDuplicatedService = serviceRepository.findOneByNameIgnoreCaseAndType(
                    serviceToUpdate.name(),
                    serviceToUpdate.type()
            );

            if (potentiallyDuplicatedService.isPresent() &&
                    !potentiallyDuplicatedService.get().getId().equals(id)) {
                log.warn("Service to update with name='{}' and type='{}' already exists with different id={}",
                        serviceToUpdate.name(), serviceToUpdate.type(), potentiallyDuplicatedService.get().getId());
                throw new ServiceAlreadyExistsException(
                        serviceToUpdate.name(),
                        serviceToUpdate.type()
                );
            }

            // Mise à jour de l'entité
            ServiceEntity entityToUpdate = existingService.get();
            log.debug("Updating service entity: {} -> {}", entityToUpdate.getName(), serviceToUpdate.name());

            entityToUpdate.setName(serviceToUpdate.name());
            entityToUpdate.setType(serviceToUpdate.type());
            entityToUpdate.setDescription(serviceToUpdate.description());
            entityToUpdate.setDurationMin(serviceToUpdate.durationMin());
            entityToUpdate.setBasePriceCents(serviceToUpdate.basePriceCents());
            entityToUpdate.setIsActive(serviceToUpdate.isActive());

            ServiceEntity updatedService = serviceRepository.save(entityToUpdate);
            log.info("Successfully updated service with id={}", updatedService.getId());

            return serviceMapper.serviceToServiceDto(updatedService);

        } catch (ServiceNotFoundException | ServiceAlreadyExistsException e) {
            // Re-lance les exceptions métier sans les wrapper
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while updating service with id={}: {}", id, serviceToUpdate, e);
            throw ServiceDataRetrievalException.forServiceId(id, e);
        } catch (Exception e) {
            log.error("Unexpected error while updating service with id={}: {}", id, serviceToUpdate, e);
            throw new ServiceDataRetrievalException(
                    String.format("Unexpected error occurred while updating service with id %d", id), e);
        }
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        log.info("Invoking deleteService with id={}", id);

        if (id == null) {
            log.warn("Attempted to delete service with null id");
            throw new IllegalArgumentException("Service id cannot be null");
        }

        try {
            Optional<ServiceEntity> serviceToDelete = serviceRepository.findById(id);
            if (serviceToDelete.isEmpty()) {
                log.warn("Could not find service to delete with id={}", id);
                throw new ServiceNotFoundException(id);
            }

            log.debug("Deleting service: {}", serviceToDelete.get().getName());
            serviceRepository.delete(serviceToDelete.get());
            log.info("Successfully deleted service with id={}", id);

        } catch (ServiceNotFoundException e) {
            // Re-lance l'exception métier sans la wrapper
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while deleting service with id={}", id, e);
            throw ServiceDataRetrievalException.forServiceId(id, e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting service with id={}", id, e);
            throw new ServiceDataRetrievalException(
                    String.format("Unexpected error occurred while deleting service with id %d", id), e);
        }
    }
}