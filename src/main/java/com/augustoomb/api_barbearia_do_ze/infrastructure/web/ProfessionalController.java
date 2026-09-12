package com.augustoomb.api_barbearia_do_ze.infrastructure.web;

import com.augustoomb.api_barbearia_do_ze.application.professional.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/professionals")
@RequiredArgsConstructor
@Tag(name = "Profissionais", description = "Gestão de profissionais da barbearia")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    @PostMapping
    @Operation(summary = "Cadastrar novo profissional")
    public ResponseEntity<ApiResponse<ProfessionalResponse>> create(@Valid @RequestBody CreateProfessionalRequest request) {
        ProfessionalResponse response = professionalService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Listar todos os profissionais cadastrados")
    public ResponseEntity<ApiResponse<List<ProfessionalResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(professionalService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar profissional por id")
    public ResponseEntity<ApiResponse<ProfessionalResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(professionalService.findById(id)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar parcialmente um profissional")
    public ResponseEntity<ApiResponse<ProfessionalResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProfessionalRequest request) {
        return ResponseEntity.ok(ApiResponse.success(professionalService.update(id, request)));
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Ativar um profissional")
    public ResponseEntity<ApiResponse<ProfessionalResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(professionalService.activate(id)));
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Desativar um profissional")
    public ResponseEntity<ApiResponse<ProfessionalResponse>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(professionalService.deactivate(id)));
    }
}
