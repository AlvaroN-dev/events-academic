package com.codeup.riwi.tiqueteracatalogo.application.usecases.evento;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Evento;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.EventoRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.excepcion.RecursoNoEncontradoException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Use case for retrieving an event by ID.
 * 
 * Transaction Configuration:
 * - readOnly = true: Optimizes for read operations (no flush, cache optimization)
 * - Propagation.SUPPORTS: Uses existing transaction if available, otherwise non-transactional
 */
public class ObtenerEventoUseCase {

    private final EventoRepositoryPort eventoRepository;

    public ObtenerEventoUseCase(EventoRepositoryPort eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    /**
     * Execute the use case to get an event by ID
     * 
     * @param id Event ID
     * @return Event if found
     * @throws RecursoNoEncontradoException if event not found
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Evento ejecutar(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento", id));
    }

    /**
     * Execute the use case to get an event by ID (returns Optional)
     * 
     * @param id Event ID
     * @return Optional containing event if found
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Optional<Evento> ejecutarOptional(Long id) {
        return eventoRepository.findById(id);
    }
}
