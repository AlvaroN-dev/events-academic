package com.codeup.riwi.tiqueteracatalogo.infrastructure.config.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Centralized logging service for structured application logging.
 * Provides convenience methods for consistent log formatting across the application.
 * 
 * <p>All log messages automatically include:</p>
 * <ul>
 *   <li>traceId - For request correlation</li>
 *   <li>timestamp - When the event occurred</li>
 *   <li>level - Log severity (INFO, WARN, ERROR)</li>
 *   <li>Additional context from MDC</li>
 * </ul>
 * 
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * @Autowired
 * private LoggingService loggingService;
 * 
 * public void processEvent(Long eventId) {
 *     loggingService.info("EVENT_PROCESS", Map.of("eventId", eventId, "action", "start"));
 *     // ... processing logic
 *     loggingService.info("EVENT_PROCESS", Map.of("eventId", eventId, "action", "complete"));
 * }
 * }
 * </pre>
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Service
public class LoggingService {

    private static final Logger log = LoggerFactory.getLogger(LoggingService.class);

    // ========================================================================
    // Business Event Logging
    // ========================================================================

    /**
     * Logs a business event at INFO level.
     * 
     * @param eventType the type of business event (e.g., "EVENT_CREATED", "VENUE_UPDATED")
     * @param context additional context as key-value pairs
     */
    public void logBusinessEvent(String eventType, Map<String, Object> context) {
        String traceId = MdcLoggingFilter.getCurrentTraceId();
        String userId = MDC.get(MdcLoggingFilter.USER_ID);
        
        StringBuilder message = new StringBuilder();
        message.append("BUSINESS_EVENT | type=").append(eventType);
        message.append(" | traceId=").append(traceId);
        message.append(" | userId=").append(userId != null ? userId : "anonymous");
        
        if (context != null && !context.isEmpty()) {
            context.forEach((key, value) -> 
                message.append(" | ").append(key).append("=").append(value));
        }
        
        log.info(message.toString());
    }

    /**
     * Logs a security event (authentication, authorization).
     * 
     * @param eventType the security event type (e.g., "AUTH_SUCCESS", "AUTH_FAILED", "ACCESS_DENIED")
     * @param userId the user ID involved
     * @param context additional context
     */
    public void logSecurityEvent(String eventType, String userId, Map<String, Object> context) {
        String traceId = MdcLoggingFilter.getCurrentTraceId();
        String clientIp = MDC.get(MdcLoggingFilter.CLIENT_IP);
        String endpoint = MDC.get(MdcLoggingFilter.ENDPOINT);
        
        StringBuilder message = new StringBuilder();
        message.append("SECURITY_EVENT | type=").append(eventType);
        message.append(" | traceId=").append(traceId);
        message.append(" | userId=").append(userId != null ? userId : "unknown");
        message.append(" | clientIp=").append(clientIp);
        message.append(" | endpoint=").append(endpoint);
        
        if (context != null && !context.isEmpty()) {
            context.forEach((key, value) -> 
                message.append(" | ").append(key).append("=").append(value));
        }
        
        // Security events are always logged at WARN level for visibility
        log.warn(message.toString());
    }

    /**
     * Logs an authentication failure event.
     * 
     * @param userId attempted user ID
     * @param reason failure reason
     */
    public void logAuthenticationFailure(String userId, String reason) {
        logSecurityEvent("AUTH_FAILED", userId, Map.of("reason", reason));
    }

    /**
     * Logs an authentication success event.
     * 
     * @param userId authenticated user ID
     */
    public void logAuthenticationSuccess(String userId) {
        logSecurityEvent("AUTH_SUCCESS", userId, null);
    }

    /**
     * Logs an access denied event.
     * 
     * @param userId user ID that was denied
     * @param resource the resource that was denied
     * @param action the action that was attempted
     */
    public void logAccessDenied(String userId, String resource, String action) {
        logSecurityEvent("ACCESS_DENIED", userId, Map.of("resource", resource, "action", action));
    }

