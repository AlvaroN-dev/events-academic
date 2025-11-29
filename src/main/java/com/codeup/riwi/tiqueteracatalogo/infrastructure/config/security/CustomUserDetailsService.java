package com.codeup.riwi.tiqueteracatalogo.infrastructure.config.security;

import com.codeup.riwi.tiqueteracatalogo.infrastructure.config.logging.LoggingService;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.UserEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Implementación personalizada de UserDetailsService para Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    private final UserRepository userRepository;
    private final LoggingService loggingService;
    
    public CustomUserDetailsService(
            UserRepository userRepository,
            LoggingService loggingService) {
        this.userRepository = userRepository;
        this.loggingService = loggingService;
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("Buscando usuario: {}", usernameOrEmail);
        
        // Buscar por email o por username
        UserEntity user = userRepository.findByEmail(usernameOrEmail)
                .or(() -> userRepository.findByUsername(usernameOrEmail))
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", usernameOrEmail);
                    loggingService.logAuthenticationFailure(usernameOrEmail, "user_not_found");
                    return new UsernameNotFoundException(
                            String.format("Usuario no encontrado: %s", usernameOrEmail)
                    );
                });
        
        // Verificar si la cuenta está habilitada
        if (!user.isEnabled()) {
            log.warn("Intento de acceso a cuenta deshabilitada: {}", usernameOrEmail);
            loggingService.logSecurityEvent("DISABLED_ACCOUNT_ACCESS", usernameOrEmail,
                    Map.of("status", "disabled"));
        }
        
        // Verificar si la cuenta está bloqueada
        if (!user.isAccountNonLocked()) {
            log.warn("Intento de acceso a cuenta bloqueada: {}", usernameOrEmail);
            loggingService.logSecurityEvent("LOCKED_ACCOUNT_ACCESS", usernameOrEmail,
                    Map.of("status", "locked"));
        }
        
        log.debug("Usuario encontrado exitosamente: {}", user.getUsername());
        return user;
    }
    
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Usuario no encontrado con ID: %d", id)
                ));
    }
}
