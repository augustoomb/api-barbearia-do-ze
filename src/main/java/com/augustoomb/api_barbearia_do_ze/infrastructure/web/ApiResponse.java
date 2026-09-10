package com.augustoomb.api_barbearia_do_ze.infrastructure.web;

import java.time.Instant;
import java.util.List;

public record ApiResponse<T>(
        T data,
        List<ApiError> errors,
        Instant timestamp
) {

    public ApiResponse {
        timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, List.of(), Instant.now());
    }

    public static <T> ApiResponse<T> error(List<ApiError> errors) {
        return new ApiResponse<>(null, errors, Instant.now());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(null, List.of(new ApiError(message)), Instant.now());
    }

    public static <T> ApiResponse<T> error(String field, String message) {
        return new ApiResponse<>(null, List.of(new ApiError(field, message)), Instant.now());
    }

    public record ApiError(String field, String message) {
        public ApiError(String message) {
            this(null, message);
        }
    }
}
