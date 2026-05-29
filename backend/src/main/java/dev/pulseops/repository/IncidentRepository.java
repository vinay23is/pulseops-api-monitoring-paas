package dev.pulseops.repository;

import dev.pulseops.entity.Incident;
import dev.pulseops.entity.enums.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByMonitorIdOrderByStartedAtDesc(Long monitorId);
    Optional<Incident> findByMonitorIdAndStatus(Long monitorId, IncidentStatus status);

    @Query("SELECT i FROM Incident i WHERE i.monitor.project.id = :projectId ORDER BY i.startedAt DESC")
    List<Incident> findByProjectIdOrderByStartedAtDesc(Long projectId);

    @Query("SELECT i FROM Incident i WHERE i.monitor.project.user.id = :userId ORDER BY i.startedAt DESC")
    List<Incident> findByUserIdOrderByStartedAtDesc(Long userId);

    @Query("SELECT COUNT(i) FROM Incident i WHERE i.monitor.project.id = :projectId AND i.status = 'OPEN'")
    Long countOpenByProjectId(Long projectId);
}
