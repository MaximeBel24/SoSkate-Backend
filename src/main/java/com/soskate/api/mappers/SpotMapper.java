package com.soskate.api.mappers;

import com.soskate.api.dtos.spot.SpotDto;
import com.soskate.api.entities.SpotEntity;
import org.springframework.stereotype.Component;

@Component
public class SpotMapper {

    public SpotDto skateparkToSkateparkDto(SpotEntity skatepark) {
        return new SpotDto(
                skatepark.getId(),
                skatepark.getName(),
                skatepark.getAddressLine1(),
                skatepark.getAddressLine2(),
                skatepark.getCity(),
                skatepark.getPostalCode(),
                skatepark.getCountry(),
                skatepark.getLatitude(),
                skatepark.getLongitude(),
                skatepark.getIsIndoor(),
                skatepark.getIsActive(),
                skatepark.getCreatedAt(),
                skatepark.getUpdatedAt()
        );
    }
}
