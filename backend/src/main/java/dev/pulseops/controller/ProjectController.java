package dev.pulseops.controller;

import dev.pulseops.dto.project.CreateProjectRequest;
import dev.pulseops.dto.project.ProjectResponse;
import dev.pulseops.entity.User;
import dev.pulseops.service.AuthService;
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
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects")
public class ProjectController {

    private final ProjectService projectService;
    private final AuthService authService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new project")
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request,
                                  @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        return projectService.create(request, user);
    }

    @GetMapping
    @Operation(summary = "List all projects for current user")
    public List<ProjectResponse> list(@AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        return projectService.listByUser(user.getId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ProjectResponse get(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        return projectService.getById(id, user.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a project")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        projectService.delete(id, user.getId());
    }
}
