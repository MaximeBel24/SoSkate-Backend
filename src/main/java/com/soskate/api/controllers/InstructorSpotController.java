package com.soskate.api.controllers;

import com.soskate.api.dto.instructor.InstructorResponse;
import com.soskate.api.dto.instructor.InstructorSpotRequest;
import com.soskate.api.dto.instructor.InstructorSpotResponse;
import com.soskate.api.dto.instructor.InstructorSummary;
import com.soskate.api.services.instructor.InstructorSpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Instructor Spots", description = "Instructor-spot association")
@RestController
@RequiredArgsConstructor
@RequestMapping("/instructors/{instructorId}")
@Slf4j
public class InstructorSpotController {

    private final InstructorSpotService instructorSpotService;

    @Operation(
            summary = "Add a spot",
            description = "Associates a spot with an instructor"
    )
    @PostMapping("/spots")
    public ResponseEntity<InstructorSpotResponse> addSpotToInstructor(
            @PathVariable Long instructorId,
            @Valid @RequestBody InstructorSpotRequest request
    ) {
        log.info("POST /api/instructors/{}/spots - Associating a spot", instructorId);
        InstructorSpotResponse response = instructorSpotService.addSpotToInstructor(instructorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "List spots",
            description = "Retrieves the spots associated with an instructor"
    )
    @GetMapping("/spots")
    public ResponseEntity<List<InstructorSpotResponse>> getSpotsByInstructor(
            @PathVariable Long instructorId
    ) {
        log.info("GET /api/instructors/{}/spots - Retrieving spots", instructorId);
        List<InstructorSpotResponse> response = instructorSpotService.getSpotsByInstructor(instructorId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Remove a spot",
            description = "Removes the association between an instructor and a spot"
    )
    @DeleteMapping("/spots/{spotId}")
    public ResponseEntity<Void> removeSpotFromInstructor(
            @PathVariable Long instructorId,
            @PathVariable Long spotId
    ) {
        log.info("DELETE /api/instructors/{}/spots/{} - Removing association", instructorId, spotId);
        instructorSpotService.removeSpotFromInstructor(instructorId, spotId);
        return ResponseEntity.noContent().build();
    }
}