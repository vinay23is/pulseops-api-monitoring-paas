package dev.pulseops.dto.monitor;

import dev.pulseops.entity.Monitor;
import dev.pulseops.entity.enums.MonitorMethod;
import java.time.Instant;

public record MonitorResponse(
        Long id,
        String name,
        String url,
        MonitorMethod method,
        Integer expectedStatusCode,
        Integer intervalSeconds,
        Integer timeoutSeconds,
        Boolean active,
        Instant lastCheckedAt,
        Instant createdAt,
        Long projectId,
        String projectName
) {
    public static MonitorResponse from(Monitor m) {
        return new MonitorResponse(
                m.getId(), m.getName(), m.getUrl(), m.getMethod(),
                m.getExpectedStatusCode(), m.getIntervalSeconds(), m.getTimeoutSeconds(),
                m.getActive(), m.getLastCheckedAt(), m.getCreatedAt(),
                m.getProject().getId(), m.getProject().getName()
        );
    }
}
