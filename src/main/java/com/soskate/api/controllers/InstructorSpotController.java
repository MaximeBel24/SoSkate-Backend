package com.soskate.api.controllers;

import com.soskate.api.dto.instructor.InstructorResponse;
import com.soskate.api.dto.instructor.InstructorSpotRequest;
import com.soskate.api.dto.instructor.InstructorSpotResponse;
import com.soskate.api.dto.instructor.InstructorSummary;
import com.soskate.api.services.instructor.InstructorSpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InstructorSpotController {

    private final InstructorSpotService instructorSpotService;

    @PostMapping("/instructors/{instructorId}/spots")
    public ResponseEntity<InstructorSpotResponse> addSpot(
            @PathVariable Long instructorId,
            @Valid @RequestBody InstructorSpotRequest request
    ) {
        InstructorSpotResponse response = instructorSpotService.addSpotToInstructor(instructorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/instructors/{instructorId}/spots")
    public ResponseEntity<List<InstructorSpotResponse>> getSpotsByInstructor(
            @PathVariable Long instructorId
    ) {
        List<InstructorSpotResponse> response = instructorSpotService.getSpotsByInstructor(instructorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/spots/{spotId}/instructors")
    public ResponseEntity<List<InstructorSummary>> getInstructorsBySpot(@PathVariable Long spotId) {
        List<InstructorSummary> instructors = instructorSpotService.getInstructorsBySpot(spotId);
        return ResponseEntity.ok(instructors);
    }

    @DeleteMapping("/instructors/{instructorId}/spots/{spotId}")
    public ResponseEntity<Void> removeSpot(
            @PathVariable Long instructorId,
            @PathVariable Long spotId
    ) {
        instructorSpotService.removeSpotFromInstructor(instructorId, spotId);
        return ResponseEntity.noContent().build();
    }
}