package dev.pulseops.service;

import dev.pulseops.dto.dashboard.DashboardResponse;
import dev.pulseops.dto.dashboard.CheckResultSummary;
import dev.pulseops.dto.monitor.MonitorResponse;
import dev.pulseops.entity.User;
import dev.pulseops.repository.IncidentRepository;
import dev.pulseops.repository.MonitorRepository;
import dev.pulseops.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MonitorRepository monitorRepository;
    private final ProjectRepository projectRepository;
    private final IncidentRepository incidentRepository;
    private final CheckResultService checkResultService;
    private final IncidentService incidentService;
    private final AlertService alertService;

    public DashboardResponse getDashboard(User user) {
        List<Long> projectIds = projectRepository.findByUserId(user.getId()).stream()
                .map(p -> p.getId()).toList();

        List<MonitorResponse> monitors = projectIds.stream()
                .flatMap(pid -> monitorRepository.findByProjectId(pid).stream()
                        .map(MonitorResponse::from))
                .toList();

        long activeMonitors = monitors.stream().filter(MonitorResponse::active).count();

        Instant since = Instant.now().minus(24, ChronoUnit.HOURS);
        double uptime = 100.0;
        double avgLatency = 0.0;
        long openIncidents = 0;

        for (Long pid : projectIds) {
            double u = checkResultService.getUptimePercentage(pid, since);
            double l = checkResultService.getAvgLatency(pid, since);
            uptime = Math.min(uptime, u);
            avgLatency += l;
            openIncidents += incidentRepository.countOpenByProjectId(pid);
        }
        if (!projectIds.isEmpty()) avgLatency /= projectIds.size();

        List<CheckResultSummary> recentChecks = projectIds.isEmpty() ? List.of() :
                checkResultService.getLatestByProject(projectIds.get(0), 20);

        var recentIncidents = incidentService.getByUser(user.getId()).stream().limit(5).toList();
        var recentAlerts = alertService.getByUser(user.getId(), 10);

        return new DashboardResponse(
                monitors.size(), activeMonitors,
                Math.round(uptime * 100.0) / 100.0,
                Math.round(avgLatency * 100.0) / 100.0,
                openIncidents, monitors, recentIncidents, recentAlerts, recentChecks
        );
    }
}
