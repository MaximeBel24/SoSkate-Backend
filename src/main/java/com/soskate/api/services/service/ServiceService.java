package com.soskate.api.services.service;

import com.soskate.api.dtos.service.ServiceResponseDto;
import com.soskate.api.dtos.service.ServiceRequestDto;

import java.util.List;

public interface ServiceService {

    List<ServiceResponseDto> getAllServices();
    ServiceResponseDto getServiceById(Long id);
    ServiceResponseDto createService(ServiceRequestDto serviceToCreate);
    ServiceResponseDto updateService(Long id, ServiceRequestDto serviceToUpdate);
    void deleteService(Long id);
}
