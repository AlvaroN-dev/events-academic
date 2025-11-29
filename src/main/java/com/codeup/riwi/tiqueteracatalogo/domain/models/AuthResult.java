package com.codeup.riwi.tiqueteracatalogo.domain.models;

import java.util.Set;

/**
 * Modelo de dominio que representa el resultado de una autenticación exitosa.
 * Este objeto pertenece al dominio y es independiente de la infraestructura.
 * 
 * Contiene la información necesaria para la respuesta de autenticación
 * sin acoplar el dominio a DTOs de infraestructura.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public class AuthResult {
    
    private boolean success;
    private String message;
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private Long userId;
    private String username;
    private String email;
    private Set<String> roles;
    
    public AuthResult() {}
    
    private AuthResult(Builder builder) {
        this.success = builder.success;
        this.message = builder.message;
        this.accessToken = builder.accessToken;
        this.tokenType = builder.tokenType;
        this.expiresIn = builder.expiresIn;
        this.userId = builder.userId;
        this.username = builder.username;
        this.email = builder.email;
        this.roles = builder.roles;
    }
    
    // Factory method for success
    public static AuthResult success(String accessToken, long expiresIn, Long userId, 
                                     String username, String email, Set<String> roles) {
        return builder()
                .success(true)
                .message("Autenticación exitosa")
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .userId(userId)
                .username(username)
                .email(email)
                .roles(roles)
                .build();
    }
    
    // Factory method for failure
    public static AuthResult failure(String message) {
        return builder()
                .success(false)
                .message(message)
                .build();
    }
    
    // Getters
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private boolean success;
        private String message;
        private String accessToken;
        private String tokenType = "Bearer";
        private long expiresIn;
        private Long userId;
        private String username;
        private String email;
        private Set<String> roles;
        
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }
        
        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }
        
        public Builder expiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }
        
        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder roles(Set<String> roles) {
            this.roles = roles;
            return this;
        }
        
        public AuthResult build() {
            return new AuthResult(this);
        }
    }
}
