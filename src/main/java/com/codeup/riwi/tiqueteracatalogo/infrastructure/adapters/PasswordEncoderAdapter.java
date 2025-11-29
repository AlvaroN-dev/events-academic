package com.codeup.riwi.tiqueteracatalogo.infrastructure.adapters;

import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador que implementa PasswordEncoderPort usando Spring Security.
 * Encapsula la dependencia de PasswordEncoder de Spring Security.
 * 
 * Este adaptador sigue el patrón de arquitectura hexagonal:
 * - Implementa el puerto de salida definido en el dominio
 * - Aísla la lógica de negocio de los detalles de Spring Security
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort {
    
    private final PasswordEncoder passwordEncoder;
    
    public PasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
