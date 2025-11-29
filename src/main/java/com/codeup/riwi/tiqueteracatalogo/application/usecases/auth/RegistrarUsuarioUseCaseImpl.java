package com.codeup.riwi.tiqueteracatalogo.application.usecases.auth;

import com.codeup.riwi.tiqueteracatalogo.domain.models.AuthResult;
import com.codeup.riwi.tiqueteracatalogo.domain.models.RegistroUsuario;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.in.RegistrarUsuarioUseCase;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.JwtPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.PasswordEncoderPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.UserRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.UserRepositoryPort.UserDomainData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Implementación del caso de uso de registro de usuario.
 * Contiene la lógica de negocio para registrar nuevos usuarios.
 * 
 * Este caso de uso sigue los principios SOLID:
 * - SRP: Solo se encarga del registro de usuarios
 * - OCP: Extensible a través de puertos
 * - LSP: Implementa la interfaz del puerto de entrada
 * - ISP: Interfaces segregadas por funcionalidad
 * - DIP: Depende de abstracciones (puertos), no de implementaciones
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(RegistrarUsuarioUseCaseImpl.class);
    
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtPort jwtPort;
    
    public RegistrarUsuarioUseCaseImpl(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            JwtPort jwtPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtPort = jwtPort;
    }
    
    @Override
    @Transactional
    public AuthResult ejecutar(RegistroUsuario registro) {
        log.info("Procesando registro para email: {}", registro.getEmail());
        
        // Regla de negocio: El email no debe existir
        if (userRepository.existsByEmail(registro.getEmail())) {
            log.warn("Intento de registro con email existente: {}", registro.getEmail());
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        // Generar username único basado en el email
        String username = generarUsernameUnico(registro.getEmail());
        
        // Obtener rol por defecto
        String defaultRole = userRepository.getDefaultRoleName();
        
        // Crear datos del usuario
        UserDomainData userData = UserDomainData.builder()
                .username(username)
                .firstName(registro.getFirstName())
                .lastName(registro.getLastName())
                .email(registro.getEmail())
                .password(passwordEncoder.encode(registro.getPassword()))
                .roles(Set.of(defaultRole))
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .build();
        
        // Guardar usuario
        UserDomainData savedUser = userRepository.save(userData);
        
        log.info("Usuario registrado exitosamente: {} (ID: {})", savedUser.email(), savedUser.id());
        
        // Generar token JWT
        String accessToken = jwtPort.generateToken(savedUser);
        
        return AuthResult.success(
                accessToken,
                jwtPort.getExpirationInSeconds(),
                savedUser.id(),
                savedUser.username(),
                savedUser.email(),
                savedUser.roles()
        );
    }
    
    /**
     * Genera un username único basado en el email.
     * Si ya existe, añade un sufijo numérico.
     */
    private String generarUsernameUnico(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername + "_" + (System.currentTimeMillis() % 10000);
        
        // Si existe, generar otro
        if (userRepository.existsByUsername(username)) {
            username = baseUsername + "_" + System.currentTimeMillis();
        }
        
        return username;
    }
}
