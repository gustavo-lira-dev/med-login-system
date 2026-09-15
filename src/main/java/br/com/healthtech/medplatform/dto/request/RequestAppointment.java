package br.com.healthtech.medplatform.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RequestAppointment(
        @Future(message = "Scheduled Date must be a valid Date and Time value - in the future")
        LocalDateTime scheduledDate,

        @NotNull(message = "Field must not be null")
        Long medicId,

        @NotNull(message = "Field must not be null")
        Long clientId)


{}
