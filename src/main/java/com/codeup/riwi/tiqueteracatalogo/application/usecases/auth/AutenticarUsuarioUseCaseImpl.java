package com.codeup.riwi.tiqueteracatalogo.application.usecases.auth;

import com.codeup.riwi.tiqueteracatalogo.domain.models.AuthResult;
import com.codeup.riwi.tiqueteracatalogo.domain.models.CredencialesLogin;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.in.AutenticarUsuarioUseCase;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.JwtPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.PasswordEncoderPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.UserRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.UserRepositoryPort.UserDomainData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del caso de uso de autenticación de usuario.
 * Contiene la lógica de negocio para autenticar usuarios.
 * 
 * Este caso de uso sigue los principios SOLID:
 * - SRP: Solo se encarga de la autenticación
 * - OCP: Extensible a través de puertos
 * - LSP: Implementa la interfaz del puerto de entrada
 * - ISP: Interfaces segregadas por funcionalidad
 * - DIP: Depende de abstracciones (puertos), no de implementaciones
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public class AutenticarUsuarioUseCaseImpl implements AutenticarUsuarioUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(AutenticarUsuarioUseCaseImpl.class);
    
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtPort jwtPort;
    
    public AutenticarUsuarioUseCaseImpl(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            JwtPort jwtPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtPort = jwtPort;
    }
    
    @Override
    @Transactional(readOnly = true)
    public AuthResult ejecutar(CredencialesLogin credenciales) {
        log.info("Procesando login para: {}", credenciales.getUsernameOrEmail());
        
        // Buscar usuario por username o email
        UserDomainData user = userRepository.findByUsernameOrEmail(credenciales.getUsernameOrEmail())
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", credenciales.getUsernameOrEmail());
                    return new BadCredentialsException("Usuario o contraseña incorrectos");
                });
        
        // Validar estado de la cuenta
        validarEstadoCuenta(user);
        
        // Validar contraseña
        if (!passwordEncoder.matches(credenciales.getPassword(), user.password())) {
            log.warn("Contraseña incorrecta para: {}", credenciales.getUsernameOrEmail());
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }
        
        // Actualizar último login
        userRepository.updateLastLogin(user.id());
        
        log.info("Login exitoso para usuario: {}", user.username());
        
        // Generar token JWT
        String accessToken = jwtPort.generateToken(user);
        
        return AuthResult.success(
                accessToken,
                jwtPort.getExpirationInSeconds(),
                user.id(),
                user.username(),
                user.email(),
                user.roles()
        );
    }
    
    /**
     * Valida el estado de la cuenta del usuario.
     * Lanza excepciones específicas según el estado.
     */
    private void validarEstadoCuenta(UserDomainData user) {
        if (!user.enabled()) {
            log.warn("Cuenta deshabilitada para: {}", user.username());
            throw new DisabledException("La cuenta está deshabilitada");
        }
        
        if (!user.accountNonLocked()) {
            log.warn("Cuenta bloqueada para: {}", user.username());
            throw new LockedException("La cuenta está bloqueada");
        }
        
        if (!user.accountNonExpired()) {
            log.warn("Cuenta expirada para: {}", user.username());
            throw new DisabledException("La cuenta ha expirado");
        }
        
        if (!user.credentialsNonExpired()) {
            log.warn("Credenciales expiradas para: {}", user.username());
            throw new BadCredentialsException("Las credenciales han expirado");
        }
    }
}
