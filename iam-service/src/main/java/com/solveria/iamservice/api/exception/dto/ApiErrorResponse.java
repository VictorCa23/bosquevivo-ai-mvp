package com.solveria.iamservice.api.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Standard error response format")
public record ApiErrorResponse(
        @Schema(
                        description = "Error code (i18n key)",
                        example = "VALIDATION_ERROR",
                        required = true)
                String errorCode,
        @Schema(
                        description = "Timestamp when error occurred",
                        example = "2024-01-15T10:30:00Z",
                        required = true)
                Instant timestamp,
        @Schema(
                        description = "Request path where error occurred",
                        example = "/api/v1/iam/roles/1/permissions",
                        required = true)
                String path,
        @Schema(
                        description = "Additional error details (field-level validation errors)",
                        example = "{\"permissionIds\": \"must not be empty\"}")
                Map<String, String> details,
        @Schema(
                        description = "Correlation ID for request tracing",
                        example = "550e8400-e29b-41d4-a716-446655440000")
                String correlationId) {
    public ApiErrorResponse(String errorCode, Instant timestamp, String path) {
        this(errorCode, timestamp, path, null, null);
    }

    public ApiErrorResponse(
            String errorCode, Instant timestamp, String path, Map<String, String> details) {
        this(errorCode, timestamp, path, details, null);
    }
}
