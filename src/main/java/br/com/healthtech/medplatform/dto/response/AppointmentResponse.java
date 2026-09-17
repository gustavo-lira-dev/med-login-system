package br.com.healthtech.medplatform.dto.response;

import java.time.LocalDateTime;

public record AppointmentResponse(String message, LocalDateTime scheduledDate, String medicEmail) {
}
