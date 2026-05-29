package dev.pulseops.dto.alert;

import dev.pulseops.entity.Alert;
import dev.pulseops.entity.enums.AlertStatus;
import dev.pulseops.entity.enums.AlertType;
import java.time.Instant;

public record AlertResponse(
        Long id,
        AlertType type,
        AlertStatus status,
        String message,
        String webhookUrl,
        String failureReason,
        Instant createdAt,
        Instant sentAt,
        Long monitorId,
        String monitorName
) {
    public static AlertResponse from(Alert a) {
        return new AlertResponse(
                a.getId(), a.getType(), a.getStatus(), a.getMessage(),
                a.getWebhookUrl(), a.getFailureReason(), a.getCreatedAt(), a.getSentAt(),
                a.getMonitor().getId(), a.getMonitor().getName()
        );
    }
}
