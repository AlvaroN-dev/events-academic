package com.codeup.riwi.tiqueteracatalogo.application.usecases.evento;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Evento;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.EventoRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.VenueRepositoryPort;

/**
 * Use case for creating a new event.
 * Contains business logic and validation rules.
 */
public class CrearEventoUseCase {

    private final EventoRepositoryPort eventoRepository;
    private final VenueRepositoryPort venueRepository;

    public CrearEventoUseCase(EventoRepositoryPort eventoRepository, VenueRepositoryPort venueRepository) {
        this.eventoRepository = eventoRepository;
        this.venueRepository = venueRepository;
    }

    /**
     * Execute the use case to create a new event
     * 
     * @param evento Event to create
     * @return Created event
     * @throws IllegalArgumentException if validation fails
     */
    public Evento ejecutar(Evento evento) {
        // Business rule: Venue must exist
        if (!venueRepository.existsById(evento.getVenueId())) {
            throw new IllegalArgumentException(
                    String.format("Venue con ID %d no encontrado", evento.getVenueId()));
        }

        // Business rule: Event name must be unique
        if (eventoRepository.existsByName(evento.getName())) {
            throw new IllegalArgumentException(
                    String.format("Ya existe un evento con el nombre: %s", evento.getName()));
        }

        // Save and return
        return eventoRepository.save(evento);
    }
}
