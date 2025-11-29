package com.codeup.riwi.tiqueteracatalogo.application.usecases.evento;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Evento;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.EventoRepositoryPort;

import java.util.List;

/**
 * Use case for listing all events.
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
    public List<Evento> ejecutar() {
        return eventoRepository.findAll();
    }

    /**
     * Execute the use case to list events by venue
     * 
     * @param venueId Venue ID
     * @return List of events for the venue
     */
    public List<Evento> ejecutarPorVenue(Long venueId) {
        return eventoRepository.findByVenueId(venueId);
    }

    /**
     * Execute the use case to count total events
     * 
     * @return Total number of events
     */
    public long contar() {
        return eventoRepository.count();
    }
}
