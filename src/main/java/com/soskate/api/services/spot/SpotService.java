package com.soskate.api.services.spot;

import com.soskate.api.dtos.spot.SpotToCreateDto;
import com.soskate.api.dtos.spot.SpotDto;
import com.soskate.api.dtos.spot.SpotToUpdateDto;

import java.util.List;

public interface SpotService {

    List<SpotDto> getAllSpots();
    SpotDto getSpotById(Long id);
    SpotDto createSkatepark(SpotToCreateDto skateparkToCreate);
    SpotDto updateSkatepark(SpotToUpdateDto skateparkToUpdate);
    void deleteSkatepark(Long id);

}
