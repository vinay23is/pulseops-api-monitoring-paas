package dev.pulseops.repository;

import dev.pulseops.entity.CheckResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.List;

public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {
    List<CheckResult> findByMonitorIdOrderByCheckedAtDesc(Long monitorId, Pageable pageable);

    @Query("SELECT AVG(cr.latencyMs) FROM CheckResult cr WHERE cr.monitor.id = :monitorId AND cr.success = true")
    Double avgLatencyByMonitorId(Long monitorId);

    @Query("SELECT COUNT(cr) FROM CheckResult cr WHERE cr.monitor.id = :monitorId AND cr.success = true")
    Long countSuccessByMonitorId(Long monitorId);

    @Query("SELECT COUNT(cr) FROM CheckResult cr WHERE cr.monitor.id = :monitorId")
    Long countTotalByMonitorId(Long monitorId);

    @Query("SELECT cr FROM CheckResult cr WHERE cr.monitor.project.id = :projectId ORDER BY cr.checkedAt DESC")
    List<CheckResult> findLatestByProjectId(Long projectId, Pageable pageable);

    @Query("SELECT COUNT(cr) FROM CheckResult cr WHERE cr.monitor.project.id = :projectId AND cr.success = true AND cr.checkedAt > :since")
    Long countSuccessByProjectIdSince(Long projectId, Instant since);

    @Query("SELECT COUNT(cr) FROM CheckResult cr WHERE cr.monitor.project.id = :projectId AND cr.checkedAt > :since")
    Long countTotalByProjectIdSince(Long projectId, Instant since);

    @Query("SELECT AVG(cr.latencyMs) FROM CheckResult cr WHERE cr.monitor.project.id = :projectId AND cr.success = true AND cr.checkedAt > :since")
    Double avgLatencyByProjectIdSince(Long projectId, Instant since);
}
