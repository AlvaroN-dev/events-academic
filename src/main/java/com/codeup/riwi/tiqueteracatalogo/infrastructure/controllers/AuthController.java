package com.codeup.riwi.tiqueteracatalogo.infrastructure.controllers;

import com.codeup.riwi.tiqueteracatalogo.application.mapper.AuthMapper;
import com.codeup.riwi.tiqueteracatalogo.domain.models.AuthResult;
import com.codeup.riwi.tiqueteracatalogo.domain.models.CredencialesLogin;
import com.codeup.riwi.tiqueteracatalogo.domain.models.RegistroUsuario;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.in.AutenticarUsuarioUseCase;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.in.RefrescarTokenUseCase;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.in.RegistrarUsuarioUseCase;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth.AuthResponse;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth.LoginRequest;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador de autenticación que maneja registro y login de usuarios.
 * 
 * Este controlador sigue la arquitectura hexagonal:
 * - Recibe DTOs de infraestructura (RegisterRequest, LoginRequest)
 * - Convierte a modelos de dominio usando AuthMapper
 * - Delega a Use Cases (puertos de entrada)
 * - Convierte respuestas de dominio a DTOs
 * 
 * Endpoints:
 * - POST /auth/register - Registro de nuevos usuarios
 * - POST /auth/login - Autenticación de usuarios
 * - POST /auth/refresh - Refrescar token de acceso
 * 
 * @author TiqueteraCatalogo Team
 * @version 2.0
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints de autenticación y registro de usuarios")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final RefrescarTokenUseCase refrescarTokenUseCase;
    
    public AuthController(
            RegistrarUsuarioUseCase registrarUsuarioUseCase,
            AutenticarUsuarioUseCase autenticarUsuarioUseCase,
            RefrescarTokenUseCase refrescarTokenUseCase) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.refrescarTokenUseCase = refrescarTokenUseCase;
    }
    
    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param request Datos de registro
     * @return AuthResponse con token JWT
     */
    @PostMapping("/register")
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una nueva cuenta de usuario y devuelve tokens de autenticación"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario registrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de registro inválidos",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "El email ya está registrado",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class)
            )
        )
    })
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        
        log.info("Solicitud de registro recibida para email: {}", request.getEmail());
        
        // Convertir DTO a modelo de dominio
        RegistroUsuario registroUsuario = AuthMapper.toRegistroUsuario(request);
        
        // Ejecutar use case
        AuthResult result = registrarUsuarioUseCase.ejecutar(registroUsuario);
        
        // Convertir resultado a DTO de respuesta
        AuthResponse response = AuthMapper.toAuthResponse(result);
        
        log.info("Registro completado exitosamente para: {}", request.getEmail());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Autentica un usuario y devuelve tokens JWT.
     * 
     * @param request Credenciales de login
     * @return AuthResponse con token JWT
     */
    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica al usuario con username/email y contraseña, devuelve tokens JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de login inválidos",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales incorrectas",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class)
            )
        )
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        
        log.info("Solicitud de login recibida para: {}", request.getUsernameOrEmail());
        
        // Convertir DTO a modelo de dominio
        CredencialesLogin credenciales = AuthMapper.toCredencialesLogin(request);
        
        // Ejecutar use case
        AuthResult result = autenticarUsuarioUseCase.ejecutar(credenciales);
        
        // Convertir resultado a DTO de respuesta
        AuthResponse response = AuthMapper.toAuthResponse(result);
        
        log.info("Login completado exitosamente para: {}", request.getUsernameOrEmail());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Refresca el token de acceso usando un refresh token válido.
     * 
     * @param refreshToken Token de refresco
     * @return AuthResponse con nuevos tokens
     */
    @PostMapping("/refresh")
    @Operation(
        summary = "Refrescar token de acceso",
        description = "Genera un nuevo token de acceso usando el refresh token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token refrescado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Refresh token inválido o expirado",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class)
            )
        )
    })
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody Map<String, String> request) {
        
        String refreshToken = request.get("refreshToken");
        
        if (refreshToken == null || refreshToken.isBlank()) {
            log.warn("Solicitud de refresh sin token");
            return ResponseEntity.badRequest().build();
        }
        
        log.debug("Solicitud de refresh token recibida");
        
        // Ejecutar use case
        AuthResult result = refrescarTokenUseCase.ejecutar(refreshToken);
        
        // Convertir resultado a DTO de respuesta
        AuthResponse response = AuthMapper.toAuthResponse(result);
        
        log.debug("Token refrescado exitosamente");
        
        return ResponseEntity.ok(response);
    }
}
