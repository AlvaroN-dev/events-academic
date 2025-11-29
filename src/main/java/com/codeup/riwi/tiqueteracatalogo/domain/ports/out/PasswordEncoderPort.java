package com.codeup.riwi.tiqueteracatalogo.domain.ports.out;

/**
 * Puerto de salida (Output Port) para codificación de contraseñas.
 * Define el contrato para cifrar y verificar contraseñas.
 * Será implementado por un adaptador en la capa de infraestructura.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public interface PasswordEncoderPort {
    
    /**
     * Codifica una contraseña en texto plano.
     * 
     * @param rawPassword Contraseña en texto plano
     * @return Contraseña codificada
     */
    String encode(String rawPassword);
    
    /**
     * Verifica si una contraseña en texto plano coincide con una codificada.
     * 
     * @param rawPassword Contraseña en texto plano
     * @param encodedPassword Contraseña codificada
     * @return true si coinciden, false si no
     */
    boolean matches(String rawPassword, String encodedPassword);
}
