package br.com.healthtech.medplatform.dto.response;

/**
 * Data Transfer Object representing a successful authentication response.
 */

public record UserAuthResponse(
        String message,
        String email,
        String token
) {}

