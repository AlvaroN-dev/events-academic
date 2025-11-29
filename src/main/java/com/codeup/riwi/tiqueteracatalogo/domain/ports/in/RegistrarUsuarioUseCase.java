package com.codeup.riwi.tiqueteracatalogo.domain.ports.in;

import com.codeup.riwi.tiqueteracatalogo.domain.models.AuthResult;
import com.codeup.riwi.tiqueteracatalogo.domain.models.RegistroUsuario;

/**
 * Puerto de entrada (Input Port) para el caso de uso de registro de usuario.
 * Define el contrato para registrar nuevos usuarios en el sistema.
 * 
 * Este puerto sigue el principio de arquitectura hexagonal, donde
 * la lógica de negocio se define en el dominio y se implementa
 * en la capa de aplicación.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
public interface RegistrarUsuarioUseCase {
    
    /**
     * Ejecuta el caso de uso de registro de usuario.
     * 
     * @param registro Datos del usuario a registrar
     * @return Resultado de la autenticación con token JWT
     * @throws IllegalArgumentException si el email ya está registrado
     */
    AuthResult ejecutar(RegistroUsuario registro);
}
