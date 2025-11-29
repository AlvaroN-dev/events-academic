package com.codeup.riwi.tiqueteracatalogo.infrastructure.config.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aspect for logging REST controller operations.
 * Provides comprehensive logging for all controller endpoints including:
 * <ul>
 *   <li>Request entry with method, endpoint, and parameters</li>
 *   <li>Response exit with status code and duration</li>
 *   <li>Exception logging with full context</li>
 *   <li>Performance metrics</li>
 * </ul>
 * 
 * <p>All logs include the traceId from MDC for request correlation.</p>
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    
    // Performance thresholds (milliseconds)
    private static final long WARN_THRESHOLD_MS = 1000;  // Log WARN if request takes > 1 second
    private static final long ERROR_THRESHOLD_MS = 5000; // Log ERROR if request takes > 5 seconds

    private final ObjectMapper objectMapper;

    public LoggingAspect() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    // ========================================================================
    // Pointcut Definitions
    // ========================================================================

    /**
     * Pointcut for all REST controller methods.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {}

    /**
     * Pointcut for all @RestControllerAdvice methods (exception handlers).
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestControllerAdvice *)")
    public void restControllerAdviceMethods() {}

    /**
     * Pointcut for all public methods in controllers package.
     */
    @Pointcut("execution(public * com.codeup.riwi.tiqueteracatalogo.infrastructure.controllers..*.*(..))")
    public void controllerPackageMethods() {}

    /**
     * Pointcut for service layer methods.
     */
    @Pointcut("execution(public * com.codeup.riwi.tiqueteracatalogo.application.services..*.*(..))")
    public void serviceMethods() {}

    /**
     * Pointcut for repository layer methods.
     */
    @Pointcut("execution(public * com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories..*.*(..))")
    public void repositoryMethods() {}

    // ========================================================================
    // Controller Logging
    // ========================================================================

    /**
     * Logs controller method execution with timing and result.
     */
    @Around("restControllerMethods() && controllerPackageMethods()")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant startTime = Instant.now();
        String traceId = MDC.get(MdcLoggingFilter.TRACE_ID);
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        
        // Extract HTTP method from annotations
        String httpMethod = extractHttpMethod(method);
        String endpoint = extractEndpoint();
        
        // Log request entry
        logRequestEntry(className, methodName, httpMethod, endpoint, joinPoint.getArgs(), signature.getParameterNames());
        
        try {
            // Execute the method
            Object result = joinPoint.proceed();
            
            // Calculate duration
            long durationMs = Duration.between(startTime, Instant.now()).toMillis();
            
            // Log successful response
            logResponseExit(className, methodName, httpMethod, endpoint, result, durationMs, traceId);
            
            return result;
            
        } catch (Throwable ex) {
            // Calculate duration even on error
            long durationMs = Duration.between(startTime, Instant.now()).toMillis();
            
            // Log exception (will be handled by GlobalExceptionHandler)
            logControllerException(className, methodName, httpMethod, endpoint, ex, durationMs, traceId);
            
            throw ex;
        }
    }

    // ========================================================================
    // Service Layer Logging
    // ========================================================================

    /**
     * Logs service method execution for debugging and performance monitoring.
     */
    @Around("serviceMethods()")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant startTime = Instant.now();
        
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        log.debug("SERVICE_ENTRY | class={} | method={}", className, methodName);
        
        try {
            Object result = joinPoint.proceed();
            
            long durationMs = Duration.between(startTime, Instant.now()).toMillis();
            
            if (durationMs > WARN_THRESHOLD_MS) {
                log.warn("SERVICE_SLOW | class={} | method={} | duration={}ms", 
                    className, methodName, durationMs);
            } else {
                log.debug("SERVICE_EXIT | class={} | method={} | duration={}ms", 
                    className, methodName, durationMs);
            }
            
            return result;
            
        } catch (Throwable ex) {
            long durationMs = Duration.between(startTime, Instant.now()).toMillis();
            log.debug("SERVICE_ERROR | class={} | method={} | duration={}ms | error={}", 
                className, methodName, durationMs, ex.getMessage());
            throw ex;
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    /**
     * Logs the entry point of a controller method.
     */
    private void logRequestEntry(String className, String methodName, String httpMethod, 
                                  String endpoint, Object[] args, String[] paramNames) {
        
        Map<String, Object> params = buildParamsMap(args, paramNames);
        String paramsJson = safeToJson(params);
        
        log.info("REQUEST_RECEIVED | controller={} | method={} | httpMethod={} | endpoint={} | params={}",
            className, methodName, httpMethod, endpoint, paramsJson);
    }

    /**
     * Logs the exit point of a controller method with response info.
     */
    private void logResponseExit(String className, String methodName, String httpMethod, 
                                  String endpoint, Object result, long durationMs, String traceId) {
        
        int statusCode = extractStatusCode(result);
        String responseType = result != null ? result.getClass().getSimpleName() : "void";
        
        // Choose log level based on duration
        if (durationMs > ERROR_THRESHOLD_MS) {
            log.error("REQUEST_COMPLETED | controller={} | method={} | httpMethod={} | endpoint={} | status={} | duration={}ms | traceId={} | VERY_SLOW",
                className, methodName, httpMethod, endpoint, statusCode, durationMs, traceId);
        } else if (durationMs > WARN_THRESHOLD_MS) {
            log.warn("REQUEST_COMPLETED | controller={} | method={} | httpMethod={} | endpoint={} | status={} | duration={}ms | traceId={} | SLOW",
                className, methodName, httpMethod, endpoint, statusCode, durationMs, traceId);
        } else {
            log.info("REQUEST_COMPLETED | controller={} | method={} | httpMethod={} | endpoint={} | status={} | duration={}ms | responseType={}",
                className, methodName, httpMethod, endpoint, statusCode, durationMs, responseType);
        }
    }

    /**
     * Logs controller exceptions.
     */
    private void logControllerException(String className, String methodName, String httpMethod, 
                                         String endpoint, Throwable ex, long durationMs, String traceId) {
        
        String exceptionType = ex.getClass().getSimpleName();
        String exceptionMessage = ex.getMessage();
        
        log.error("REQUEST_FAILED | controller={} | method={} | httpMethod={} | endpoint={} | duration={}ms | traceId={} | exception={} | message={}",
            className, methodName, httpMethod, endpoint, durationMs, traceId, exceptionType, exceptionMessage);
    }

    /**
     * Extracts HTTP method from Spring annotations.
     */
    private String extractHttpMethod(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) return "GET";
        if (method.isAnnotationPresent(PostMapping.class)) return "POST";
        if (method.isAnnotationPresent(PutMapping.class)) return "PUT";
        if (method.isAnnotationPresent(DeleteMapping.class)) return "DELETE";
        if (method.isAnnotationPresent(PatchMapping.class)) return "PATCH";
        
        RequestMapping rm = method.getAnnotation(RequestMapping.class);
        if (rm != null && rm.method().length > 0) {
            return Arrays.stream(rm.method())
                .map(Enum::name)
                .collect(Collectors.joining(","));
        }
        
        return "UNKNOWN";
    }

    /**
     * Extracts the current endpoint from request context.
     */
    private String extractEndpoint() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getRequestURI();
            }
        } catch (Exception e) {
            log.trace("Could not extract endpoint from request context", e);
        }
        return MDC.get(MdcLoggingFilter.ENDPOINT);
    }

    /**
     * Extracts status code from ResponseEntity or defaults to 200.
     */
    private int extractStatusCode(Object result) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getStatusCode().value();
        }
        return 200; // Default for @ResponseBody methods
    }

    /**
     * Builds a map of parameter names to values.
     */
    private Map<String, Object> buildParamsMap(Object[] args, String[] paramNames) {
        Map<String, Object> params = new HashMap<>();
        
        if (args == null || paramNames == null) {
            return params;
        }
        
        for (int i = 0; i < Math.min(args.length, paramNames.length); i++) {
            Object arg = args[i];
            String paramName = paramNames[i];
            
            // Skip sensitive or complex objects
            if (arg == null) {
                params.put(paramName, null);
            } else if (isSensitiveParam(paramName)) {
                params.put(paramName, "[REDACTED]");
            } else if (isSimpleType(arg)) {
                params.put(paramName, arg);
            } else if (isHttpServletType(arg)) {
                params.put(paramName, "[" + arg.getClass().getSimpleName() + "]");
            } else {
                // For complex objects, just log the type
                params.put(paramName, "[" + arg.getClass().getSimpleName() + "]");
            }
        }
        
        return params;
    }

    /**
     * Checks if a parameter name indicates sensitive data.
     */
    private boolean isSensitiveParam(String paramName) {
        String lower = paramName.toLowerCase();
        return lower.contains("password") 
            || lower.contains("secret") 
            || lower.contains("token")
            || lower.contains("credential")
            || lower.contains("auth");
    }

    /**
     * Checks if an object is a simple type that can be safely logged.
     */
    private boolean isSimpleType(Object obj) {
        return obj instanceof String 
            || obj instanceof Number 
            || obj instanceof Boolean
            || obj instanceof Enum;
    }

    /**
     * Checks if an object is an HTTP servlet type (should not be serialized).
     */
    private boolean isHttpServletType(Object obj) {
        String className = obj.getClass().getName();
        return className.startsWith("jakarta.servlet") 
            || className.startsWith("org.springframework.web");
    }

    /**
     * Safely converts an object to JSON string.
     */
    private String safeToJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }
}
