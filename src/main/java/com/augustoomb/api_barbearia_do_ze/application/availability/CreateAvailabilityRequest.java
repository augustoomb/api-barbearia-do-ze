package com.augustoomb.api_barbearia_do_ze.application.availability;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateAvailabilityRequest(
        @NotNull(message = "Dia da semana é obrigatório")
        @Min(value = 1, message = "Dia da semana deve ser no mínimo 1")
        @Max(value = 7, message = "Dia da semana deve ser no máximo 7")
        @Schema(description = "Dia da semana. 1 = segunda-feira, 7 = domingo.", example = "1", minimum = "1", maximum = "7")
        Integer dayOfWeek,

        @NotNull(message = "Horário de início é obrigatório")
        @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Horário de início deve estar no formato HH:mm")
        String startTime,

        @NotNull(message = "Horário de término é obrigatório")
        @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Horário de término deve estar no formato HH:mm")
        String endTime
) {
}
