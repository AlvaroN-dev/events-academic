package com.codeup.riwi.tiqueteracatalogo.infrastructure.config.security;

import com.codeup.riwi.tiqueteracatalogo.domain.excepcion.RecursoNoEncontradoException;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.config.logging.LoggingService;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth.AuthResponse;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth.LoginRequest;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.auth.RegisterRequest;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.RoleEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.UserEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.RoleRepository;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio de autenticación que maneja el registro y login de usuarios.
 */
@Service
public class AuthenticationService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoggingService loggingService;
    
    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            LoggingService loggingService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.loggingService = loggingService;
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Procesando registro para email: {}", request.getEmail());
        
        // Verificar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            loggingService.logSecurityEvent("REGISTRATION_EMAIL_EXISTS", request.getEmail(),
                    Map.of("email", request.getEmail()));
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        // Generar username desde email si no se proporciona
        String username = request.getEmail().split("@")[0] + "_" + System.currentTimeMillis() % 10000;
        
        // Verificar si el username ya existe
        if (userRepository.existsByUsername(username)) {
            username = username + "_" + System.currentTimeMillis() % 1000;
        }
        
        // Obtener o crear el rol USER por defecto
        RoleEntity userRole = roleRepository.findByName(RoleEntity.RoleName.ROLE_USER)
                .orElseGet(() -> {
                    log.info("Creando rol ROLE_USER por defecto");
                    RoleEntity newRole = new RoleEntity();
                    newRole.setName(RoleEntity.RoleName.ROLE_USER);
                    newRole.setDescription("Usuario estándar con permisos básicos");
                    return roleRepository.save(newRole);
                });
        
        // Crear el nuevo usuario
        UserEntity user = UserEntity.builder()
                .username(username)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .build();
        
        UserEntity savedUser = userRepository.save(user);
        
        log.info("Usuario registrado exitosamente: {}", savedUser.getEmail());
        loggingService.logSecurityEvent("USER_REGISTERED", savedUser.getEmail(),
                Map.of("userId", savedUser.getId()));
        
        // Generar token JWT
        String accessToken = jwtService.generateToken(savedUser);
        
        return buildAuthResponse(savedUser, accessToken);
    }
    
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Procesando login para: {}", request.getUsernameOrEmail());
        
        try {
            // Autenticar con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );
            
            UserEntity user = (UserEntity) authentication.getPrincipal();
            user.updateLastLogin();
            
            log.info("Login exitoso para usuario: {}", user.getUsername());
            loggingService.logAuthenticationSuccess(user.getUsername());
            
            // Generar token JWT
            String accessToken = jwtService.generateToken(user);
            
            return buildAuthResponse(user, accessToken);
            
        } catch (BadCredentialsException e) {
            log.warn("Credenciales inválidas para: {}", request.getUsernameOrEmail());
            loggingService.logAuthenticationFailure(request.getUsernameOrEmail(), "bad_credentials");
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
            
        } catch (DisabledException e) {
            log.warn("Cuenta deshabilitada para: {}", request.getUsernameOrEmail());
            loggingService.logAuthenticationFailure(request.getUsernameOrEmail(), "account_disabled");
            throw new DisabledException("La cuenta está deshabilitada");
            
        } catch (LockedException e) {
            log.warn("Cuenta bloqueada para: {}", request.getUsernameOrEmail());
            loggingService.logAuthenticationFailure(request.getUsernameOrEmail(), "account_locked");
            throw new LockedException("La cuenta está bloqueada");
        }
    }
    
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        log.debug("Procesando refresh token");
        
        String username = jwtService.extractUsername(refreshToken);
        
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        
        if (!jwtService.isTokenValid(refreshToken, user)) {
            loggingService.logSecurityEvent("REFRESH_TOKEN_INVALID", username,
                    Map.of("reason", "invalid_or_expired"));
            throw new BadCredentialsException("Refresh token inválido o expirado");
        }
        
        String newAccessToken = jwtService.generateToken(user);
        
        log.info("Token refrescado exitosamente para: {}", username);
        
        return buildAuthResponse(user, newAccessToken);
    }
    
    private AuthResponse buildAuthResponse(UserEntity user, String accessToken) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        
        return AuthResponse.success(
                accessToken,
                jwtService.getExpirationInSeconds(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }
}
