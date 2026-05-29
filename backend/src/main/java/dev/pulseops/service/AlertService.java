package dev.pulseops.service;

import dev.pulseops.dto.alert.AlertResponse;
import dev.pulseops.entity.Alert;
import dev.pulseops.entity.Monitor;
import dev.pulseops.entity.enums.AlertStatus;
import dev.pulseops.entity.enums.AlertType;
import dev.pulseops.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final WebClient.Builder webClientBuilder;

    @Transactional
    public void fireAlerts(Monitor monitor, String message) {
        createInAppAlert(monitor, message);
        createMockEmailAlert(monitor, message);
        if (monitor.getProject() != null) {
            fireWebhookIfConfigured(monitor, message);
        }
    }

    private void createInAppAlert(Monitor monitor, String message) {
        Alert alert = Alert.builder()
                .monitor(monitor)
                .type(AlertType.IN_APP)
                .message(message)
                .status(AlertStatus.SENT)
                .sentAt(Instant.now())
                .build();
        alertRepository.save(alert);
    }

    private void createMockEmailAlert(Monitor monitor, String message) {
        Alert alert = Alert.builder()
                .monitor(monitor)
                .type(AlertType.MOCK_EMAIL)
                .message("[MOCK EMAIL] To: " + monitor.getProject().getUser().getEmail() + " | " + message)
                .status(AlertStatus.SENT)
                .sentAt(Instant.now())
                .build();
        alertRepository.save(alert);
        log.info("[MOCK EMAIL] Alert for monitor {} — {}", monitor.getName(), message);
    }

    private void fireWebhookIfConfigured(Monitor monitor, String message) {
        // Webhook URL can be stored on project description for demo; real impl would use AlertConfig entity
        String webhookUrl = null; // Future: fetch from alert config

        Alert alert = Alert.builder()
                .monitor(monitor)
                .type(AlertType.WEBHOOK)
                .message(message)
                .webhookUrl(webhookUrl)
                .build();

        if (webhookUrl != null && !webhookUrl.isBlank()) {
            try {
                webClientBuilder.build()
                        .post().uri(webhookUrl)
                        .bodyValue(java.util.Map.of("monitor", monitor.getName(), "message", message, "timestamp", Instant.now().toString()))
                        .retrieve().toBodilessEntity().block(java.time.Duration.ofSeconds(5));
                alert.setStatus(AlertStatus.SENT);
                alert.setSentAt(Instant.now());
            } catch (Exception e) {
                alert.setStatus(AlertStatus.FAILED);
                alert.setFailureReason(e.getMessage());
                log.warn("Webhook delivery failed for monitor {}: {}", monitor.getName(), e.getMessage());
            }
        } else {
            alert.setStatus(AlertStatus.PENDING);
        }
        alertRepository.save(alert);
    }

    public List<AlertResponse> getByUser(Long userId, int limit) {
        return alertRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit)).stream()
                .map(AlertResponse::from)
                .toList();
    }

    public List<AlertResponse> getByProject(Long projectId, int limit) {
        return alertRepository.findByProjectIdOrderByCreatedAtDesc(projectId, PageRequest.of(0, limit)).stream()
                .map(AlertResponse::from)
                .toList();
    }
}
