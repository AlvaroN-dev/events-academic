package com.codeup.riwi.tiqueteracatalogo.infrastructure.config.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that populates MDC (Mapped Diagnostic Context) with request-scoped values.
 * These values are automatically included in all log messages during the request lifecycle.
 * 
 * <p>MDC Keys populated:</p>
 * <ul>
 *   <li><b>traceId</b> - Unique identifier for the request (UUID)</li>
 *   <li><b>requestId</b> - Same as traceId (alias for compatibility)</li>
 *   <li><b>endpoint</b> - The requested URI path</li>
 *   <li><b>method</b> - HTTP method (GET, POST, PUT, DELETE, etc.)</li>
 *   <li><b>clientIp</b> - Client IP address</li>
 *   <li><b>userAgent</b> - Client User-Agent header</li>
 *   <li><b>userId</b> - Authenticated user ID (when available)</li>
 * </ul>
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcLoggingFilter extends OncePerRequestFilter {

    // MDC Keys
    public static final String TRACE_ID = "traceId";
    public static final String REQUEST_ID = "requestId";
    public static final String ENDPOINT = "endpoint";
    public static final String METHOD = "method";
    public static final String CLIENT_IP = "clientIp";
    public static final String USER_AGENT = "userAgent";
    public static final String USER_ID = "userId";
    public static final String SESSION_ID = "sessionId";

    // HTTP Headers
    private static final String X_REQUEST_ID = "X-Request-ID";
    private static final String X_TRACE_ID = "X-Trace-ID";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String X_REAL_IP = "X-Real-IP";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // Generate or extract trace ID
            String traceId = extractOrGenerateTraceId(request);
            
            // Populate MDC with request context
            populateMdc(request, traceId);

            // Add trace ID to response headers for client correlation
            response.setHeader(X_TRACE_ID, traceId);
            response.setHeader(X_REQUEST_ID, traceId);

            // Continue with the filter chain
            filterChain.doFilter(request, response);

        } finally {
            // Always clear MDC to prevent memory leaks and context pollution
            clearMdc();
        }
    }

    /**
     * Extracts trace ID from request headers or generates a new UUID.
     * Supports X-Request-ID and X-Trace-ID headers for distributed tracing.
     */
    private String extractOrGenerateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(X_REQUEST_ID);
        
        if (traceId == null || traceId.isBlank()) {
            traceId = request.getHeader(X_TRACE_ID);
        }
        
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        
        return traceId;
    }

    /**
     * Populates MDC with all relevant request context values.
     */
    private void populateMdc(HttpServletRequest request, String traceId) {
        // Trace identifiers
        MDC.put(TRACE_ID, traceId);
        MDC.put(REQUEST_ID, traceId);

        // Request information
        MDC.put(ENDPOINT, request.getRequestURI());
        MDC.put(METHOD, request.getMethod());

        // Client information
        MDC.put(CLIENT_IP, extractClientIp(request));
        
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && !userAgent.isBlank()) {
            // Truncate long user agents
            MDC.put(USER_AGENT, userAgent.length() > 100 ? userAgent.substring(0, 100) + "..." : userAgent);
        }

        // Session information (if available)
        if (request.getSession(false) != null) {
            MDC.put(SESSION_ID, request.getSession().getId());
        }

        // User information - placeholder for future authentication integration
        // When Spring Security is configured, extract user from SecurityContext
        String userId = extractUserId(request);
        if (userId != null) {
            MDC.put(USER_ID, userId);
        }
    }

    /**
     * Extracts the real client IP address, considering proxy headers.
     */
    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader(X_FORWARDED_FOR);
        
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return ip.split(",")[0].trim();
        }
        
        ip = request.getHeader(X_REAL_IP);
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Extracts user ID from the request.
     * This is a placeholder for future Spring Security integration.
     * 
     * TODO: When Spring Security is enabled, extract from SecurityContextHolder:
     * <pre>
     * Authentication auth = SecurityContextHolder.getContext().getAuthentication();
     * if (auth != null && auth.isAuthenticated()) {
     *     return auth.getName();
     * }
     * </pre>
     */
    private String extractUserId(HttpServletRequest request) {
        // Check for custom user header (useful for development/testing)
        String userId = request.getHeader("X-User-ID");
        if (userId != null && !userId.isBlank()) {
            return userId;
        }
        
        // Placeholder: return "anonymous" for unauthenticated requests
        return "anonymous";
    }

    /**
     * Clears all MDC values to prevent context leaking between requests.
     */
    private void clearMdc() {
        MDC.remove(TRACE_ID);
        MDC.remove(REQUEST_ID);
        MDC.remove(ENDPOINT);
        MDC.remove(METHOD);
        MDC.remove(CLIENT_IP);
        MDC.remove(USER_AGENT);
        MDC.remove(USER_ID);
        MDC.remove(SESSION_ID);
    }

    /**
     * Utility method to get current trace ID from MDC.
     * Useful for other components that need to access the trace ID.
     */
    public static String getCurrentTraceId() {
        String traceId = MDC.get(TRACE_ID);
        return traceId != null ? traceId : UUID.randomUUID().toString();
    }

    /**
     * Utility method to set a custom user ID in MDC.
     * Call this after authentication to update the user context.
     */
    public static void setUserId(String userId) {
        if (userId != null && !userId.isBlank()) {
            MDC.put(USER_ID, userId);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip MDC for static resources
        return path.startsWith("/static/") 
            || path.startsWith("/css/")
            || path.startsWith("/js/")
            || path.startsWith("/images/")
            || path.endsWith(".ico");
    }
}
