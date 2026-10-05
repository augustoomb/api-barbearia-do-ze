package com.augustoomb.api_barbearia_do_ze.infrastructure.web;

import com.augustoomb.api_barbearia_do_ze.domain.availability.AvailabilityNotFoundException;
import com.augustoomb.api_barbearia_do_ze.domain.availability.InvalidAvailabilityException;
import com.augustoomb.api_barbearia_do_ze.domain.availability.OverlappingAvailabilityException;
import com.augustoomb.api_barbearia_do_ze.domain.professional.DuplicateProfessionalEmailException;
import com.augustoomb.api_barbearia_do_ze.domain.professional.InvalidProfessionalException;
import com.augustoomb.api_barbearia_do_ze.domain.professional.ProfessionalNotFoundException;
import com.augustoomb.api_barbearia_do_ze.domain.service.DuplicateServiceNameException;
import com.augustoomb.api_barbearia_do_ze.domain.service.InvalidServiceException;
import com.augustoomb.api_barbearia_do_ze.domain.service.ServiceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ServiceNotFoundException ex, HttpServletRequest request) {
        log.warn("Serviço não encontrado: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateServiceNameException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(DuplicateServiceNameException ex, HttpServletRequest request) {
        log.warn("Conflito de nome de serviço: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalid(InvalidServiceException ex, HttpServletRequest request) {
        log.warn("Requisição inválida: {} - {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ProfessionalNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleProfessionalNotFound(ProfessionalNotFoundException ex, HttpServletRequest request) {
        log.warn("Profissional não encontrado: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(DuplicateProfessionalEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateProfessionalEmail(DuplicateProfessionalEmailException ex, HttpServletRequest request) {
        log.warn("Conflito de e-mail de profissional: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidProfessionalException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidProfessional(InvalidProfessionalException ex, HttpServletRequest request) {
        log.warn("Requisição inválida para profissional: {} - {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AvailabilityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleAvailabilityNotFound(AvailabilityNotFoundException ex, HttpServletRequest request) {
        log.warn("Período de disponibilidade não encontrado: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidAvailabilityException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidAvailability(InvalidAvailabilityException ex, HttpServletRequest request) {
        log.warn("Requisição inválida para disponibilidade: {} - {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(OverlappingAvailabilityException.class)
    public ResponseEntity<ApiResponse<Void>> handleOverlappingAvailability(OverlappingAvailabilityException ex, HttpServletRequest request) {
        log.warn("Conflito de disponibilidade: {} - {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Erro de validação: {}", request.getRequestURI());
        List<ApiResponse.ApiError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiResponse.ApiError(error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado em {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erro interno no servidor"));
    }
}
