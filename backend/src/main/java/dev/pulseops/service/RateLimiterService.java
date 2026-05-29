package dev.pulseops.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${pulseops.rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;

    public boolean isAllowed(String apiKeyPrefix) {
        String key = "rate:" + apiKeyPrefix + ":" + (System.currentTimeMillis() / 60000);
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(90));
        }
        return count == null || count <= requestsPerMinute;
    }
}
