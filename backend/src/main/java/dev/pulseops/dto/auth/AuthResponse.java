package dev.pulseops.dto.auth;

public record AuthResponse(
        String token,
        String email,
        String name,
        Long userId
) {}
