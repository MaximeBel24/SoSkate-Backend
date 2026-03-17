package com.soskate.api.controllers.admin;

import com.soskate.api.dto.admin.DashboardResponse;
import com.soskate.api.services.admin.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin - Dashboard", description = "Administrative dashboard management")
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @Operation(
            summary = "Get data dashboard",
            description = "Get monthly customers monthly user registrations, daily bookings, monthly bookings"
    )
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboardData() {
        log.info("GET api/admin/dashboard - Fetching Dashboard Data");
        return ResponseEntity.ok(adminDashboardService.getDashboardData());
    }

}
