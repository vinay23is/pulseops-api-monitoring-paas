package dev.pulseops.repository;

import dev.pulseops.entity.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MonitorRepository extends JpaRepository<Monitor, Long> {
    List<Monitor> findByProjectId(Long projectId);
    Optional<Monitor> findByIdAndProjectId(Long id, Long projectId);

    @Query("SELECT m FROM Monitor m WHERE m.active = true AND " +
           "(m.lastCheckedAt IS NULL OR m.lastCheckedAt < :cutoff)")
    List<Monitor> findDueMonitors(Instant cutoff);

    List<Monitor> findByActiveTrue();

    long countByProjectId(Long projectId);
}
