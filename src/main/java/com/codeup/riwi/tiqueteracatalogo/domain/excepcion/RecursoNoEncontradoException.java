package com.codeup.riwi.tiqueteracatalogo.domain.excepcion;

/**
 * Domain exception thrown when a resource is not found.
 * Pure domain exception - no framework dependencies.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String message) {
        super(message);
    }

    public RecursoNoEncontradoException(String resourceName, Long id) {
        super(String.format("%s con ID %d no encontrado", resourceName, id));
    }
}
