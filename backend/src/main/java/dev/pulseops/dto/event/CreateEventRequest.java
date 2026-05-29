package dev.pulseops.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateEventRequest(
        @NotBlank @Size(max = 100) String eventType,
        @Size(max = 5000) String payload,
        @NotBlank String apiKey
) {}
