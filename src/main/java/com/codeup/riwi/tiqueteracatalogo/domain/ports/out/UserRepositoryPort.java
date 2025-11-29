package com.codeup.riwi.tiqueteracatalogo.domain.ports.out;

import java.util.Optional;
import java.util.Set;

/**
 * Puerto de salida (Output Port) para el repositorio de usuarios.
 * Define el contrato para operaciones de persistencia de usuarios.
 * Será implementado por un adaptador en la capa de infraestructura.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public interface UserRepositoryPort {
    
    /**
     * Busca un usuario por email.
     * 
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<UserDomainData> findByEmail(String email);
    
    /**
     * Busca un usuario por username.
     * 
     * @param username Username del usuario
     * @return Optional con el usuario si existe
     */
    Optional<UserDomainData> findByUsername(String username);
    
    /**
     * Busca un usuario por username o email.
     * 
     * @param usernameOrEmail Username o email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<UserDomainData> findByUsernameOrEmail(String usernameOrEmail);
    
    /**
     * Verifica si existe un usuario con el email dado.
     * 
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el username dado.
     * 
     * @param username Username a verificar
     * @return true si existe, false si no
     */
    boolean existsByUsername(String username);
    
    /**
     * Guarda un nuevo usuario.
     * 
     * @param userData Datos del usuario a guardar
     * @return Datos del usuario guardado con ID asignado
     */
    UserDomainData save(UserDomainData userData);
    
    /**
     * Obtiene el rol por defecto para nuevos usuarios.
     * 
     * @return Nombre del rol por defecto
     */
    String getDefaultRoleName();
    
    /**
     * Actualiza el último login del usuario.
     * 
     * @param userId ID del usuario
     */
    void updateLastLogin(Long userId);
    
    /**
     * Clase interna para transportar datos de usuario entre capas.
     * Evita exponer entidades de infraestructura al dominio.
     */
    record UserDomainData(
            Long id,
            String username,
            String firstName,
            String lastName,
            String email,
            String password,
            Set<String> roles,
            boolean enabled,
            boolean accountNonLocked,
            boolean accountNonExpired,
            boolean credentialsNonExpired
    ) {
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private Long id;
            private String username;
            private String firstName;
            private String lastName;
            private String email;
            private String password;
            private Set<String> roles;
            private boolean enabled = true;
            private boolean accountNonLocked = true;
            private boolean accountNonExpired = true;
            private boolean credentialsNonExpired = true;
            
            public Builder id(Long id) {
                this.id = id;
                return this;
            }
            
            public Builder username(String username) {
                this.username = username;
                return this;
            }
            
            public Builder firstName(String firstName) {
                this.firstName = firstName;
                return this;
            }
            
            public Builder lastName(String lastName) {
                this.lastName = lastName;
                return this;
            }
            
            public Builder email(String email) {
                this.email = email;
                return this;
            }
            
            public Builder password(String password) {
                this.password = password;
                return this;
            }
            
            public Builder roles(Set<String> roles) {
                this.roles = roles;
                return this;
            }
            
            public Builder enabled(boolean enabled) {
                this.enabled = enabled;
                return this;
            }
            
            public Builder accountNonLocked(boolean accountNonLocked) {
                this.accountNonLocked = accountNonLocked;
                return this;
            }
            
            public Builder accountNonExpired(boolean accountNonExpired) {
                this.accountNonExpired = accountNonExpired;
                return this;
            }
            
            public Builder credentialsNonExpired(boolean credentialsNonExpired) {
                this.credentialsNonExpired = credentialsNonExpired;
                return this;
            }
            
            public UserDomainData build() {
                return new UserDomainData(id, username, firstName, lastName, email, password, 
                        roles, enabled, accountNonLocked, accountNonExpired, credentialsNonExpired);
            }
        }
    }
}
