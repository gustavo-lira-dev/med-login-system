package br.com.healthtech.medplatform.dto.response;

import java.time.LocalDateTime;

public record AppointmentResponse(LocalDateTime scheduledDate, String medicEmail) {
}
