package dev.pulseops.service;

import dev.pulseops.dto.incident.IncidentResponse;
import dev.pulseops.entity.Incident;
import dev.pulseops.entity.Monitor;
import dev.pulseops.entity.enums.IncidentStatus;
import dev.pulseops.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;

    @Transactional
    public Incident openOrUpdate(Monitor monitor, String reason) {
        Optional<Incident> existing = incidentRepository.findByMonitorIdAndStatus(monitor.getId(), IncidentStatus.OPEN);
        if (existing.isPresent()) {
            Incident incident = existing.get();
            incident.setFailureCount(incident.getFailureCount() == null ? 2 : incident.getFailureCount() + 1);
            incident.setReason(reason);
            return incidentRepository.save(incident);
        }
        Incident incident = Incident.builder()
                .monitor(monitor)
                .status(IncidentStatus.OPEN)
                .reason(reason)
                .failureCount(1)
                .build();
        return incidentRepository.save(incident);
    }

    @Transactional
    public void resolveIfOpen(Monitor monitor) {
        incidentRepository.findByMonitorIdAndStatus(monitor.getId(), IncidentStatus.OPEN)
                .ifPresent(incident -> {
                    incident.setStatus(IncidentStatus.RESOLVED);
                    incident.setResolvedAt(Instant.now());
                    incidentRepository.save(incident);
                });
    }

    public List<IncidentResponse> getByProject(Long projectId) {
        return incidentRepository.findByProjectIdOrderByStartedAtDesc(projectId).stream()
                .map(IncidentResponse::from)
                .toList();
    }

    public List<IncidentResponse> getByUser(Long userId) {
        return incidentRepository.findByUserIdOrderByStartedAtDesc(userId).stream()
                .map(IncidentResponse::from)
                .toList();
    }

    public boolean hasOpenIncident(Long monitorId) {
        return incidentRepository.findByMonitorIdAndStatus(monitorId, IncidentStatus.OPEN).isPresent();
    }
}