    // ========================================================================
    // Performance Logging
    // ========================================================================

    /**
     * Logs a performance metric.
     * 
     * @param operation the operation being measured
     * @param durationMs duration in milliseconds
     * @param context additional context
     */
    public void logPerformance(String operation, long durationMs, Map<String, Object> context) {
        String traceId = MdcLoggingFilter.getCurrentTraceId();
        
        StringBuilder message = new StringBuilder();
        message.append("PERFORMANCE | operation=").append(operation);
        message.append(" | traceId=").append(traceId);
        message.append(" | duration=").append(durationMs).append("ms");
        
        if (context != null && !context.isEmpty()) {
            context.forEach((key, value) -> 
                message.append(" | ").append(key).append("=").append(value));
        }
        
        // Log level based on duration
        if (durationMs > 5000) {
            log.error(message.toString() + " | CRITICAL_SLOW");
        } else if (durationMs > 1000) {
            log.warn(message.toString() + " | SLOW");
        } else {
            log.debug(message.toString());
        }
    }

    // ========================================================================
    // Database/Repository Logging
    // ========================================================================

    /**
     * Logs a database operation.
     * 
     * @param operation the database operation (e.g., "SELECT", "INSERT", "UPDATE", "DELETE")
     * @param entity the entity type
     * @param entityId the entity ID (can be null for queries)
     * @param durationMs operation duration
     */
    public void logDatabaseOperation(String operation, String entity, Object entityId, long durationMs) {
        String traceId = MdcLoggingFilter.getCurrentTraceId();
        
        StringBuilder message = new StringBuilder();
        message.append("DB_OPERATION | operation=").append(operation);
        message.append(" | entity=").append(entity);
        if (entityId != null) {
            message.append(" | entityId=").append(entityId);
        }
        message.append(" | traceId=").append(traceId);
        message.append(" | duration=").append(durationMs).append("ms");
        
        log.debug(message.toString());
    }

    // ========================================================================
    // Standard Logging Methods
    // ========================================================================

    /**
     * Logs at INFO level with structured format.
     */
    public void info(String eventType, Map<String, Object> context) {
        String message = buildLogMessage(eventType, context);
        log.info(message);
    }

    /**
     * Logs at WARN level with structured format.
     */
    public void warn(String eventType, Map<String, Object> context) {
        String message = buildLogMessage(eventType, context);
        log.warn(message);
    }

    /**
     * Logs at ERROR level with structured format.
     */
    public void error(String eventType, Map<String, Object> context, Throwable ex) {
        String message = buildLogMessage(eventType, context);
        log.error(message, ex);
    }

    /**
     * Logs at DEBUG level with structured format.
     */
    public void debug(String eventType, Map<String, Object> context) {
        String message = buildLogMessage(eventType, context);
        log.debug(message);
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private String buildLogMessage(String eventType, Map<String, Object> context) {
        String traceId = MdcLoggingFilter.getCurrentTraceId();
        
        StringBuilder message = new StringBuilder();
        message.append(eventType);
        message.append(" | traceId=").append(traceId);
        
        if (context != null && !context.isEmpty()) {
            context.forEach((key, value) -> 
                message.append(" | ").append(key).append("=").append(sanitizeValue(value)));
        }
        
        return message.toString();
    }

    /**
     * Sanitizes values for safe logging (prevents log injection, truncates long values).
     */
    private String sanitizeValue(Object value) {
        if (value == null) {
            return "null";
        }
        
        String strValue = value.toString();
        
        // Remove newlines and control characters to prevent log injection
        strValue = strValue.replaceAll("[\\r\\n\\t]", " ");
        
        // Truncate very long values
        if (strValue.length() > 500) {
            strValue = strValue.substring(0, 500) + "...[truncated]";
        }
        
        return strValue;
    }
}
