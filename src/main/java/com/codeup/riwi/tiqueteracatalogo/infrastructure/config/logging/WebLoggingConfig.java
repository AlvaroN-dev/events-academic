package com.codeup.riwi.tiqueteracatalogo.infrastructure.config.logging;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for logging interceptors.
 * Registers the RequestLoggingInterceptor for all API endpoints.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Configuration
public class WebLoggingConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor requestLoggingInterceptor;

    public WebLoggingConfig(RequestLoggingInterceptor requestLoggingInterceptor) {
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
            .addPathPatterns("/api/**")      // All API endpoints
            .excludePathPatterns(
                "/api/docs/**",              // Swagger/OpenAPI docs
                "/swagger-ui/**",            // Swagger UI resources
                "/v3/api-docs/**",           // OpenAPI spec
                "/actuator/**"               // Actuator endpoints (if enabled)
            );
    }
}
