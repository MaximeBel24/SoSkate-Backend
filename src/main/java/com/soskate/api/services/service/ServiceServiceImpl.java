package com.soskate.api.services.service;

import com.soskate.api.dtos.service.ServiceRequestDTO;
import com.soskate.api.dtos.service.ServiceResponseDTO;
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
 * Implémentation du service de gestion des services/cours.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    @Override
    @Transactional
    public ServiceResponseDTO createService(ServiceRequestDTO requestDTO) {
        log.info("Création d'un nouveau service : {}", requestDTO.name());

        if (serviceRepository.existsByName(requestDTO.name())) {
            log.warn("Tentative de création d'un service avec un nom existant : {}", requestDTO.name());
            throw new ServiceAlreadyExistsException(requestDTO.name(), true);
        }

        ServiceEntity service = ServiceMapper.serviceRequestDTOToServiceEntity(requestDTO);
        ServiceEntity savedService = serviceRepository.save(service);

        log.info("Service créé avec succès : ID {}", savedService.getId());

        return ServiceMapper.serviceEntityToServiceResponseDTO(savedService);
    }

    @Override
    public List<ServiceResponseDTO> getAllServices() {
        log.debug("Récupération de tous les services");

        return serviceRepository.findAll()
                .stream()
                .map(ServiceMapper::serviceEntityToServiceResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponseDTO> getActiveServices() {
        log.debug("Récupération des services actifs");

        return serviceRepository.findByIsActiveTrue()
                .stream()
                .map(ServiceMapper::serviceEntityToServiceResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceResponseDTO getServiceById(Long id) {
        log.debug("Récupération du service avec l'ID : {}", id);

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));

        return ServiceMapper.serviceEntityToServiceResponseDTO(service);
    }

    @Override
    public List<ServiceResponseDTO> getServicesByType(ServiceType type) {
        log.debug("Récupération des services de type : {}", type);

        return serviceRepository.findByType(type)
                .stream()
                .map(ServiceMapper::serviceEntityToServiceResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServiceResponseDTO updateService(Long id, ServiceRequestDTO requestDTO) {
        log.info("Mise à jour du service ID : {}", id);

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));

        if (!service.getName().equals(requestDTO.name())
                && serviceRepository.existsByName(requestDTO.name())) {
            log.warn("Tentative de renommage avec un nom existant : {}", requestDTO.name());
            throw new ServiceAlreadyExistsException(requestDTO.name(), true);
        }

        ServiceMapper.updateEntityFromDTO(service, requestDTO);
        ServiceEntity updatedService = serviceRepository.save(service);

        log.info("Service mis à jour avec succès : ID {}", updatedService.getId());

        return ServiceMapper.serviceEntityToServiceResponseDTO(updatedService);
    }

    @Override
    @Transactional
    public void deactivateService(Long id) {
        log.info("Désactivation du service ID : {}", id);

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));

        service.setIsActive(false);
        serviceRepository.save(service);

        log.info("Service désactivé avec succès : ID {}", id);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        log.info("Suppression du service ID : {}", id);

        if (!serviceRepository.existsById(id)) {
            throw new ServiceNotFoundException(id);
        }

        serviceRepository.deleteById(id);

        log.info("Service supprimé avec succès : ID {}", id);
    }
}