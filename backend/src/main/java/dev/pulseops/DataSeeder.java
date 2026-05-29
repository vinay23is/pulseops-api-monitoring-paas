package dev.pulseops;

import dev.pulseops.entity.*;
import dev.pulseops.entity.enums.*;
import dev.pulseops.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final MonitorRepository monitorRepository;
    private final CheckResultRepository checkResultRepository;
    private final IncidentRepository incidentRepository;
    private final AlertRepository alertRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByEmail("demo@pulseops.dev")) {
            log.info("Demo data already seeded — skipping");
            return;
        }

        log.info("Seeding demo data...");

        User demo = userRepository.save(User.builder()
                .name("Demo User")
                .email("demo@pulseops.dev")
                .password(passwordEncoder.encode("demo123"))
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("My SaaS App")
                .slug("my-saas-app")
                .description("Production monitoring for my SaaS platform")
                .user(demo)
                .build());

        Monitor apiMonitor = monitorRepository.save(Monitor.builder()
                .name("API Health")
                .url("https://httpbin.org/status/200")
                .method(MonitorMethod.GET)
                .expectedStatusCode(200)
                .intervalSeconds(60)
                .timeoutSeconds(10)
                .active(true)
                .project(project)
                .lastCheckedAt(Instant.now().minus(1, ChronoUnit.MINUTES))
                .build());

        Monitor webMonitor = monitorRepository.save(Monitor.builder()
                .name("Website Homepage")
                .url("https://example.com")
                .method(MonitorMethod.GET)
                .expectedStatusCode(200)
                .intervalSeconds(120)
                .timeoutSeconds(15)
                .active(true)
                .project(project)
                .lastCheckedAt(Instant.now().minus(2, ChronoUnit.MINUTES))
                .build());

        Monitor authMonitor = monitorRepository.save(Monitor.builder()
                .name("Auth Service")
                .url("https://httpbin.org/status/200")
                .method(MonitorMethod.GET)
                .expectedStatusCode(200)
                .intervalSeconds(30)
                .timeoutSeconds(5)
                .active(true)
                .project(project)
                .lastCheckedAt(Instant.now().minus(30, ChronoUnit.SECONDS))
                .build());

        // Seed historical check results
        for (int i = 48; i >= 0; i--) {
            Instant time = Instant.now().minus(i * 30, ChronoUnit.MINUTES);
            boolean success = i != 5 && i != 6;
            checkResultRepository.save(CheckResult.builder()
                    .monitor(apiMonitor)
                    .statusCode(success ? 200 : 503)
                    .latencyMs(success ? (long)(80 + Math.random() * 120) : null)
                    .success(success)
                    .errorMessage(success ? null : "Service Unavailable")
                    .checkedAt(time)
                    .build());
        }

        for (int i = 24; i >= 0; i--) {
            Instant time = Instant.now().minus(i * 60, ChronoUnit.MINUTES);
            checkResultRepository.save(CheckResult.builder()
                    .monitor(webMonitor)
                    .statusCode(200)
                    .latencyMs((long)(200 + Math.random() * 300))
                    .success(true)
                    .checkedAt(time)
                    .build());
        }

        // Seed a resolved incident
        incidentRepository.save(Incident.builder()
                .monitor(apiMonitor)
                .status(IncidentStatus.RESOLVED)
                .reason("Service Unavailable — HTTP 503")
                .failureCount(2)
                .startedAt(Instant.now().minus(5, ChronoUnit.HOURS))
                .resolvedAt(Instant.now().minus(4, ChronoUnit.HOURS))
                .build());

        // Seed alerts
        alertRepository.save(Alert.builder()
                .monitor(apiMonitor)
                .type(AlertType.IN_APP)
                .status(AlertStatus.SENT)
                .message("Monitor 'API Health' is DOWN: Service Unavailable")
                .sentAt(Instant.now().minus(5, ChronoUnit.HOURS))
                .build());

        alertRepository.save(Alert.builder()
                .monitor(apiMonitor)
                .type(AlertType.MOCK_EMAIL)
                .status(AlertStatus.SENT)
                .message("[MOCK EMAIL] To: demo@pulseops.dev | Monitor 'API Health' has RECOVERED")
                .sentAt(Instant.now().minus(4, ChronoUnit.HOURS))
                .build());

        log.info("Demo data seeded. Login: demo@pulseops.dev / demo123");
    }
}
