package dev.pulseops.repository;

import dev.pulseops.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUserId(Long userId);
    Optional<Project> findBySlug(String slug);
    boolean existsBySlug(String slug);
    Optional<Project> findByIdAndUserId(Long id, Long userId);
}
