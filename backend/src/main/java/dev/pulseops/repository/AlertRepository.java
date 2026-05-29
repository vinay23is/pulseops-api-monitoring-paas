package dev.pulseops.repository;

import dev.pulseops.entity.Alert;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByMonitorIdOrderByCreatedAtDesc(Long monitorId);

    @Query("SELECT a FROM Alert a WHERE a.monitor.project.id = :projectId ORDER BY a.createdAt DESC")
    List<Alert> findByProjectIdOrderByCreatedAtDesc(Long projectId, Pageable pageable);

    @Query("SELECT a FROM Alert a WHERE a.monitor.project.user.id = :userId ORDER BY a.createdAt DESC")
    List<Alert> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
