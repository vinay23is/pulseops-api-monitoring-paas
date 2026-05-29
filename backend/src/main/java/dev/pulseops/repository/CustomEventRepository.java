package dev.pulseops.repository;

import dev.pulseops.entity.CustomEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomEventRepository extends JpaRepository<CustomEvent, Long> {
    List<CustomEvent> findByProjectIdOrderByCreatedAtDesc(Long projectId, Pageable pageable);
}
