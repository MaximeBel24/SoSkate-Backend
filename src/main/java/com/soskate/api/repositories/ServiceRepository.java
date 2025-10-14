package com.soskate.api.repositories;

import com.soskate.api.entities.ServiceEntity;
import com.soskate.api.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    Optional<ServiceEntity> findOneByNameIgnoreCaseAndType(
            String name,
            ServiceType type
    );
}
