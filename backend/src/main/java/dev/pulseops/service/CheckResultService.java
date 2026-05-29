package dev.pulseops.service;

import dev.pulseops.dto.dashboard.CheckResultSummary;
import dev.pulseops.entity.CheckResult;
import dev.pulseops.entity.Monitor;
import dev.pulseops.repository.CheckResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckResultService {

    private final CheckResultRepository checkResultRepository;

    @Transactional
    public CheckResult save(Monitor monitor, Integer statusCode, Long latencyMs, Boolean success, String errorMessage) {
        CheckResult result = CheckResult.builder()
                .monitor(monitor)
                .statusCode(statusCode)
                .latencyMs(latencyMs)
                .success(success)
                .errorMessage(errorMessage)
                .build();
        return checkResultRepository.save(result);
    }

    public List<CheckResultSummary> getLatestByProject(Long projectId, int limit) {
        return checkResultRepository.findLatestByProjectId(projectId, PageRequest.of(0, limit)).stream()
                .map(CheckResultSummary::from)
                .toList();
    }

    public List<CheckResultSummary> getByMonitor(Long monitorId, int limit) {
        return checkResultRepository.findByMonitorIdOrderByCheckedAtDesc(monitorId, PageRequest.of(0, limit)).stream()
                .map(CheckResultSummary::from)
                .toList();
    }

    public double getUptimePercentage(Long projectId, java.time.Instant since) {
        Long total = checkResultRepository.countTotalByProjectIdSince(projectId, since);
        if (total == null || total == 0) return 100.0;
        Long success = checkResultRepository.countSuccessByProjectIdSince(projectId, since);
        return success == null ? 100.0 : (success * 100.0 / total);
    }

    public double getAvgLatency(Long projectId, java.time.Instant since) {
        Double avg = checkResultRepository.avgLatencyByProjectIdSince(projectId, since);
        return avg == null ? 0.0 : avg;
    }
}
