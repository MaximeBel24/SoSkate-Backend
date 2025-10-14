package com.soskate.api.repositories;

import com.soskate.api.entities.SpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface SpotRepository extends JpaRepository<SpotEntity,Long> {

    Optional<SpotEntity> findOneByNameIgnoreCaseAndLatitudeAndLongitude(
            String name,
            BigDecimal latitude,
            BigDecimal longitude
    );

}
