package com.augustoomb.api_barbearia_do_ze.application.availability;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.UUID;

public record AvailabilityResponse(
        UUID id,
        UUID professionalId,
        @Schema(description = "Dia da semana. MONDAY = segunda-feira, SUNDAY = domingo.", example = "MONDAY")
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
