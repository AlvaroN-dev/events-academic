package com.codeup.riwi.tiqueteracatalogo.domain.ports.in;

import com.codeup.riwi.tiqueteracatalogo.domain.models.AuthResult;

/**
 * Puerto de entrada (Input Port) para el caso de uso de refrescar token.
 * Define el contrato para renovar tokens JWT expirados.
 * 
 * Este puerto sigue el principio de arquitectura hexagonal, donde
 * la lógica de negocio se define en el dominio y se implementa
 * en la capa de aplicación.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public interface RefrescarTokenUseCase {
    
    /**
     * Ejecuta el caso de uso de refrescar token.
     * 
     * @param refreshToken Token de refresco actual
     * @return Resultado de la autenticación con nuevo token JWT
     * @throws org.springframework.security.authentication.BadCredentialsException si el token es inválido o expirado
     */
    AuthResult ejecutar(String refreshToken);
}
