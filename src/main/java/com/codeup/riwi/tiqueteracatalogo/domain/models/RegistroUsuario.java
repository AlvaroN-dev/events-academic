package com.codeup.riwi.tiqueteracatalogo.domain.models;

/**
 * Modelo de dominio que representa los datos necesarios para registrar un usuario.
 * Este objeto pertenece al dominio y es independiente de la infraestructura.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public class RegistroUsuario {
    
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    
    public RegistroUsuario() {}
    
    public RegistroUsuario(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
    
    // Getters
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
    
    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        
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
        
        public RegistroUsuario build() {
            return new RegistroUsuario(firstName, lastName, email, password);
        }
    }
}
