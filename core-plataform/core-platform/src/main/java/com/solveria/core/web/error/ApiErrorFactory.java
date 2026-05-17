package com.solveria.core.web.error;

import com.solveria.core.observability.context.CorrelationIdContext;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;

public final class ApiErrorFactory {

    private ApiErrorFactory() {}

    public static ApiErrorResponse fromStatus(HttpStatus status, String message, String path) {
        return new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                CorrelationIdContext.get(),
                List.of());
    }

    public static ApiErrorResponse validation(String message, String path, List<String> details) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                CorrelationIdContext.get(),
                details == null ? List.of() : List.copyOf(details));
    }
}
