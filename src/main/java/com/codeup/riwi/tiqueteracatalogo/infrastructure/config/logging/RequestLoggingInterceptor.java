package com.codeup.riwi.tiqueteracatalogo.infrastructure.config.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.Instant;

/**
 * HTTP Request Logging Interceptor.
 * Provides detailed logging for all HTTP requests including timing information.
 * 
 * <p>Logs the following events:</p>
 * <ul>
 *   <li>Request start with method, URI, and client information</li>
 *   <li>Request completion with status code and duration</li>
 *   <li>Slow request warnings (configurable threshold)</li>
 * </ul>
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    
    private static final String START_TIME_ATTR = "requestStartTime";
    private static final long SLOW_REQUEST_THRESHOLD_MS = 1000; // 1 second

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Store start time for duration calculation
        request.setAttribute(START_TIME_ATTR, Instant.now());
        
        // Log request entry
        String traceId = MDC.get(MdcLoggingFilter.TRACE_ID);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIp = MDC.get(MdcLoggingFilter.CLIENT_IP);
        
        String fullPath = queryString != null ? uri + "?" + queryString : uri;
        
        log.info("HTTP_REQUEST_START | traceId={} | method={} | path={} | clientIp={}",
            traceId, method, fullPath, clientIp);
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                          Object handler, ModelAndView modelAndView) {
        // Post-processing after the handler but before view rendering
        // Not used in REST API context
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        
        // Calculate request duration
        Instant startTime = (Instant) request.getAttribute(START_TIME_ATTR);
        long durationMs = startTime != null 
            ? Duration.between(startTime, Instant.now()).toMillis() 
            : -1;
        
        String traceId = MDC.get(MdcLoggingFilter.TRACE_ID);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();
        
        // Determine log level based on status and duration
        if (ex != null) {
            log.error("HTTP_REQUEST_ERROR | traceId={} | method={} | path={} | status={} | duration={}ms | error={}",
                traceId, method, uri, status, durationMs, ex.getMessage());
        } else if (status >= 500) {
            log.error("HTTP_REQUEST_COMPLETE | traceId={} | method={} | path={} | status={} | duration={}ms | SERVER_ERROR",
                traceId, method, uri, status, durationMs);
        } else if (status >= 400) {
            log.warn("HTTP_REQUEST_COMPLETE | traceId={} | method={} | path={} | status={} | duration={}ms | CLIENT_ERROR",
                traceId, method, uri, status, durationMs);
        } else if (durationMs > SLOW_REQUEST_THRESHOLD_MS) {
            log.warn("HTTP_REQUEST_COMPLETE | traceId={} | method={} | path={} | status={} | duration={}ms | SLOW_REQUEST",
                traceId, method, uri, status, durationMs);
        } else {
            log.info("HTTP_REQUEST_COMPLETE | traceId={} | method={} | path={} | status={} | duration={}ms",
                traceId, method, uri, status, durationMs);
        }
    }
}
