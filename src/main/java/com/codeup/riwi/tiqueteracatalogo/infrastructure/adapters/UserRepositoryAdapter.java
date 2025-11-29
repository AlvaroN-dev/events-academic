package com.codeup.riwi.tiqueteracatalogo.infrastructure.adapters;

import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.UserRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.RoleEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.UserEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.RoleRepository;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa UserRepositoryPort usando JPA.
 * Maneja la traducción entre modelos de dominio y entidades JPA.
 * 
 * Este adaptador sigue el patrón de arquitectura hexagonal:
 * - Implementa el puerto de salida definido en el dominio
 * - Traduce entre objetos de dominio y entidades de infraestructura
 * - Aísla la lógica de negocio de los detalles de persistencia
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {
    
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryAdapter.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    public UserRepositoryAdapter(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    
    @Override
    public Optional<UserDomainData> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toDomainData);
    }
    
    @Override
    public Optional<UserDomainData> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::toDomainData);
    }
    
    @Override
    public Optional<UserDomainData> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(this::toDomainData);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public UserDomainData save(UserDomainData userData) {
        UserEntity entity = toEntity(userData);
        UserEntity saved = userRepository.save(entity);
        log.debug("Usuario guardado con ID: {}", saved.getId());
        return toDomainData(saved);
    }
    
    @Override
    public String getDefaultRoleName() {
        return RoleEntity.RoleName.ROLE_USER.name();
    }
    
    @Override
    public void updateLastLogin(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.updateLastLogin();
            userRepository.save(user);
            log.debug("Último login actualizado para usuario ID: {}", userId);
        });
    }
    
    // ============ Mapper methods ============
    
    /**
     * Convierte una entidad JPA a datos de dominio.
     */
    private UserDomainData toDomainData(UserEntity entity) {
        Set<String> roles = entity.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        
        return UserDomainData.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .roles(roles)
                .enabled(entity.isEnabled())
                .accountNonLocked(entity.isAccountNonLocked())
                .accountNonExpired(entity.isAccountNonExpired())
                .credentialsNonExpired(entity.isCredentialsNonExpired())
                .build();
    }
    
    /**
     * Convierte datos de dominio a una entidad JPA.
     */
    private UserEntity toEntity(UserDomainData userData) {
        // Obtener o crear roles
        Set<RoleEntity> roleEntities = userData.roles().stream()
                .map(roleName -> {
                    RoleEntity.RoleName enumName = RoleEntity.RoleName.valueOf(roleName);
                    return roleRepository.findByName(enumName)
                            .orElseGet(() -> {
                                log.info("Creando rol: {}", roleName);
                                RoleEntity newRole = new RoleEntity();
                                newRole.setName(enumName);
                                newRole.setDescription("Rol " + roleName);
                                return roleRepository.save(newRole);
                            });
                })
                .collect(Collectors.toSet());
        
        return UserEntity.builder()
                .id(userData.id())
                .username(userData.username())
                .firstName(userData.firstName())
                .lastName(userData.lastName())
                .email(userData.email())
                .password(userData.password())
                .roles(roleEntities)
                .enabled(userData.enabled())
                .accountNonLocked(userData.accountNonLocked())
                .accountNonExpired(userData.accountNonExpired())
                .credentialsNonExpired(userData.credentialsNonExpired())
                .build();
    }
}
