package com.codeup.riwi.tiqueteracatalogo.infrastructure.config;

import com.codeup.riwi.tiqueteracatalogo.application.usecases.auth.AutenticarUsuarioUseCaseImpl;
import com.codeup.riwi.tiqueteracatalogo.application.usecases.auth.RefrescarTokenUseCaseImpl;
import com.codeup.riwi.tiqueteracatalogo.application.usecases.auth.RegistrarUsuarioUseCaseImpl;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.in.AutenticarUsuarioUseCase;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.in.RefrescarTokenUseCase;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.in.RegistrarUsuarioUseCase;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.JwtPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.PasswordEncoderPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de beans para los casos de uso de autenticación.
 * Esta clase sigue el patrón de configuración de Spring para inyección de dependencias.
 * 
 * Los use cases se configuran aquí porque:
 * 1. No tienen anotación @Service/@Component (pertenecen a la capa de aplicación)
 * 2. Dependen de puertos que son implementados por adaptadores de infraestructura
 * 3. Mantiene la separación entre capas de la arquitectura hexagonal
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Configuration
public class AuthUseCasesConfig {
    
    /**
     * Bean para el caso de uso de registro de usuario.
     */
    @Bean
    public RegistrarUsuarioUseCase registrarUsuarioUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            JwtPort jwtPort) {
        return new RegistrarUsuarioUseCaseImpl(userRepositoryPort, passwordEncoderPort, jwtPort);
    }
    
    /**
     * Bean para el caso de uso de autenticación.
     */
    @Bean
    public AutenticarUsuarioUseCase autenticarUsuarioUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            JwtPort jwtPort) {
        return new AutenticarUsuarioUseCaseImpl(userRepositoryPort, passwordEncoderPort, jwtPort);
    }
    
    /**
     * Bean para el caso de uso de refrescar token.
     */
    @Bean
    public RefrescarTokenUseCase refrescarTokenUseCase(
            UserRepositoryPort userRepositoryPort,
            JwtPort jwtPort) {
        return new RefrescarTokenUseCaseImpl(userRepositoryPort, jwtPort);
    }
}
