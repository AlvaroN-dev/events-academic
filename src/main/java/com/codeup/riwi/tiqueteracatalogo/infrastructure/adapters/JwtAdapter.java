package com.codeup.riwi.tiqueteracatalogo.infrastructure.adapters;

import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.JwtPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.UserRepositoryPort.UserDomainData;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.config.security.JwtService;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.RoleEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.UserEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa JwtPort usando JwtService de infraestructura.
 * Traduce entre modelos de dominio y la implementación JWT concreta.
 * 
 * Este adaptador sigue el patrón de arquitectura hexagonal:
 * - Implementa el puerto de salida definido en el dominio
 * - Encapsula la dependencia de JwtService
 * - Traduce UserDomainData a UserEntity cuando es necesario
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Component
public class JwtAdapter implements JwtPort {
    
    private final JwtService jwtService;
    
    public JwtAdapter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    @Override
    public String generateToken(UserDomainData userData) {
        UserEntity userEntity = toUserEntity(userData);
        return jwtService.generateToken(userEntity);
    }
    
    @Override
    public String generateRefreshToken(UserDomainData userData) {
        UserEntity userEntity = toUserEntity(userData);
        return jwtService.generateRefreshToken(userEntity);
    }
    
    @Override
    public String extractUsername(String token) {
        return jwtService.extractUsername(token);
    }
    
    @Override
    public boolean isTokenValid(String token, UserDomainData userData) {
        UserEntity userEntity = toUserEntity(userData);
        return jwtService.isTokenValid(token, userEntity);
    }
    
    @Override
    public long getExpirationInSeconds() {
        return jwtService.getExpirationInSeconds();
    }
    
    /**
     * Convierte UserDomainData a UserEntity para compatibilidad con JwtService.
     * Nota: Esta conversión es necesaria porque JwtService trabaja con UserDetails.
     */
    private UserEntity toUserEntity(UserDomainData userData) {
        // Crear un UserEntity mínimo para JWT (solo necesita username y roles)
        Set<RoleEntity> roles = userData.roles().stream()
                .map(roleName -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(RoleEntity.RoleName.valueOf(roleName));
                    return role;
                })
                .collect(Collectors.toSet());
        
        return UserEntity.builder()
                .id(userData.id())
                .username(userData.username())
                .email(userData.email())
                .password(userData.password())
                .roles(roles)
                .enabled(userData.enabled())
                .accountNonLocked(userData.accountNonLocked())
                .accountNonExpired(userData.accountNonExpired())
                .credentialsNonExpired(userData.credentialsNonExpired())
                .build();
    }
}
