package dev.pulseops.controller;

import dev.pulseops.dto.incident.IncidentResponse;
import dev.pulseops.entity.User;
import dev.pulseops.service.AuthService;
import dev.pulseops.service.IncidentService;
import dev.pulseops.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final AuthService authService;
    private final ProjectService projectService;

    @GetMapping("/api/v1/incidents")
    @Operation(summary = "List all incidents for current user")
    public List<IncidentResponse> listAll(@AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        return incidentService.getByUser(user.getId());
    }

    @GetMapping("/api/v1/projects/{projectId}/incidents")
    @Operation(summary = "List incidents for a project")
    public List<IncidentResponse> listByProject(@PathVariable Long projectId,
                                                @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        projectService.getEntityById(projectId, user.getId());
        return incidentService.getByProject(projectId);
    }
}
