package com.codeup.riwi.tiqueteracatalogo.domain.ports.out;

import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.UserRepositoryPort.UserDomainData;

/**
 * Puerto de salida (Output Port) para operaciones de JWT.
 * Define el contrato para generación y validación de tokens.
 * Será implementado por un adaptador en la capa de infraestructura.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public interface JwtPort {
    
    /**
     * Genera un token JWT para el usuario.
     * 
     * @param userData Datos del usuario
     * @return Token JWT generado
     */
    String generateToken(UserDomainData userData);
    
    /**
     * Genera un token de refresco para el usuario.
     * 
     * @param userData Datos del usuario
     * @return Token de refresco generado
     */
    String generateRefreshToken(UserDomainData userData);
    
    /**
     * Extrae el username de un token.
     * 
     * @param token Token JWT
     * @return Username extraído
     */
    String extractUsername(String token);
    
    /**
     * Valida si un token es válido para el usuario dado.
     * 
     * @param token Token JWT
     * @param userData Datos del usuario
     * @return true si es válido, false si no
     */
    boolean isTokenValid(String token, UserDomainData userData);
    
    /**
     * Obtiene el tiempo de expiración del token en segundos.
     * 
     * @return Tiempo de expiración en segundos
     */
    long getExpirationInSeconds();
}
