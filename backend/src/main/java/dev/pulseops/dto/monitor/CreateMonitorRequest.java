package dev.pulseops.dto.monitor;

import dev.pulseops.entity.enums.MonitorMethod;
import jakarta.validation.constraints.*;

public record CreateMonitorRequest(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Size(max = 500) String url,
        MonitorMethod method,
        @Min(100) @Max(599) Integer expectedStatusCode,
        @Min(10) @Max(3600) Integer intervalSeconds,
        @Min(1) @Max(60) Integer timeoutSeconds,
        Boolean active
) {}
