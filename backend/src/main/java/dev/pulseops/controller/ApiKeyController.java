package dev.pulseops.controller;

import dev.pulseops.entity.ApiKey;
import dev.pulseops.entity.Project;
import dev.pulseops.entity.User;
import dev.pulseops.service.ApiKeyService;
import dev.pulseops.service.AuthService;
import dev.pulseops.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/api-keys")
@RequiredArgsConstructor
@Tag(name = "API Keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final ProjectService projectService;
    private final AuthService authService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generate a new API key for a project")
    public Map<String, String> create(@PathVariable Long projectId,
                                      @RequestParam String name,
                                      @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        Project project = projectService.getEntityById(projectId, user.getId());
        return apiKeyService.create(name, project);
    }

    @GetMapping
    @Operation(summary = "List API keys for a project")
    public List<Map<String, Object>> list(@PathVariable Long projectId,
                                          @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        projectService.getEntityById(projectId, user.getId());
        return apiKeyService.listByProject(projectId).stream()
                .map(k -> Map.<String, Object>of(
                        "id", k.getId(),
                        "name", k.getName(),
                        "prefix", k.getKeyPrefix(),
                        "active", k.getActive(),
                        "createdAt", k.getCreatedAt()
                )).toList();
    }

    @DeleteMapping("/{keyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Revoke an API key")
    public void revoke(@PathVariable Long projectId,
                       @PathVariable Long keyId,
                       @AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser(principal.getUsername());
        projectService.getEntityById(projectId, user.getId());
        apiKeyService.revoke(keyId);
    }
}
