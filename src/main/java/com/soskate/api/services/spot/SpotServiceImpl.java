package com.soskate.api.services.spot;

import com.soskate.api.dtos.spot.SpotToCreateDto;
import com.soskate.api.dtos.spot.SpotDto;
import com.soskate.api.dtos.spot.SpotToUpdateDto;
import com.soskate.api.entities.SpotEntity;
import com.soskate.api.exceptions.spot.SpotAlreadyExistsException;
import com.soskate.api.exceptions.spot.SpotDataRetrievalException;
import com.soskate.api.exceptions.spot.SpotNotFoundException;
import com.soskate.api.mappers.SpotMapper;
import com.soskate.api.repositories.SpotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpotServiceImpl implements SpotService {

    private final Logger log = LoggerFactory.getLogger(SpotServiceImpl.class);

    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private SpotMapper skateparkMapper;

    @Override
    public List<SpotDto> getAllSpots() {
        log.info("Invoking getAllSkateparks()");
        try {
            return spotRepository.findAll().stream()
                    .map(skateparkMapper::skateparkToSkateparkDto)
                    .toList();
        } catch (DataAccessException e) {
            log.error("Could not retrieve skateparks", e);
            throw new SpotDataRetrievalException(e);
        }
     }

    @Override
    public SpotDto getSpotById(Long id) {
        log.info("Invoking getSkateparkById with id={}", id);
        try {
            Optional<SpotEntity> skatepark = spotRepository.findById(id);
            if (skatepark.isEmpty()) {
                log.warn("Could not find skatepark with id={}", id);
                throw new SpotNotFoundException(id);
            }
            return skateparkMapper.skateparkToSkateparkDto(skatepark.get());
        } catch (DataAccessException e) {
            log.error("Could not find skatepark with id={}", id, e);
            throw new SpotDataRetrievalException(e);
        }
    }

    @Override
    public SpotDto createSkatepark(SpotToCreateDto skateparkToCreate) {
        log.info("Invoking createSkatepark with skateparkToCreate={}", skateparkToCreate);
        try {
            Optional<SpotEntity> skatepark = spotRepository.findOneByNameIgnoreCaseAndLatitudeAndLongitude(
                    skateparkToCreate.name(),
                    skateparkToCreate.latitude(),
                    skateparkToCreate.longitude()
            );
            if (skatepark.isPresent()) {
                log.warn("Skatepark to create with name={}, latitude={}, longitude={} already exists",
                        skateparkToCreate.name(),
                        skateparkToCreate.latitude(),
                        skateparkToCreate.longitude()
                        );
                throw new SpotAlreadyExistsException(
                        skateparkToCreate.name(),
                        skateparkToCreate.latitude(),
                        skateparkToCreate.longitude()
                );
            }

            SpotEntity skateparkToRegister = new SpotEntity(
                    skateparkToCreate.name(),
                    skateparkToCreate.addressLine1(),
                    skateparkToCreate.addressLine2(),
                    skateparkToCreate.city(),
                    skateparkToCreate.postalCode(),
                    skateparkToCreate.country(),
                    skateparkToCreate.latitude(),
                    skateparkToCreate.longitude(),
                    skateparkToCreate.isIndoor(),
                    skateparkToCreate.isActive()
            );

            SpotEntity registeredSkatepark = spotRepository.save(skateparkToRegister);

            return getSpotById(registeredSkatepark.getId());

        } catch (DataAccessException e) {
            log.error("Could not create skatepark={}", skateparkToCreate, e);
            throw new SpotDataRetrievalException(e);
        }
    }

    @Override
    public SpotDto updateSkatepark(SpotToUpdateDto spotToUpdate) {
        log.info("Invoking updateSkatepark with spotToUpdate={}", spotToUpdate);
        try {
            Optional<SpotEntity> existingSkatepark = spotRepository.findById(spotToUpdate.id());
            if (existingSkatepark.isEmpty()) {
                log.warn("Could not find skatepark to update with id={}", spotToUpdate.id());
                throw new SpotNotFoundException(spotToUpdate.id());
            }

            Optional<SpotEntity> potentiallyDuplicatedSkatepark = spotRepository.findOneByNameIgnoreCaseAndLatitudeAndLongitude(
                    spotToUpdate.name(),
                    spotToUpdate.latitude(),
                    spotToUpdate.longitude()
            );
            if (potentiallyDuplicatedSkatepark.isPresent() && !potentiallyDuplicatedSkatepark.get().getId().equals(spotToUpdate.id())) {
                log.warn("Skatepark to update with name={}, latitude={}, longitude={} already exists",
                        spotToUpdate.name(),
                        spotToUpdate.latitude(),
                        spotToUpdate.longitude()
                );
                throw new SpotAlreadyExistsException(
                        spotToUpdate.name(),
                        spotToUpdate.latitude(),
                        spotToUpdate.longitude()
                );
            }

            existingSkatepark.get().setName(spotToUpdate.name());
            existingSkatepark.get().setAddressLine1(spotToUpdate.addressLine1());
            existingSkatepark.get().setAddressLine2(spotToUpdate.addressLine2());
            existingSkatepark.get().setCity(spotToUpdate.city());
            existingSkatepark.get().setPostalCode(spotToUpdate.postalCode());
            existingSkatepark.get().setCountry(spotToUpdate.country());
            existingSkatepark.get().setLatitude(spotToUpdate.latitude());
            existingSkatepark.get().setLongitude(spotToUpdate.longitude());
            existingSkatepark.get().setIsIndoor(spotToUpdate.isIndoor());
            existingSkatepark.get().setIsActive(spotToUpdate.isActive());

            SpotEntity updatedSkatepark = spotRepository.save(existingSkatepark.get());

            return getSpotById(updatedSkatepark.getId());

        } catch (DataAccessException e) {
            log.error("Could not update skatepark={}", spotToUpdate, e);
            throw new SpotDataRetrievalException(e);
        }
    }

    @Override
    public void deleteSkatepark(Long id) {
        log.info("Invoking delete with id={}", id);
        try {
            Optional<SpotEntity> skateparkToDelete = spotRepository.findById(id);
            if (skateparkToDelete.isEmpty()) {
                log.warn("Could not found skateparkToDelete with id ={}", id);
                throw new SpotNotFoundException(id);
            }

            spotRepository.delete(skateparkToDelete.get());

        } catch (DataAccessException e) {
            log.error("Could not delete player with id={}", id, e);
            throw new SpotDataRetrievalException(e);
        }
    }
}
