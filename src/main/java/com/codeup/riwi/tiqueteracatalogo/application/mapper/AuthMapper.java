package com.codeup.riwi.tiqueteracatalogo.application.mapper;

import com.codeup.riwi.tiqueteracatalogo.domain.models.AuthResult;
import com.codeup.riwi.tiqueteracatalogo.domain.models.CredencialesLogin;
import com.codeup.riwi.tiqueteracatalogo.domain.models.RegistroUsuario;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth.AuthResponse;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth.LoginRequest;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth.RegisterRequest;

/**
 * Mapper para convertir entre DTOs de infraestructura y modelos de dominio de autenticación.
 * Sigue el patrón de arquitectura hexagonal manteniendo la separación de capas.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public final class AuthMapper {
    
    private AuthMapper() {
        // Utility class - no instantiation
    }
    
    /**
     * Convierte RegisterRequest (DTO) a RegistroUsuario (modelo de dominio).
     */
    public static RegistroUsuario toRegistroUsuario(RegisterRequest request) {
        return RegistroUsuario.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }
    
    /**
     * Convierte LoginRequest (DTO) a CredencialesLogin (modelo de dominio).
     */
    public static CredencialesLogin toCredencialesLogin(LoginRequest request) {
        return CredencialesLogin.builder()
                .usernameOrEmail(request.getUsernameOrEmail())
                .password(request.getPassword())
                .build();
    }
    
    /**
     * Convierte AuthResult (modelo de dominio) a AuthResponse (DTO).
     */
    public static AuthResponse toAuthResponse(AuthResult result) {
        return AuthResponse.success(
                result.getAccessToken(),
                result.getExpiresIn(),
                result.getUserId(),
                result.getUsername(),
                result.getEmail(),
                result.getRoles()
        );
    }
}
