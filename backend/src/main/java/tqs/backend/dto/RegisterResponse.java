package tqs.backend.dto;

public record RegisterResponse(
        String message,
        String userId,
        String email,
        String role
) {}
