package com.codeup.riwi.tiqueteracatalogo.infrastructure.config.security;

import com.codeup.riwi.tiqueteracatalogo.infrastructure.config.logging.LoggingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * Filtro de autenticación JWT que intercepta cada request HTTP.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final LoggingService loggingService;
    
    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            LoggingService loggingService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.loggingService = loggingService;
    }
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        final String requestUri = request.getRequestURI();
        
        // Si no hay header de autorización o no empieza con Bearer, continuar sin autenticar
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            final String jwt = authHeader.substring(BEARER_PREFIX.length());
            final String username = jwtService.extractUsername(jwt);
            
            // Si extraemos el username y no hay autenticación previa en el contexto
            if (StringUtils.hasText(username) && 
                    SecurityContextHolder.getContext().getAuthentication() == null) {
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Validar el token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Crear token de autenticación
                    UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    
                    // Agregar detalles de la request
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Establecer en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    // Agregar información del usuario al MDC para logging
                    MDC.put("userId", username);
                    MDC.put("userRoles", userDetails.getAuthorities().toString());
                    
                    log.debug("Usuario autenticado exitosamente: {} para URI: {}", 
                            username, requestUri);
                } else {
                    // Token inválido
                    loggingService.logSecurityEvent("JWT_VALIDATION_FAILED", username,
                            Map.of("uri", requestUri, "clientIp", request.getRemoteAddr()));
                }
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("Token JWT expirado para request: {}", requestUri);
            loggingService.logSecurityEvent("JWT_EXPIRED", "unknown",
                    Map.of("uri", requestUri, "clientIp", request.getRemoteAddr()));
            
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("Firma de JWT inválida detectada para request: {}", requestUri);
            loggingService.logSecurityEvent("JWT_SIGNATURE_INVALID", "unknown",
                    Map.of("uri", requestUri, "clientIp", request.getRemoteAddr()));
            
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.warn("Token JWT malformado para request: {}", requestUri);
            loggingService.logSecurityEvent("JWT_MALFORMED", "unknown",
                    Map.of("uri", requestUri, "clientIp", request.getRemoteAddr()));
            
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.warn("Token JWT no soportado para request: {}", requestUri);
            loggingService.logSecurityEvent("JWT_UNSUPPORTED", "unknown",
                    Map.of("uri", requestUri, "clientIp", request.getRemoteAddr()));
            
        } catch (Exception e) {
            log.error("Error procesando token JWT para request: {}", requestUri, e);
            loggingService.logSecurityEvent("JWT_PROCESSING_ERROR", "unknown",
                    Map.of("uri", requestUri, "error", e.getMessage()));
        }
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        return path.startsWith("/auth/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/actuator/health") ||
               path.equals("/error");
    }
}
