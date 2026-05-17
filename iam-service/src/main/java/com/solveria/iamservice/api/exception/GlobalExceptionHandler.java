package com.solveria.iamservice.api.exception;

import com.solveria.core.shared.exceptions.EntityNotFoundException;
import com.solveria.core.shared.exceptions.SolverException;
import com.solveria.iamservice.api.exception.dto.ApiErrorResponse;
import com.solveria.iamservice.application.exception.IamServiceException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SolverException.class)
    public ResponseEntity<ApiErrorResponse> handleSolverException(
            SolverException ex, HttpServletRequest request) {
        HttpStatus status = determineHttpStatus(ex);

        log.error(
                "event=IAM_API_EXCEPTION_HANDLED errorCode={} status={} path={}",
                ex.getCode(),
                status.value(),
                request.getRequestURI(),
                ex);

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(ex.getCode(), Instant.now(), request.getRequestURI());

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(IamServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleIamServiceException(
            IamServiceException ex, HttpServletRequest request) {
        log.error(
                "event=IAM_API_EXCEPTION_HANDLED errorCode={} status={} path={}",
                ex.getErrorCode(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI(),
                ex);

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(ex.getErrorCode(), Instant.now(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(
                        error -> {
                            fieldErrors.put(error.getField(), error.getDefaultMessage());
                        });

        log.warn(
                "event=IAM_API_EXCEPTION_HANDLED errorCode={} status={} path={}",
                ErrorCodes.VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                ex);

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(
                        ErrorCodes.VALIDATION_ERROR,
                        Instant.now(),
                        request.getRequestURI(),
                        fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ApiErrorResponse errorResponse =
                new ApiErrorResponse(
                        status.getReasonPhrase(), Instant.now(), request.getRequestURI());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(
            Exception ex, HttpServletRequest request) {
        log.error(
                "event=IAM_API_EXCEPTION_HANDLED errorCode={} status={} path={}",
                ErrorCodes.UNEXPECTED_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI(),
                ex);

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(
                        ErrorCodes.UNEXPECTED_ERROR, Instant.now(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private HttpStatus determineHttpStatus(SolverException ex) {
        if (ex instanceof EntityNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }
}
