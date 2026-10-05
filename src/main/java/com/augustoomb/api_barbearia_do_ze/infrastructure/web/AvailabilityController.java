package com.augustoomb.api_barbearia_do_ze.infrastructure.web;

import com.augustoomb.api_barbearia_do_ze.application.availability.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/professionals/{professionalId}/availability")
@RequiredArgsConstructor
@Tag(name = "Disponibilidade", description = "Gestão da disponibilidade semanal dos profissionais")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    @Operation(summary = "Cadastrar disponibilidade para um profissional")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> create(
            @PathVariable UUID professionalId,
            @Valid @RequestBody CreateAvailabilityRequest request) {
        AvailabilityResponse response = availabilityService.create(professionalId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Listar disponibilidade de um profissional")
    public ResponseEntity<ApiResponse<List<AvailabilityResponse>>> findByProfessional(
            @PathVariable UUID professionalId,
            @RequestParam(required = false)
            @Parameter(description = "Dia da semana para filtrar. 1 = segunda-feira, 7 = domingo.", example = "1")
            Integer dayOfWeek) {
        return ResponseEntity.ok(ApiResponse.success(availabilityService.findByProfessional(professionalId, dayOfWeek)));
    }

    @PutMapping("/{availabilityId}")
    @Operation(summary = "Alterar um período de disponibilidade")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> update(
            @PathVariable UUID professionalId,
            @PathVariable UUID availabilityId,
            @Valid @RequestBody UpdateAvailabilityRequest request) {
        return ResponseEntity.ok(ApiResponse.success(availabilityService.update(professionalId, availabilityId, request)));
    }

    @DeleteMapping("/{availabilityId}")
    @Operation(summary = "Remover um período de disponibilidade")
    public ResponseEntity<Void> delete(
            @PathVariable UUID professionalId,
            @PathVariable UUID availabilityId) {
        availabilityService.delete(professionalId, availabilityId);
        return ResponseEntity.noContent().build();
    }
}
