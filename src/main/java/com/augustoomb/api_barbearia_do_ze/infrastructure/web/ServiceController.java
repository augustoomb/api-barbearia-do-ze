package com.augustoomb.api_barbearia_do_ze.infrastructure.web;

import com.augustoomb.api_barbearia_do_ze.application.service.*;
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
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Tag(name = "Serviços", description = "Gestão de serviços oferecidos pela barbearia")
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    @Operation(summary = "Cadastrar novo serviço")
    public ResponseEntity<ApiResponse<ServiceResponse>> create(@Valid @RequestBody CreateServiceRequest request) {
        ServiceResponse response = serviceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Listar todos os serviços cadastrados")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(serviceService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar serviço por id")
    public ResponseEntity<ApiResponse<ServiceResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(serviceService.findById(id)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar parcialmente um serviço")
    public ResponseEntity<ApiResponse<ServiceResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateServiceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(serviceService.update(id, request)));
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Ativar um serviço")
    public ResponseEntity<ApiResponse<ServiceResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(serviceService.activate(id)));
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Desativar um serviço")
    public ResponseEntity<ApiResponse<ServiceResponse>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(serviceService.deactivate(id)));
    }
}
