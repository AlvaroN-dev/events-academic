package com.codeup.riwi.tiqueteracatalogo.infrastructure.controllers.advice;

import com.codeup.riwi.tiqueteracatalogo.domain.excepcion.RecursoNoEncontradoException;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.config.logging.MdcLoggingFilter;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.exception.ApiErrorResponse;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.exception.ApiErrorResponse.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers.
 * Implements RFC 7807 Problem Details specification.
 * 
 * @author TiqueteraCatalogo Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_TYPE_BASE = "https://api.tiqueteracatalogo.com/errors/";

    // ========================================================================
    // Helper Methods
    // ========================================================================
    
    private String getTraceId() {
        return MdcLoggingFilter.getCurrentTraceId();
    }

    private String getUserId() {
        String userId = MDC.get(MdcLoggingFilter.USER_ID);
        return userId != null ? userId : "anonymous";
    }

    // ========================================================================
    // Validation Errors (400 Bad Request)
    // ========================================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {

        List<ValidationError> validationErrors = ex.getBindingResult()
            .getAllErrors()
            .stream()
            .map(error -> {
                String fieldName = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
                Object rejectedValue = error instanceof FieldError fe ? fe.getRejectedValue() : null;
                return new ValidationError(fieldName, rejectedValue, error.getDefaultMessage(), error.getCode());
            })
            .collect(Collectors.toList());

        log.warn("VALIDATION_ERROR | traceId={} | endpoint={} | errors={}", 
            getTraceId(), request.getRequestURI(), validationErrors.size());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ApiErrorResponse.builder()
                .type(ERROR_TYPE_BASE + "validation-error")
                .title("Validation Error")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("One or more validation errors occurred.")
                .instance(request.getRequestURI())
                .traceId(getTraceId())
                .errors(validationErrors)
                .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, 
            HttpServletRequest request) {

        List<ValidationError> validationErrors = ex.getConstraintViolations()
            .stream()
            .map(v -> new ValidationError(
                extractFieldName(v), 
                v.getInvalidValue(), 
                v.getMessage(),
                v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()))
            .collect(Collectors.toList());

        log.warn("CONSTRAINT_VIOLATION | traceId={} | endpoint={}", getTraceId(), request.getRequestURI());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ApiErrorResponse.builder()
                .type(ERROR_TYPE_BASE + "constraint-violation")
                .title("Constraint Violation")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("Request parameters or path variables failed validation.")
                .instance(request.getRequestURI())
                .traceId(getTraceId())
                .errors(validationErrors)
                .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, 
            HttpServletRequest request) {

        String detail = "The request body is malformed or contains invalid JSON.";
        String message = ex.getMessage();
        
        if (message != null) {
            if (message.contains("Required request body is missing")) {
                detail = "Request body is required but was not provided.";
            } else if (message.contains("Cannot deserialize")) {
                detail = "Invalid value type in request body.";
            }
        }

        log.warn("MALFORMED_REQUEST | traceId={} | endpoint={}", getTraceId(), request.getRequestURI());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ApiErrorResponse.builder()
                .type(ERROR_TYPE_BASE + "malformed-request")
                .title("Malformed Request")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(detail)
                .instance(request.getRequestURI())
                .traceId(getTraceId())
                .build());
    }

    // ========================================================================
    // Not Found (404)
    // ========================================================================

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
            RecursoNoEncontradoException ex, 
            HttpServletRequest request) {

        log.info("RESOURCE_NOT_FOUND | traceId={} | endpoint={} | message={}", 
            getTraceId(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ApiErrorResponse.builder()
                .type(ERROR_TYPE_BASE + "resource-not-found")
                .title("Resource Not Found")
                .status(HttpStatus.NOT_FOUND.value())
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .traceId(getTraceId())
                .build());
    }

    // ========================================================================
    // Authentication/Authorization (401/403)
    // ========================================================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, 
            HttpServletRequest request) {

        log.warn("AUTHENTICATION_FAILED | traceId={} | endpoint={}", getTraceId(), request.getRequestURI());

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ApiErrorResponse.builder()
                .type(ERROR_TYPE_BASE + "authentication-failed")
                .title("Authentication Failed")
                .status(HttpStatus.UNAUTHORIZED.value())
                .detail("Invalid email or password.")
                .instance(request.getRequestURI())
                .traceId(getTraceId())
                .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, 
            HttpServletRequest request) {

        log.warn("ACCESS_DENIED | traceId={} | endpoint={} | userId={}", 
            getTraceId(), request.getRequestURI(), getUserId());

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ApiErrorResponse.builder()
                .type(ERROR_TYPE_BASE + "access-denied")
                .title("Access Denied")
                .status(HttpStatus.FORBIDDEN.value())
                .detail("You do not have permission to access this resource.")
                .instance(request.getRequestURI())
                .traceId(getTraceId())
                .build());
    }

    // ========================================================================
    // Data Integrity (409 Conflict)
    // ========================================================================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, 
            HttpServletRequest request) {

        String detail = "A data integrity constraint was violated.";
        String message = ex.getMostSpecificCause().getMessage();

        if (message != null) {
            if (message.contains("unique constraint") || message.contains("Unique index")) {
                detail = "A resource with the same unique identifier already exists.";
            } else if (message.contains("foreign key") || message.contains("FK_")) {
                detail = "The operation references a resource that does not exist.";
            } else if (message.contains("not-null") || message.contains("NULL not allowed")) {
                detail = "A required field is missing or null.";
            }
        }

        log.error("DATA_INTEGRITY_VIOLATION | traceId={} | endpoint={} | userId={}", 
            getTraceId(), request.getRequestURI(), getUserId(), ex);

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ApiErrorResponse.builder()
                .type(ERROR_TYPE_BASE + "data-integrity-violation")
                .title("Data Integrity Violation")
                .status(HttpStatus.CONFLICT.value())
                .detail(detail)
                .instance(request.getRequestURI())
                .traceId(getTraceId())
                .build());
    }

    // ========================================================================
    // Internal Server Error (500) - Catch-all
    // ========================================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
            Exception ex, 
            HttpServletRequest request) {

        log.error("INTERNAL_SERVER_ERROR | traceId={} | endpoint={} | userId={} | message={}", 
            getTraceId(), request.getRequestURI(), getUserId(), ex.getMessage(), ex);

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(ApiErrorResponse.builder()
                .type(ERROR_TYPE_BASE + "internal-error")
                .title("Internal Server Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail("An unexpected error occurred. Please try again later.")
                .instance(request.getRequestURI())
                .traceId(getTraceId())
                .build());
    }

    // ========================================================================
    // Private Helpers
    // ========================================================================

    private String extractFieldName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int lastDot = path.lastIndexOf('.');
        return lastDot > 0 ? path.substring(lastDot + 1) : path;
    }
}
