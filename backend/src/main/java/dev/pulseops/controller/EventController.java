package dev.pulseops.controller;

import dev.pulseops.dto.event.CreateEventRequest;
import dev.pulseops.entity.ApiKey;
import dev.pulseops.entity.CustomEvent;
import dev.pulseops.repository.CustomEventRepository;
import dev.pulseops.service.ApiKeyService;
import dev.pulseops.service.RateLimiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Custom Events")
public class EventController {

    private final ApiKeyService apiKeyService;
    private final CustomEventRepository customEventRepository;
    private final RateLimiterService rateLimiterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit a custom event using an API key")
    public Map<String, String> submit(@Valid @RequestBody CreateEventRequest request) {
        String rawKey = request.apiKey();
        String prefix = rawKey.substring(0, Math.min(10, rawKey.length()));

        if (!rateLimiterService.isAllowed(prefix)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded: 60 requests/minute");
        }

        ApiKey apiKey = apiKeyService.validateKey(rawKey);

        CustomEvent event = CustomEvent.builder()
                .eventType(request.eventType())
                .payload(request.payload())
                .apiKey(apiKey)
                .project(apiKey.getProject())
                .build();
        customEventRepository.save(event);

        return Map.of("status", "accepted", "eventType", request.eventType());
    }
}
