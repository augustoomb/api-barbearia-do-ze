package com.augustoomb.api_barbearia_do_ze.application.service;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdateServiceRequest(
        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
        String name,

        @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
        String description,

        @Min(value = 1, message = "Duração deve ser maior que zero")
        Integer durationMinutes,

        @DecimalMin(value = "0.00", inclusive = true, message = "Preço não pode ser negativo")
        @Digits(integer = 8, fraction = 2, message = "Preço deve ter no máximo 8 dígitos inteiros e 2 decimais")
        BigDecimal price
) {
}
