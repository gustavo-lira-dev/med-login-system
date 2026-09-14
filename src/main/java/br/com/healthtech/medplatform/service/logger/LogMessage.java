package br.com.healthtech.medplatform.service.logger;

import java.time.LocalDateTime;

public record LogMessage(
        LocalDateTime timestamp,
        String severity,
        String status,
        String message,
        String className,
        String method)
{
    public static LogMessage error(String message, String method, String className) {
        return new LogMessage(
                LocalDateTime.now(),
                "ERROR",
                "FAILURE",
                message,
                className,
                method);
    }

    public static LogMessage info(String message, String className, String method) {
        return new LogMessage(
                LocalDateTime.now(),
                "INFO",
                "SUCCESS",
                message,
                className,
                method);
    }

    public static LogMessage warn(String status, String message, String className, String method) {
        return new LogMessage(
                LocalDateTime.now(),
                "WARN",
                status,
                message,
                className,
                method);
    }
}
