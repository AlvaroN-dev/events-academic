package com.codeup.riwi.tiqueteracatalogo.domain.ports.in;

import com.codeup.riwi.tiqueteracatalogo.domain.models.AuthResult;
import com.codeup.riwi.tiqueteracatalogo.domain.models.CredencialesLogin;

/**
 * Puerto de entrada (Input Port) para el caso de uso de autenticación.
 * Define el contrato para autenticar usuarios en el sistema.
 * 
 * Este puerto sigue el principio de arquitectura hexagonal, donde
 * la lógica de negocio se define en el dominio y se implementa
 * en la capa de aplicación.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public interface AutenticarUsuarioUseCase {
    
    /**
     * Ejecuta el caso de uso de autenticación.
     * 
     * @param credenciales Credenciales de login (usuario/email y contraseña)
     * @return Resultado de la autenticación con token JWT
     * @throws org.springframework.security.authentication.BadCredentialsException si las credenciales son inválidas
     * @throws org.springframework.security.authentication.DisabledException si la cuenta está deshabilitada
     * @throws org.springframework.security.authentication.LockedException si la cuenta está bloqueada
     */
    AuthResult ejecutar(CredencialesLogin credenciales);
}
