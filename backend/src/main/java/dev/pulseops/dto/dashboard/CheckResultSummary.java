package dev.pulseops.dto.dashboard;

import dev.pulseops.entity.CheckResult;
import java.time.Instant;

public record CheckResultSummary(
        Long id,
        Long monitorId,
        String monitorName,
        String monitorUrl,
        Integer statusCode,
        Long latencyMs,
        Boolean success,
        String errorMessage,
        Instant checkedAt
) {
    public static CheckResultSummary from(CheckResult cr) {
        return new CheckResultSummary(
                cr.getId(),
                cr.getMonitor().getId(),
                cr.getMonitor().getName(),
                cr.getMonitor().getUrl(),
                cr.getStatusCode(),
                cr.getLatencyMs(),
                cr.getSuccess(),
                cr.getErrorMessage(),
                cr.getCheckedAt()
        );
    }
}
