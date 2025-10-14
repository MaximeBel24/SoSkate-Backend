package com.soskate.api.controllers;

import com.soskate.api.dtos.spot.SpotToCreateDto;
import com.soskate.api.dtos.spot.SpotDto;
import com.soskate.api.dtos.spot.SpotToUpdateDto;
import com.soskate.api.services.spot.SpotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skateparks")
public class SpotController {

    @Autowired
    private SpotService spotService;

    @GetMapping
    public List<SpotDto> getAllSkateparks() {
        return spotService.getAllSpots();
    }

    @GetMapping("/{id}")
    public SpotDto getSkateparkById(@PathVariable("id") Long id) {
        return spotService.getSpotById(id);
    }

    @PostMapping
    public SpotDto createSkatepark(@Valid @RequestBody SpotToCreateDto skateparkToCreate) {
        return spotService.createSkatepark(skateparkToCreate);
    }

    @PutMapping
    public SpotDto updateSkatepark(@Valid @RequestBody SpotToUpdateDto skateparkToUpdate) {
        return spotService.updateSkatepark(skateparkToUpdate);
    }

    @DeleteMapping("/{id}")
    public void deleteSkatepark(@PathVariable("id") Long id) {
        spotService.deleteSkatepark(id);
    }
}
