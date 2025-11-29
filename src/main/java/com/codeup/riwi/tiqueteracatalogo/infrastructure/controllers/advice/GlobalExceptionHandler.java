package com.codeup.riwi.tiqueteracatalogo.infrastructure.controllers.advice;

import com.codeup.riwi.tiqueteracatalogo.domain.excepcion.BusinessRuleViolationException;
import com.codeup.riwi.tiqueteracatalogo.domain.excepcion.ConflictException;
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
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers.
 * Implements RFC 7807 Problem Details specification for error responses.
 * 
 * <p>This handler provides a consistent error response format across all controllers,
 * including:</p>
 * <ul>
 *   <li>Validation errors (400 Bad Request)</li>
 *   <li>Resource not found errors (404 Not Found)</li>
 *   <li>Business rule violations (422 Unprocessable Entity)</li>
 *   <li>Conflict errors (409 Conflict)</li>
 *   <li>Internal server errors (500 Internal Server Error)</li>
 * </ul>
 * 
 * @author TiqueteraCatalogo Team
 * @version 2.0
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7807">RFC 7807</a>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_TYPE_BASE = "https://api.tiqueteracatalogo.com/errors/";

    // ========================================================================
    // Helper method to get current traceId
    // ========================================================================
    
    /**
     * Gets the current traceId from MDC for log correlation.
     */
    private String getTraceId() {
        return MdcLoggingFilter.getCurrentTraceId();
    }

    /**
     * Structured log for errors with full context.
     */
    private void logError(String errorType, String endpoint, String userId, 
                          String message, Throwable ex) {
        String traceId = getTraceId();
        log.error("ERROR_HANDLED | type={} | traceId={} | endpoint={} | userId={} | message={}", 
            errorType, traceId, endpoint, userId, message, ex);
    }

    /**
     * Structured log for warnings with full context.
     */
    private void logWarn(String errorType, String endpoint, String message) {
        String traceId = getTraceId();
        String userId = MDC.get(MdcLoggingFilter.USER_ID);
        log.warn("WARN_HANDLED | type={} | traceId={} | endpoint={} | userId={} | message={}", 
            errorType, traceId, endpoint, userId != null ? userId : "anonymous", message);
    }

    /**
     * Structured log for info-level events with full context.
     */
    private void logInfo(String errorType, String endpoint, String message) {
        String traceId = getTraceId();
        log.info("INFO_HANDLED | type={} | traceId={} | endpoint={} | message={}", 
            errorType, traceId, endpoint, message);
    }

    // ========================================================================
    // Validation Errors (400 Bad Request)
    // ========================================================================

    /**
     * Handles validation errors from @Valid annotations on request body objects.
     * 
     * @param ex the MethodArgumentNotValidException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response with validation details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {

        List<ValidationError> validationErrors = ex.getBindingResult()
            .getAllErrors()
            .stream()
            .map(error -> {
                String fieldName = error instanceof FieldError 
                    ? ((FieldError) error).getField() 
                    : error.getObjectName();
                Object rejectedValue = error instanceof FieldError 
                    ? ((FieldError) error).getRejectedValue() 
                    : null;
                String code = error.getCode();
                return new ValidationError(fieldName, rejectedValue, error.getDefaultMessage(), code);
            })
            .collect(Collectors.toList());

        String traceId = getTraceId();
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "validation-error")
            .title("Validation Error")
            .status(HttpStatus.BAD_REQUEST.value())
            .detail("One or more validation errors occurred. Please check the 'errors' field for details.")
            .instance(request.getRequestURI())
            .traceId(traceId)
            .errors(validationErrors)
            .build();

        logWarn("VALIDATION_ERROR", request.getRequestURI(), 
            String.format("%d validation errors: %s", validationErrors.size(), 
                validationErrors.stream().map(e -> e.getField() + ":" + e.getMessage()).collect(Collectors.joining(", "))));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    /**
     * Handles constraint violations from @Validated annotations on path variables and parameters.
     * 
     * @param ex the ConstraintViolationException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, 
            HttpServletRequest request) {

        List<ValidationError> validationErrors = ex.getConstraintViolations()
            .stream()
            .map(violation -> {
                String field = extractFieldName(violation);
                return new ValidationError(
                    field, 
                    violation.getInvalidValue(), 
                    violation.getMessage(),
                    violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()
                );
            })
            .collect(Collectors.toList());

        String traceId = getTraceId();
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "constraint-violation")
            .title("Constraint Violation")
            .status(HttpStatus.BAD_REQUEST.value())
            .detail("Request parameters or path variables failed validation.")
            .instance(request.getRequestURI())
            .traceId(traceId)
            .errors(validationErrors)
            .build();

        logWarn("CONSTRAINT_VIOLATION", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    /**
     * Handles malformed JSON or unreadable request body.
     * 
     * @param ex the HttpMessageNotReadableException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, 
            HttpServletRequest request) {

        String detail = "The request body is malformed or contains invalid JSON. Please verify the request format.";
        
        // Provide more specific messages for common issues
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("Required request body is missing")) {
                detail = "Request body is required but was not provided.";
            } else if (message.contains("Cannot deserialize value of type")) {
                detail = "Invalid value type in request body. Please check data types.";
            } else if (message.contains("Unexpected character")) {
                detail = "Invalid JSON format. Please verify the JSON syntax.";
            }
        }

        String traceId = getTraceId();
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "malformed-request")
            .title("Malformed Request")
            .status(HttpStatus.BAD_REQUEST.value())
            .detail(detail)
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("MALFORMED_REQUEST", request.getRequestURI(), detail);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    /**
     * Handles missing request parameters.
     * 
     * @param ex the MissingServletRequestParameterException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        String detail = String.format("Required parameter '%s' of type '%s' is missing.", 
            ex.getParameterName(), ex.getParameterType());
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "missing-parameter")
            .title("Missing Parameter")
            .status(HttpStatus.BAD_REQUEST.value())
            .detail(detail)
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("MISSING_PARAMETER", request.getRequestURI(), 
            String.format("Missing parameter: %s", ex.getParameterName()));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    /**
     * Handles type mismatch errors for path variables and parameters.
     * 
     * @param ex the MethodArgumentTypeMismatchException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, 
            HttpServletRequest request) {

        String requiredType = ex.getRequiredType() != null 
            ? ex.getRequiredType().getSimpleName() 
            : "unknown";

        String traceId = getTraceId();
        String detail = String.format("Parameter '%s' with value '%s' could not be converted to type '%s'.", 
            ex.getName(), ex.getValue(), requiredType);
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "type-mismatch")
            .title("Type Mismatch")
            .status(HttpStatus.BAD_REQUEST.value())
            .detail(detail)
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("TYPE_MISMATCH", request.getRequestURI(), 
            String.format("Parameter '%s' expected type '%s', got '%s'", ex.getName(), requiredType, ex.getValue()));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    // ========================================================================
    // Not Found Errors (404)
    // ========================================================================

    /**
     * Handles resource not found exceptions.
     * 
     * @param ex the RecursoNoEncontradoException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
            RecursoNoEncontradoException ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "resource-not-found")
            .title("Resource Not Found")
            .status(HttpStatus.NOT_FOUND.value())
            .detail(ex.getMessage())
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logInfo("RESOURCE_NOT_FOUND", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    /**
     * Handles requests to non-existent endpoints.
     * 
     * @param ex the NoHandlerFoundException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        String detail = String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL());
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "endpoint-not-found")
            .title("Endpoint Not Found")
            .status(HttpStatus.NOT_FOUND.value())
            .detail(detail)
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("ENDPOINT_NOT_FOUND", request.getRequestURI(), detail);

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    // ========================================================================
    // Method Not Allowed (405) / Unsupported Media Type (415)
    // ========================================================================

    /**
     * Handles unsupported HTTP methods.
     * 
     * @param ex the HttpRequestMethodNotSupportedException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        String detail = String.format("HTTP method '%s' is not supported for this endpoint. Supported methods: %s", 
            ex.getMethod(), ex.getSupportedHttpMethods());
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "method-not-allowed")
            .title("Method Not Allowed")
            .status(HttpStatus.METHOD_NOT_ALLOWED.value())
            .detail(detail)
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("METHOD_NOT_ALLOWED", request.getRequestURI(), detail);

        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    /**
     * Handles unsupported media types.
     * 
     * @param ex the HttpMediaTypeNotSupportedException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        String detail = String.format("Content type '%s' is not supported. Supported types: %s", 
            ex.getContentType(), ex.getSupportedMediaTypes());
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "unsupported-media-type")
            .title("Unsupported Media Type")
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
            .detail(detail)
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("UNSUPPORTED_MEDIA_TYPE", request.getRequestURI(), detail);

        return ResponseEntity
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    // ========================================================================
    // Conflict Errors (409)
    // ========================================================================

    /**
     * Handles conflict exceptions (e.g., duplicate resources).
     * 
     * @param ex the ConflictException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflictException(
            ConflictException ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "conflict")
            .title("Conflict")
            .status(HttpStatus.CONFLICT.value())
            .detail(ex.getMessage())
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("CONFLICT", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    /**
     * Handles illegal argument exceptions.
     * 
     * @param ex the IllegalArgumentException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "invalid-argument")
            .title("Invalid Argument")
            .status(HttpStatus.CONFLICT.value())
            .detail(ex.getMessage())
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("INVALID_ARGUMENT", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    // ========================================================================
    // Business Rule Violations (422 Unprocessable Entity)
    // ========================================================================

    /**
     * Handles business rule violation exceptions.
     * 
     * @param ex the BusinessRuleViolationException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessRuleViolation(
            BusinessRuleViolationException ex, 
            HttpServletRequest request) {

        String typeUri = ex.getRuleCode() != null 
            ? ERROR_TYPE_BASE + "business-rule/" + ex.getRuleCode()
            : ERROR_TYPE_BASE + "business-rule-violation";

        String traceId = getTraceId();
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(typeUri)
            .title("Business Rule Violation")
            .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .detail(ex.getMessage())
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("BUSINESS_RULE_VIOLATION", request.getRequestURI(), 
            String.format("Rule: %s, Message: %s", ex.getRuleCode(), ex.getMessage()));

        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    // ========================================================================
    // Data Integrity Errors (409 or 400)
    // ========================================================================

    /**
     * Handles data integrity violation exceptions from the database layer.
     * 
     * @param ex the DataIntegrityViolationException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, 
            HttpServletRequest request) {

        String detail = "A data integrity constraint was violated. Please verify your request data.";
        String message = ex.getMostSpecificCause().getMessage();

        // Provide more specific messages for common constraint violations
        if (message != null) {
            if (message.contains("unique constraint") || message.contains("Unique index")) {
                detail = "A resource with the same unique identifier already exists.";
            } else if (message.contains("foreign key constraint") || message.contains("FK_")) {
                detail = "The operation references a resource that does not exist.";
            } else if (message.contains("not-null constraint") || message.contains("NULL not allowed")) {
                detail = "A required field is missing or null.";
            } else if (message.contains("EVENT_DATE")) {
                detail = "Event date is required and must have a valid format (e.g., 2025-12-15T20:00:00).";
            } else if (message.contains("VENUE_ID")) {
                detail = "Venue ID is required and must reference an existing venue.";
            }
        }

        String traceId = getTraceId();
        String userId = MDC.get(MdcLoggingFilter.USER_ID);
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "data-integrity-violation")
            .title("Data Integrity Violation")
            .status(HttpStatus.CONFLICT.value())
            .detail(detail)
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logError("DATA_INTEGRITY_VIOLATION", request.getRequestURI(), 
            userId != null ? userId : "anonymous", message, ex);

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    // ========================================================================
    // Authentication/Authorization Errors (401/403)
    // ========================================================================

    /**
     * Handles bad credentials exception (wrong email or password).
     * 
     * @param ex the BadCredentialsException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "authentication-failed")
            .title("Authentication Failed")
            .status(HttpStatus.UNAUTHORIZED.value())
            .detail("Invalid email or password. Please check your credentials and try again.")
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("AUTHENTICATION_FAILED", request.getRequestURI(), "Bad credentials attempt");

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    /**
     * Handles disabled account exception.
     * 
     * @param ex the DisabledException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiErrorResponse> handleDisabledException(
            DisabledException ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "account-disabled")
            .title("Account Disabled")
            .status(HttpStatus.UNAUTHORIZED.value())
            .detail("Your account has been disabled. Please contact support for assistance.")
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("ACCOUNT_DISABLED", request.getRequestURI(), "Disabled account access attempt");

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    /**
     * Handles locked account exception.
     * 
     * @param ex the LockedException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiErrorResponse> handleLockedException(
            LockedException ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "account-locked")
            .title("Account Locked")
            .status(HttpStatus.UNAUTHORIZED.value())
            .detail("Your account has been locked due to too many failed attempts. Please try again later or contact support.")
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("ACCOUNT_LOCKED", request.getRequestURI(), "Locked account access attempt");

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    /**
     * Handles generic authentication exceptions.
     * 
     * @param ex the AuthenticationException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            AuthenticationException ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "authentication-required")
            .title("Authentication Required")
            .status(HttpStatus.UNAUTHORIZED.value())
            .detail("Authentication is required to access this resource. Please provide valid credentials.")
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("AUTHENTICATION_REQUIRED", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    /**
     * Handles access denied exception (insufficient permissions).
     * 
     * @param ex the AccessDeniedException
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        String userId = MDC.get(MdcLoggingFilter.USER_ID);
        
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "access-denied")
            .title("Access Denied")
            .status(HttpStatus.FORBIDDEN.value())
            .detail("You do not have permission to access this resource.")
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        logWarn("ACCESS_DENIED", request.getRequestURI(), 
            String.format("User '%s' denied access", userId != null ? userId : "anonymous"));

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    // ========================================================================
    // Internal Server Errors (500)
    // ========================================================================

    /**
     * Handles all unhandled exceptions as internal server errors.
     * This is the catch-all handler that ensures no exception leaks sensitive information.
     * 
     * @param ex the Exception
     * @param request the HTTP request
     * @return RFC 7807 compliant error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
            Exception ex, 
            HttpServletRequest request) {

        String traceId = getTraceId();
        String userId = MDC.get(MdcLoggingFilter.USER_ID);
        
        // Log the full exception for debugging with structured format
        logError("INTERNAL_SERVER_ERROR", request.getRequestURI(), 
            userId != null ? userId : "anonymous", 
            ex.getMessage(), ex);

        // Return generic message to client (don't expose internal details)
        ApiErrorResponse response = ApiErrorResponse.builder()
            .type(ERROR_TYPE_BASE + "internal-error")
            .title("Internal Server Error")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .detail("An unexpected error occurred. Please try again later or contact support if the problem persists.")
            .instance(request.getRequestURI())
            .traceId(traceId)
            .build();

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(response);
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    /**
     * Extracts the field name from a constraint violation path.
     * 
     * @param violation the constraint violation
     * @return the field name
     */
    private String extractFieldName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        // Extract just the field name from paths like "methodName.parameterName.fieldName"
        int lastDot = path.lastIndexOf('.');
        return lastDot > 0 ? path.substring(lastDot + 1) : path;
    }
}
