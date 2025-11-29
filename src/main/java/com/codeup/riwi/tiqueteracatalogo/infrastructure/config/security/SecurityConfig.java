package com.codeup.riwi.tiqueteracatalogo.infrastructure.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de seguridad de Spring Security.
 * 
 * Características:
 * - Autenticación JWT stateless
 * - CORS configurado para desarrollo y producción
 * - Endpoints públicos y protegidos
 * - Seguridad a nivel de método con @PreAuthorize
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    
    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthFilter,
            UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }
    
    /**
     * Configura la cadena de filtros de seguridad.
     * 
     * - Deshabilita CSRF (no necesario para APIs stateless)
     * - Configura CORS
     * - Define endpoints públicos y protegidos
     * - Agrega filtro JWT antes del filtro de autenticación estándar
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF - no necesario para APIs REST stateless
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configurar autorización de requests
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos - autenticación
                .requestMatchers("/auth/**").permitAll()
                
                // Swagger/OpenAPI - públicos
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                
                // Actuator health - público
                .requestMatchers("/actuator/health").permitAll()
                
                // Error endpoint - público
                .requestMatchers("/error").permitAll()
                
                // GET de eventos y venues - públicos (lectura sin autenticación)
                .requestMatchers(HttpMethod.GET, "/api/v1/events/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/venues/**").permitAll()
                
                // Operaciones de escritura requieren autenticación
                .requestMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/v1/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/**").authenticated()
                
                // Cualquier otra request requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Configurar manejo de sesiones como stateless
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configurar proveedor de autenticación
            .authenticationProvider(authenticationProvider())
            
            // Agregar filtro JWT antes del filtro de autenticación
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * Configura CORS para permitir requests desde diferentes orígenes.
     * En producción, esto debería ser más restrictivo.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Orígenes permitidos - en producción usar valores específicos
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",    // React dev
            "http://localhost:4200",    // Angular dev
            "http://localhost:5173",    // Vite dev
            "http://localhost:8080"     // Mismo origen
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "X-Trace-Id"
        ));
        
        // Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "X-Trace-Id"
        ));
        
        // Permitir credenciales (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Tiempo de caché para preflight requests (1 hora)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * Proveedor de autenticación usando DAO con password encoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    /**
     * Password encoder usando BCrypt.
     * BCrypt es recomendado para hashing de contraseñas por su resistencia a ataques.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Authentication manager para uso en servicios.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
