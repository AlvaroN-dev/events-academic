package com.codeup.riwi.tiqueteracatalogo.domain.models;

/**
 * Modelo de dominio que representa las credenciales de login.
 * Este objeto pertenece al dominio y es independiente de la infraestructura.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public class CredencialesLogin {
    
    private String usernameOrEmail;
    private String password;
    
    public CredencialesLogin() {}
    
    public CredencialesLogin(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }
    
    // Getters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }
    
    public String getPassword() {
        return password;
    }
    
    // Setters
    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String usernameOrEmail;
        private String password;
        
        public Builder usernameOrEmail(String usernameOrEmail) {
            this.usernameOrEmail = usernameOrEmail;
            return this;
        }
        
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        
        public CredencialesLogin build() {
            return new CredencialesLogin(usernameOrEmail, password);
        }
    }
}
