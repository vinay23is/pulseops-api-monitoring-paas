package dev.pulseops.controller;

import dev.pulseops.dto.dashboard.DashboardResponse;
import dev.pulseops.entity.User;
import dev.pulseops.service.AuthService;
import dev.pulseops.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthService authService;

    @GetMapping
    @Operation(summary = "Get dashboard overview (uptime, latency, incidents, recent checks)")
    public DashboardResponse getDashboard(@AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        return dashboardService.getDashboard(user);
    }
}
