package dev.pulseops.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Size(min = 2, max = 60)
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase alphanumeric with hyphens") String slug,
        @Size(max = 500) String description
) {}
