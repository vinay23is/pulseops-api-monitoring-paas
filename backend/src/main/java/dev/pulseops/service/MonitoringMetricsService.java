package dev.pulseops.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MonitoringMetricsService {

    private final Counter checksTotal;
    private final Counter checksSucceeded;
    private final Counter checksFailed;
    private final Counter enqueueFailures;
    private final Timer checkLatency;
    private final AtomicInteger lastDispatchSize = new AtomicInteger();

    public MonitoringMetricsService(MeterRegistry meterRegistry) {
        this.checksTotal = Counter.builder("pulseops_monitor_checks_total")
                .description("Total monitor checks processed by workers")
                .register(meterRegistry);
        this.checksSucceeded = Counter.builder("pulseops_monitor_checks_success_total")
                .description("Successful monitor checks")
                .register(meterRegistry);
        this.checksFailed = Counter.builder("pulseops_monitor_checks_failure_total")
                .description("Failed monitor checks")
                .register(meterRegistry);
        this.enqueueFailures = Counter.builder("pulseops_monitor_enqueue_failures_total")
                .description("Failures while enqueueing monitor checks")
                .register(meterRegistry);
        this.checkLatency = Timer.builder("pulseops_monitor_check_latency")
                .description("Observed latency for outbound monitor checks")
                .publishPercentileHistogram()
                .register(meterRegistry);

        Gauge.builder("pulseops_monitor_dispatch_batch_size", lastDispatchSize, AtomicInteger::get)
                .description("Number of due monitors found in the latest scheduler dispatch")
                .register(meterRegistry);
    }

    public void recordDispatchSize(int monitorCount) {
        lastDispatchSize.set(monitorCount);
    }

    public void recordEnqueueFailure() {
        enqueueFailures.increment();
    }

    public void recordCheck(boolean success, long latencyMs) {
        checksTotal.increment();
        if (success) {
            checksSucceeded.increment();
        } else {
            checksFailed.increment();
        }
        checkLatency.record(java.time.Duration.ofMillis(latencyMs));
    }
}
