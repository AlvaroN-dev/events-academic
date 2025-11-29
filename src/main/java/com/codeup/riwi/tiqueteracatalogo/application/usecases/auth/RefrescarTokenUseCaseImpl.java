package com.codeup.riwi.tiqueteracatalogo.application.usecases.auth;

import com.codeup.riwi.tiqueteracatalogo.domain.excepcion.RecursoNoEncontradoException;
import com.codeup.riwi.tiqueteracatalogo.domain.models.AuthResult;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.in.RefrescarTokenUseCase;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.JwtPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.UserRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.UserRepositoryPort.UserDomainData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del caso de uso de refrescar token.
 * Contiene la lógica de negocio para renovar tokens JWT.
 * 
 * Este caso de uso sigue los principios SOLID:
 * - SRP: Solo se encarga del refresco de tokens
 * - OCP: Extensible a través de puertos
 * - LSP: Implementa la interfaz del puerto de entrada
 * - ISP: Interfaces segregadas por funcionalidad
 * - DIP: Depende de abstracciones (puertos), no de implementaciones
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public class RefrescarTokenUseCaseImpl implements RefrescarTokenUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(RefrescarTokenUseCaseImpl.class);
    
    private final UserRepositoryPort userRepository;
    private final JwtPort jwtPort;
    
    public RefrescarTokenUseCaseImpl(
            UserRepositoryPort userRepository,
            JwtPort jwtPort) {
        this.userRepository = userRepository;
        this.jwtPort = jwtPort;
    }
    
    @Override
    @Transactional(readOnly = true)
    public AuthResult ejecutar(String refreshToken) {
        log.debug("Procesando refresh token");
        
        // Extraer username del token
        String username = jwtPort.extractUsername(refreshToken);
        
        // Buscar usuario
        UserDomainData user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        
        // Validar token
        if (!jwtPort.isTokenValid(refreshToken, user)) {
            log.warn("Refresh token inválido para: {}", username);
            throw new BadCredentialsException("Refresh token inválido o expirado");
        }
        
        log.info("Token refrescado exitosamente para: {}", username);
        
        // Generar nuevo token
        String newAccessToken = jwtPort.generateToken(user);
        
        return AuthResult.success(
                newAccessToken,
                jwtPort.getExpirationInSeconds(),
                user.id(),
                user.username(),
                user.email(),
                user.roles()
        );
    }
}
