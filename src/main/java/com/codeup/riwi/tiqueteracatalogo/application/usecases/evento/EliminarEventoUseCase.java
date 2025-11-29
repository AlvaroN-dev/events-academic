package com.codeup.riwi.tiqueteracatalogo.application.usecases.evento;

import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.EventoRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.excepcion.RecursoNoEncontradoException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for deleting an event.
 * 
 * Transaction Configuration:
 * - Propagation.REQUIRED: Joins existing transaction or creates new one
 * - Isolation.READ_COMMITTED: Standard isolation for delete operations
 * - Rollback on any exception for data integrity
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
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        rollbackFor = Exception.class
    )
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
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        rollbackFor = Exception.class
    )
    public boolean ejecutarConBoolean(Long id) {
        if (!eventoRepository.existsById(id)) {
            return false;
        }

        eventoRepository.deleteById(id);
        return true;
    }
}
