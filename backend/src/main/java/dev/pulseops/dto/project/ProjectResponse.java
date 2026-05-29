package dev.pulseops.dto.project;

import dev.pulseops.entity.Project;
import java.time.Instant;

public record ProjectResponse(
        Long id,
        String name,
        String slug,
        String description,
        Instant createdAt,
        long monitorCount
) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getSlug(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getMonitors() != null ? project.getMonitors().size() : 0
        );
    }
}
