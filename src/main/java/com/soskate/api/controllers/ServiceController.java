package com.soskate.api.controllers;

import com.soskate.api.dto.service.ServiceRequest;
import com.soskate.api.dto.service.ServiceResponse;
import com.soskate.api.enums.ServiceType;
import com.soskate.api.services.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * REST controller for skateboard service/lesson management.
 *
 * @author SoSkate Team
 * @version 1.0
 */
@Tag(name = "Services", description = "Skateboard service and lesson management")
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceController {

    private final ServiceService serviceService;

    @Operation(
            summary = "Create a service",
            description = "Creates a new service/lesson"
    )
    @PostMapping
    public ResponseEntity<ServiceResponse> createService(
            @Valid @RequestBody ServiceRequest requestDTO) {

        log.info("POST /api/services - Creating a service: {}", requestDTO.name());

        ServiceResponse service = serviceService.createService(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(service);
    }

    @Operation(
            summary = "List all services",
            description = "Retrieves all services"
    )
    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        log.info("GET /api/services - Retrieving all services");

        List<ServiceResponse> services = serviceService.getAllServices();

        log.info("{} service(s) found", services.size());

        return ResponseEntity.ok(services);
    }

    @Operation(
            summary = "List active services",
            description = "Retrieves only active services"
    )
    @GetMapping("/active")
    public ResponseEntity<List<ServiceResponse>> getActiveServices() {
        log.info("GET /api/services/active - Retrieving active services");

        List<ServiceResponse> services = serviceService.getActiveServices();

        return ResponseEntity.ok(services);
    }

    @Operation(
            summary = "Retrieve a service",
            description = "Retrieves a service by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getServiceById(@PathVariable Long id) {
        log.info("GET /api/services/{} - Retrieving service", id);

        ServiceResponse service = serviceService.getServiceById(id);

        return ResponseEntity.ok(service);
    }

    @Operation(
            summary = "Filter by type",
            description = "Retrieves services by type (LESSON, RENTAL)"
    )
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ServiceResponse>> getServicesByType(@PathVariable ServiceType type) {
        log.info("GET /api/services/type/{} - Retrieving by type", type);

        List<ServiceResponse> services = serviceService.getServicesByType(type);

        return ResponseEntity.ok(services);
    }

    @Operation(
            summary = "Update a service",
            description = "Updates an existing service"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequest requestDTO) {

        log.info("PUT /api/services/{} - Updating service", id);

        ServiceResponse service = serviceService.updateService(id, requestDTO);

        return ResponseEntity.ok(service);
    }

    @Operation(
            summary = "Deactivate a service",
            description = "Deactivates a service (soft delete)"
    )
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateService(@PathVariable Long id) {
        log.info("PATCH /api/services/{}/deactivate - Deactivating", id);

        serviceService.deactivateService(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete a service",
            description = "Permanently deletes a service"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        log.info("DELETE /api/services/{} - Deleting", id);

        serviceService.deleteService(id);

        return ResponseEntity.noContent().build();
    }
}
