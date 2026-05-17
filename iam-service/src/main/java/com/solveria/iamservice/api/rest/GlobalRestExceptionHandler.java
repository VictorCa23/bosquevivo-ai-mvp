package com.solveria.iamservice.api.rest;

import com.solveria.core.shared.exceptions.ApplicationException;
import com.solveria.core.shared.exceptions.BusinessRuleViolationException;
import com.solveria.core.shared.exceptions.DomainException;
import com.solveria.core.shared.exceptions.EntityNotFoundException;
import com.solveria.core.shared.exceptions.PermissionDeniedException;
import com.solveria.core.shared.exceptions.SolverException;
import com.solveria.iamservice.api.rest.dto.ErrorResponse;
import com.solveria.iamservice.application.exception.IamServiceException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);
    private static final String DEFAULT_ERROR_CODE = "IAM_INTERNAL_ERROR";

    private final MessageSource messageSource;

    public GlobalRestExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex, HttpServletRequest request, WebRequest webRequest) {
        return handleSolverException(ex, HttpStatus.NOT_FOUND, request, webRequest);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ErrorResponse> handlePermissionDeniedException(
            PermissionDeniedException ex, HttpServletRequest request, WebRequest webRequest) {
        return handleSolverException(ex, HttpStatus.FORBIDDEN, request, webRequest);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolationException(
            BusinessRuleViolationException ex, HttpServletRequest request, WebRequest webRequest) {
        return handleSolverException(ex, HttpStatus.CONFLICT, request, webRequest);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex, HttpServletRequest request, WebRequest webRequest) {
        return handleSolverException(ex, HttpStatus.BAD_REQUEST, request, webRequest);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(
            ApplicationException ex, HttpServletRequest request, WebRequest webRequest) {
        return handleSolverException(ex, HttpStatus.BAD_REQUEST, request, webRequest);
    }

    @ExceptionHandler(SolverException.class)
    public ResponseEntity<ErrorResponse> handleSolverException(
            SolverException ex, HttpServletRequest request, WebRequest webRequest) {
        return handleSolverException(ex, HttpStatus.BAD_REQUEST, request, webRequest);
    }

    private ResponseEntity<ErrorResponse> handleSolverException(
            SolverException ex,
            HttpStatus status,
            HttpServletRequest request,
            WebRequest webRequest) {
        Locale locale = webRequest.getLocale();
        String message = resolveMessage(ex.getCode(), ex.getArgs(), locale);

        log.error(
                "event=IAM_ERROR errorCode={} status={} path={}",
                ex.getCode(),
                status.value(),
                request.getRequestURI(),
                ex);

        ErrorResponse errorResponse =
                new ErrorResponse(ex.getCode(), message, Instant.now(), request.getRequestURI());

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(IamServiceException.class)
    public ResponseEntity<ErrorResponse> handleIamServiceException(
            IamServiceException ex, HttpServletRequest request, WebRequest webRequest) {
        Locale locale = webRequest.getLocale();
        String message = resolveMessage(ex.getErrorCode(), Map.of(), locale);

        log.error(
                "event=IAM_SERVICE_ERROR errorCode={} path={}",
                ex.getErrorCode(),
                request.getRequestURI(),
                ex);

        ErrorResponse errorResponse =
                new ErrorResponse(
                        ex.getErrorCode(), message, Instant.now(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request, WebRequest webRequest) {
        Locale locale = webRequest.getLocale();
        String message = resolveMessage("validation.error.generic", Map.of(), locale);

        log.error("event=IAM_VALIDATION_ERROR path={}", request.getRequestURI(), ex);

        ErrorResponse errorResponse =
                new ErrorResponse(
                        "VALIDATION_ERROR", message, Instant.now(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ErrorResponse errorResponse =
                new ErrorResponse(
                        status.getReasonPhrase(),
                        ex.getReason(),
                        Instant.now(),
                        request.getRequestURI());

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request, WebRequest webRequest) {
        Locale locale = webRequest.getLocale();
        String message = resolveMessage(DEFAULT_ERROR_CODE, Map.of(), locale);

        log.error(
                "event=IAM_UNEXPECTED_ERROR errorCode={} path={}",
                DEFAULT_ERROR_CODE,
                request.getRequestURI(),
                ex);

        ErrorResponse errorResponse =
                new ErrorResponse(
                        DEFAULT_ERROR_CODE, message, Instant.now(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private String resolveMessage(String code, Map<String, Object> args, Locale locale) {
        try {
            Object[] argsArray = args.values().toArray();
            return messageSource.getMessage(code, argsArray, locale);
        } catch (Exception e) {
            log.warn("event=IAM_I18N_RESOLUTION_FAILED errorCode={} locale={}", code, locale, e);
            try {
                return messageSource.getMessage(code, args.values().toArray(), Locale.ENGLISH);
            } catch (Exception fallbackEx) {
                log.error("event=IAM_I18N_FALLBACK_FAILED errorCode={}", code, fallbackEx);
                return code;
            }
        }
    }
}
