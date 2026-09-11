package br.com.healthtech.medplatform.exception;

import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Type of response DTO for throwable.
 */

public record ErrorResponse(
        LocalDateTime timestamp,
        HttpStatus status,
        String error,
        String message,
        String path,
        @Nullable HashMap<String, String> errors) //HashMap for validation constraint errors.
{
    // Secondary constructor for when it isn't a validation error.
    public ErrorResponse(HttpStatus status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }
}

