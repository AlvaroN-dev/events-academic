package com.codeup.riwi.tiqueteracatalogo.application.usecases.evento;

import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.EventoRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.excepcion.RecursoNoEncontradoException;

/**
 * Use case for deleting an event.
 */
public class EliminarEventoUseCase {

    private final EventoRepositoryPort eventoRepository;

    public EliminarEventoUseCase(EventoRepositoryPort eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    /**
     * Execute the use case to delete an event
     * 
     * @param id Event ID
     * @throws RecursoNoEncontradoException if event not found
     */
    public void ejecutar(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Evento", id);
        }

        eventoRepository.deleteById(id);
    }

    /**
     * Execute the use case to delete an event (returns boolean)
     * 
     * @param id Event ID
     * @return true if deleted, false if not found
     */
    public boolean ejecutarConBoolean(Long id) {
        if (!eventoRepository.existsById(id)) {
            return false;
        }

        eventoRepository.deleteById(id);
        return true;
    }
}
