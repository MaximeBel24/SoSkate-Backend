package com.soskate.api.mappers;

import com.soskate.api.dtos.service.ServiceResponseDto;
import com.soskate.api.entities.ServiceEntity;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public ServiceResponseDto serviceToServiceDto(ServiceEntity service) {
        return new ServiceResponseDto(
                service.getId(),
                service.getName(),
                service.getType(),
                service.getDescription(),
                service.getDurationMin(),
                service.getBasePriceCents(),
                service.getIsActive()
        );
    }
}
