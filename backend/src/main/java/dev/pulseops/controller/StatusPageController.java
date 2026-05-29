package dev.pulseops.controller;

import dev.pulseops.dto.incident.IncidentResponse;
import dev.pulseops.dto.monitor.MonitorResponse;
import dev.pulseops.entity.Project;
import dev.pulseops.service.IncidentService;
import dev.pulseops.service.MonitorService;
import dev.pulseops.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/status")
@RequiredArgsConstructor
@Tag(name = "Public Status Page")
public class StatusPageController {

    private final ProjectService projectService;
    private final MonitorService monitorService;
    private final IncidentService incidentService;

    @GetMapping("/{slug}")
    @Operation(summary = "Public status page for a project (no auth required)")
    public Map<String, Object> statusPage(@PathVariable String slug) {
        Project project = projectService.getBySlug(slug);
        List<MonitorResponse> monitors = monitorService.listByProject(project.getId());
        List<IncidentResponse> incidents = incidentService.getByProject(project.getId());

        long openIncidents = incidents.stream()
                .filter(i -> "OPEN".equals(i.status().name()))
                .count();
        String overallStatus = openIncidents == 0 ? "OPERATIONAL" : "DEGRADED";

        return Map.of(
                "project", Map.of("id", project.getId(), "name", project.getName(), "slug", project.getSlug()),
                "status", overallStatus,
                "monitors", monitors,
                "openIncidents", openIncidents,
                "recentIncidents", incidents.stream().limit(10).toList()
        );
    }
}
