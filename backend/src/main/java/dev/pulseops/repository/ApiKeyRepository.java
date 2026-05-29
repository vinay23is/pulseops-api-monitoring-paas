package dev.pulseops.repository;

import dev.pulseops.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    List<ApiKey> findByProjectId(Long projectId);
    Optional<ApiKey> findByKeyHash(String keyHash);
    Optional<ApiKey> findByKeyPrefix(String keyPrefix);
}
