package dev.pulseops.scheduler;

import dev.pulseops.entity.Monitor;
import dev.pulseops.repository.MonitorRepository;
import dev.pulseops.service.MonitoringMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "pulseops.monitor.scheduler-enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class MonitorScheduler {

    private final MonitorRepository monitorRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final MonitoringMetricsService metricsService;

    @Value("${pulseops.monitor.stream-name:monitor-checks}")
    private String streamName;

    @Scheduled(fixedDelay = 30000)
    public void dispatch() {
        Instant cutoff = Instant.now().minus(25, ChronoUnit.SECONDS);
        List<Monitor> dueMonitors = monitorRepository.findDueMonitors(cutoff);
        metricsService.recordDispatchSize(dueMonitors.size());
        log.debug("Scheduling {} monitors for health check", dueMonitors.size());

        for (Monitor monitor : dueMonitors) {
            try {
                Map<String, String> payload = Map.of("monitorId", String.valueOf(monitor.getId()));
                redisTemplate.opsForStream().add(streamName, payload);
            } catch (Exception e) {
                metricsService.recordEnqueueFailure();
                log.error("Failed to enqueue monitor {}: {}", monitor.getId(), e.getMessage());
            }
        }
    }
}
