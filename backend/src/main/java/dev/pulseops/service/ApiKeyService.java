package dev.pulseops.service;

import dev.pulseops.entity.ApiKey;
import dev.pulseops.entity.Project;
import dev.pulseops.exception.ResourceNotFoundException;
import dev.pulseops.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public Map<String, String> create(String name, Project project) {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        String rawKey = "pk_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String prefix = rawKey.substring(0, 10);
        String hash = encoder.encode(rawKey);

        ApiKey apiKey = ApiKey.builder()
                .name(name)
                .keyHash(hash)
                .keyPrefix(prefix)
                .project(project)
                .build();
        apiKeyRepository.save(apiKey);
        return Map.of("key", rawKey, "prefix", prefix, "name", name);
    }

    public List<ApiKey> listByProject(Long projectId) {
        return apiKeyRepository.findByProjectId(projectId);
    }

    @Transactional
    public void revoke(Long keyId) {
        ApiKey key = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new ResourceNotFoundException("API key not found"));
        key.setActive(false);
        apiKeyRepository.save(key);
    }

    public ApiKey validateKey(String rawKey) {
        String prefix = rawKey.substring(0, Math.min(10, rawKey.length()));
        ApiKey apiKey = apiKeyRepository.findByKeyPrefix(prefix)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid API key"));
        if (!apiKey.getActive()) throw new IllegalArgumentException("API key is revoked");
        if (!encoder.matches(rawKey, apiKey.getKeyHash())) throw new IllegalArgumentException("Invalid API key");
        return apiKey;
    }
}
