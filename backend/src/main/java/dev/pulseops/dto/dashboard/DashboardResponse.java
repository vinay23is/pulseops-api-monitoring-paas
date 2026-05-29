package dev.pulseops.dto.dashboard;

import dev.pulseops.dto.alert.AlertResponse;
import dev.pulseops.dto.incident.IncidentResponse;
import dev.pulseops.dto.monitor.MonitorResponse;
import java.util.List;

public record DashboardResponse(
        long totalMonitors,
        long activeMonitors,
        double uptimePercentage,
        double avgLatencyMs,
        long openIncidents,
        List<MonitorResponse> monitors,
        List<IncidentResponse> recentIncidents,
        List<AlertResponse> recentAlerts,
        List<CheckResultSummary> recentChecks
) {}
