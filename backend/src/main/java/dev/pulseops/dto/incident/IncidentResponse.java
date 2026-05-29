package dev.pulseops.dto.incident;

import dev.pulseops.entity.Incident;
import dev.pulseops.entity.enums.IncidentStatus;
import java.time.Instant;

public record IncidentResponse(
        Long id,
        IncidentStatus status,
        String reason,
        Instant startedAt,
        Instant resolvedAt,
        Integer failureCount,
        Long monitorId,
        String monitorName,
        String monitorUrl,
        Long projectId
) {
    public static IncidentResponse from(Incident i) {
        return new IncidentResponse(
                i.getId(), i.getStatus(), i.getReason(),
                i.getStartedAt(), i.getResolvedAt(), i.getFailureCount(),
                i.getMonitor().getId(), i.getMonitor().getName(), i.getMonitor().getUrl(),
                i.getMonitor().getProject().getId()
        );
    }
}
