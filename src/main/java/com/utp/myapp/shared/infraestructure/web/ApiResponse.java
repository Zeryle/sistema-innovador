package com.utp.myapp.shared.infraestructure.web;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Standard API response wrapper for consistent REST responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Instant timestamp,
        List<String> errors
) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "Success", data, Instant.now(), null);
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, message, data, Instant.now(), null);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, "Resource created successfully", data, Instant.now(), null);
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return new ApiResponse<>(false, message, null, Instant.now(), errors);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, Instant.now(), List.of(message));
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(false, message, null, Instant.now(), List.of(message));
    }
}
