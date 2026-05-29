package dev.pulseops.service;

import dev.pulseops.dto.monitor.CreateMonitorRequest;
import dev.pulseops.dto.monitor.MonitorResponse;
import dev.pulseops.entity.Monitor;
import dev.pulseops.entity.Project;
import dev.pulseops.entity.enums.MonitorMethod;
import dev.pulseops.exception.ResourceNotFoundException;
import dev.pulseops.repository.MonitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitorService {

    private final MonitorRepository monitorRepository;

    @Transactional
    public MonitorResponse create(CreateMonitorRequest request, Project project) {
        Monitor monitor = Monitor.builder()
                .name(request.name())
                .url(request.url())
                .method(request.method() != null ? request.method() : MonitorMethod.GET)
                .expectedStatusCode(request.expectedStatusCode() != null ? request.expectedStatusCode() : 200)
                .intervalSeconds(request.intervalSeconds() != null ? request.intervalSeconds() : 60)
                .timeoutSeconds(request.timeoutSeconds() != null ? request.timeoutSeconds() : 10)
                .active(request.active() != null ? request.active() : true)
                .project(project)
                .build();
        return MonitorResponse.from(monitorRepository.save(monitor));
    }

    public List<MonitorResponse> listByProject(Long projectId) {
        return monitorRepository.findByProjectId(projectId).stream()
                .map(MonitorResponse::from)
                .toList();
    }

    public MonitorResponse getById(Long monitorId, Long projectId) {
        return monitorRepository.findByIdAndProjectId(monitorId, projectId)
                .map(MonitorResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found: " + monitorId));
    }

    public Monitor getEntityById(Long monitorId) {
        return monitorRepository.findById(monitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found: " + monitorId));
    }

    @Transactional
    public MonitorResponse update(Long monitorId, Long projectId, CreateMonitorRequest request) {
        Monitor monitor = monitorRepository.findByIdAndProjectId(monitorId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found: " + monitorId));
        if (request.name() != null) monitor.setName(request.name());
        if (request.url() != null) monitor.setUrl(request.url());
        if (request.method() != null) monitor.setMethod(request.method());
        if (request.expectedStatusCode() != null) monitor.setExpectedStatusCode(request.expectedStatusCode());
        if (request.intervalSeconds() != null) monitor.setIntervalSeconds(request.intervalSeconds());
        if (request.timeoutSeconds() != null) monitor.setTimeoutSeconds(request.timeoutSeconds());
        if (request.active() != null) monitor.setActive(request.active());
        return MonitorResponse.from(monitorRepository.save(monitor));
    }

    @Transactional
    public void delete(Long monitorId, Long projectId) {
        Monitor monitor = monitorRepository.findByIdAndProjectId(monitorId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found: " + monitorId));
        monitorRepository.delete(monitor);
    }

    @Transactional
    public void updateLastCheckedAt(Long monitorId) {
        monitorRepository.findById(monitorId).ifPresent(m -> {
            m.setLastCheckedAt(java.time.Instant.now());
            monitorRepository.save(m);
        });
    }

    @Transactional
    public MonitorResponse toggleActive(Long monitorId, Long projectId) {
        Monitor monitor = monitorRepository.findByIdAndProjectId(monitorId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Monitor not found: " + monitorId));
        monitor.setActive(!monitor.getActive());
        return MonitorResponse.from(monitorRepository.save(monitor));
    }
}
