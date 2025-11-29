package com.codeup.riwi.tiqueteracatalogo.application.usecases.evento;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Evento;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.EventoRepositoryPort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Use case for listing all events.
 * 
 * Transaction Configuration:
 * - readOnly = true: Optimizes for read operations (no flush, cache optimization)
 * - Propagation.SUPPORTS: Uses existing transaction if available, otherwise non-transactional
 */
public class ListarEventosUseCase {

    private final EventoRepositoryPort eventoRepository;

    public ListarEventosUseCase(EventoRepositoryPort eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    /**
     * Execute the use case to list all events
     * 
     * @return List of all events
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Evento> ejecutar() {
        return eventoRepository.findAll();
    }

    /**
     * Execute the use case to list events by venue
     * 
     * @param venueId Venue ID
     * @return List of events for the venue
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Evento> ejecutarPorVenue(Long venueId) {
        return eventoRepository.findByVenueId(venueId);
    }

    /**
     * Execute the use case to count total events
     * 
     * @return Total number of events
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public long contar() {
        return eventoRepository.count();
    }
}
