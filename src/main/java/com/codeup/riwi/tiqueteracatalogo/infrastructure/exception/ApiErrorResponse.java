package com.codeup.riwi.tiqueteracatalogo.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RFC 7807 Problem Details compliant error response.
 * This class provides a standardized format for API error responses.
 * 
 * <p>RFC 7807 defines a "problem detail" as a way to carry machine-readable 
 * details of errors in an HTTP response to avoid the need to define new 
 * error response formats for HTTP APIs.</p>
 * 
 * <p>Standard Fields:</p>
 * <ul>
 *   <li><b>type</b> - A URI reference that identifies the problem type</li>
 *   <li><b>title</b> - A short, human-readable summary of the problem type</li>
 *   <li><b>status</b> - The HTTP status code</li>
 *   <li><b>detail</b> - A human-readable explanation specific to this occurrence</li>
 *   <li><b>instance</b> - A URI reference that identifies the specific occurrence</li>
 * </ul>
 * 
 * <p>Extended Fields:</p>
 * <ul>
 *   <li><b>timestamp</b> - The date/time when the error occurred</li>
 *   <li><b>traceId</b> - Unique identifier for tracing the request</li>
 *   <li><b>errors</b> - List of validation errors (for 400 responses)</li>
 *   <li><b>properties</b> - Additional custom properties</li>
 * </ul>
 * 
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7807">RFC 7807</a>
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "RFC 7807 Problem Details error response")
public class ApiErrorResponse {

    // ========================================================================
    // RFC 7807 Standard Fields
    // ========================================================================

    @Schema(
        description = "A URI reference that identifies the problem type",
        example = "https://api.tiqueteracatalogo.com/errors/validation-error"
    )
    private URI type;

    @Schema(
        description = "A short, human-readable summary of the problem type",
        example = "Validation Error"
    )
    private String title;

    @Schema(
        description = "The HTTP status code",
        example = "400"
    )
    private int status;

    @Schema(
        description = "A human-readable explanation specific to this occurrence of the problem",
        example = "One or more validation errors occurred"
    )
    private String detail;

    @Schema(
        description = "A URI reference that identifies the specific occurrence of the problem",
        example = "/api/v1/events/123"
    )
    private URI instance;

    // ========================================================================
    // Extended Fields
    // ========================================================================

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Schema(
        description = "The date and time when the error occurred",
        example = "2025-01-15T10:30:00.000-05:00"
    )
    private OffsetDateTime timestamp;

    @Schema(
        description = "Unique identifier for tracing this request",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private String traceId;

    @Schema(description = "List of validation errors (for 400 responses)")
    private List<ValidationError> errors;

    @Schema(description = "Additional custom properties")
    private Map<String, Object> properties;

    // ========================================================================
    // Constructors
    // ========================================================================

    /**
     * Default constructor
     */
    public ApiErrorResponse() {
        this.timestamp = OffsetDateTime.now();
        this.traceId = UUID.randomUUID().toString();
    }

    /**
     * Constructor with basic fields
     */
    public ApiErrorResponse(int status, String title, String detail) {
        this();
        this.status = status;
        this.title = title;
        this.detail = detail;
    }

    /**
     * Full constructor
     */
    public ApiErrorResponse(URI type, String title, int status, String detail, URI instance) {
        this();
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
    }

    // ========================================================================
    // Builder Pattern
    // ========================================================================

    /**
     * Creates a new builder for ApiErrorResponse
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for ApiErrorResponse
     */
    public static class Builder {
        private URI type;
        private String title;
        private int status;
        private String detail;
        private URI instance;
        private OffsetDateTime timestamp = OffsetDateTime.now();
        private String traceId = UUID.randomUUID().toString();
        private List<ValidationError> errors;
        private Map<String, Object> properties;

        public Builder type(URI type) {
            this.type = type;
            return this;
        }

        public Builder type(String type) {
            try {
                this.type = URI.create(type);
            } catch (IllegalArgumentException e) {
                this.type = URI.create("about:blank");
            }
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder instance(URI instance) {
            this.instance = instance;
            return this;
        }

        public Builder instance(String instance) {
            try {
                this.instance = URI.create(instance);
            } catch (IllegalArgumentException e) {
                this.instance = null;
            }
            return this;
        }

        public Builder timestamp(OffsetDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder errors(List<ValidationError> errors) {
            this.errors = errors;
            return this;
        }

        public Builder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public ApiErrorResponse build() {
            ApiErrorResponse response = new ApiErrorResponse();
            response.type = this.type;
            response.title = this.title;
            response.status = this.status;
            response.detail = this.detail;
            response.instance = this.instance;
            response.timestamp = this.timestamp;
            response.traceId = this.traceId;
            response.errors = this.errors;
            response.properties = this.properties;
            return response;
        }
    }

    // ========================================================================
    // Nested Classes
    // ========================================================================

    /**
     * Represents a single validation error
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Individual validation error details")
    public static class ValidationError {

        @Schema(description = "The field that failed validation", example = "name")
        private String field;

        @Schema(description = "The rejected value", example = "")
        private Object rejectedValue;

        @Schema(description = "The validation error message", example = "Event name is required")
        private String message;

        @Schema(description = "The validation error code", example = "NotBlank")
        private String code;

        public ValidationError() {
        }

        public ValidationError(String field, Object rejectedValue, String message, String code) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
            this.code = code;
        }

        // Getters and Setters
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    // ========================================================================
    // Getters and Setters
    // ========================================================================

    public URI getType() {
        return type;
    }

    public void setType(URI type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public URI getInstance() {
        return instance;
    }

    public void setInstance(URI instance) {
        this.instance = instance;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
