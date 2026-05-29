package dev.pulseops.controller;

import dev.pulseops.dto.monitor.CreateMonitorRequest;
import dev.pulseops.dto.monitor.MonitorResponse;
import dev.pulseops.entity.Project;
import dev.pulseops.entity.User;
import dev.pulseops.service.AuthService;
import dev.pulseops.service.MonitorService;
import dev.pulseops.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/monitors")
@RequiredArgsConstructor
@Tag(name = "Monitors")
public class MonitorController {

    private final MonitorService monitorService;
    private final ProjectService projectService;
    private final AuthService authService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a monitor")
    public MonitorResponse create(@PathVariable Long projectId,
                                  @Valid @RequestBody CreateMonitorRequest request,
                                  @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        Project project = projectService.getEntityById(projectId, user.getId());
        return monitorService.create(request, project);
    }

    @GetMapping
    @Operation(summary = "List monitors for a project")
    public List<MonitorResponse> list(@PathVariable Long projectId,
                                      @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        projectService.getEntityById(projectId, user.getId());
        return monitorService.listByProject(projectId);
    }

    @GetMapping("/{monitorId}")
    @Operation(summary = "Get a monitor by ID")
    public MonitorResponse get(@PathVariable Long projectId,
                               @PathVariable Long monitorId,
                               @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        projectService.getEntityById(projectId, user.getId());
        return monitorService.getById(monitorId, projectId);
    }

    @PutMapping("/{monitorId}")
    @Operation(summary = "Update a monitor")
    public MonitorResponse update(@PathVariable Long projectId,
                                  @PathVariable Long monitorId,
                                  @Valid @RequestBody CreateMonitorRequest request,
                                  @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        projectService.getEntityById(projectId, user.getId());
        return monitorService.update(monitorId, projectId, request);
    }

    @DeleteMapping("/{monitorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a monitor")
    public void delete(@PathVariable Long projectId,
                       @PathVariable Long monitorId,
                       @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        projectService.getEntityById(projectId, user.getId());
        monitorService.delete(monitorId, projectId);
    }

    @PatchMapping("/{monitorId}/toggle")
    @Operation(summary = "Toggle monitor active/inactive")
    public MonitorResponse toggle(@PathVariable Long projectId,
                                  @PathVariable Long monitorId,
                                  @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        projectService.getEntityById(projectId, user.getId());
        return monitorService.toggleActive(monitorId, projectId);
    }
}
