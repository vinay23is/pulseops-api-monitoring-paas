package dev.pulseops.worker;

import dev.pulseops.entity.Monitor;
import dev.pulseops.service.AlertService;
import dev.pulseops.service.CheckResultService;
import dev.pulseops.service.IncidentService;
import dev.pulseops.service.MonitorService;
import dev.pulseops.service.MonitoringMetricsService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@ConditionalOnProperty(name = "pulseops.monitor.worker-enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class MonitorWorker {

    private final RedisTemplate<String, String> redisTemplate;
    private final MonitorService monitorService;
    private final CheckResultService checkResultService;
    private final IncidentService incidentService;
    private final AlertService alertService;
    private final WebClient.Builder webClientBuilder;
    private final MonitoringMetricsService metricsService;

    @Value("${pulseops.monitor.stream-name:monitor-checks}")
    private String streamName;

    @Value("${pulseops.monitor.consumer-group:workers}")
    private String groupName;

    @Value("${pulseops.monitor.consumer-name:worker-1}")
    private String consumerName;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @PostConstruct
    public void start() {
        ensureStreamAndGroup();
        executor.submit(this::runLoop);
    }

    private void ensureStreamAndGroup() {
        try {
            redisTemplate.opsForStream().createGroup(streamName, ReadOffset.latest(), groupName);
        } catch (Exception e) {
            log.debug("Stream/group already exists or created: {}", e.getMessage());
        }
    }

    private void runLoop() {
        log.info("MonitorWorker started, consuming from stream: {}", streamName);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream().read(
                        Consumer.from(groupName, consumerName),
                        StreamReadOptions.empty().count(10).block(Duration.ofSeconds(2)),
                        StreamOffset.create(streamName, ReadOffset.lastConsumed())
                );
                if (messages != null) {
                    for (MapRecord<String, Object, Object> message : messages) {
                        String monitorIdStr = (String) message.getValue().get("monitorId");
                        if (monitorIdStr != null) {
                            executor.submit(() -> processMonitor(Long.parseLong(monitorIdStr)));
                        }
                        redisTemplate.opsForStream().acknowledge(streamName, groupName, message.getId());
                    }
                }
            } catch (Exception e) {
                log.debug("Worker loop error (may be normal on startup): {}", e.getMessage());
                try { Thread.sleep(2000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
            }
        }
    }

    private void processMonitor(Long monitorId) {
        Monitor monitor;
        try {
            monitor = monitorService.getEntityById(monitorId);
        } catch (Exception e) {
            log.warn("Monitor {} not found", monitorId);
            return;
        }

        long start = System.currentTimeMillis();
        Integer statusCode = null;
        String errorMessage = null;
        boolean success = false;

        try {
            HttpMethod httpMethod = HttpMethod.valueOf(monitor.getMethod().name());
            var response = webClientBuilder.build()
                    .method(httpMethod)
                    .uri(monitor.getUrl())
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(monitor.getTimeoutSeconds()))
                    .block();

            if (response != null) {
                statusCode = response.getStatusCode().value();
                success = statusCode == monitor.getExpectedStatusCode();
                if (!success) {
                    errorMessage = "Expected " + monitor.getExpectedStatusCode() + " but got " + statusCode;
                }
            }
        } catch (Exception e) {
            errorMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
            success = false;
        }

        long latencyMs = System.currentTimeMillis() - start;
        metricsService.recordCheck(success, latencyMs);

        checkResultService.save(monitor, statusCode, latencyMs, success, errorMessage);
        monitorService.updateLastCheckedAt(monitorId);

        if (!success) {
            incidentService.openOrUpdate(monitor, errorMessage != null ? errorMessage : "Check failed");
            if (!incidentService.hasOpenIncident(monitorId)) {
                alertService.fireAlerts(monitor, "Monitor " + monitor.getName() + " is DOWN: " + errorMessage);
            }
        } else {
            boolean wasDown = incidentService.hasOpenIncident(monitorId);
            incidentService.resolveIfOpen(monitor);
            if (wasDown) {
                alertService.fireAlerts(monitor, "Monitor " + monitor.getName() + " has RECOVERED");
            }
        }

        log.debug("Checked {} → success={} latency={}ms status={}", monitor.getUrl(), success, latencyMs, statusCode);
    }

}
