package com.codeup.riwi.tiqueteracatalogo.application.usecases.evento;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Evento;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.EventoRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.VenueRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.excepcion.RecursoNoEncontradoException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for updating an existing event.
 * Contains business logic and validation rules.
 * 
 * Transaction Configuration:
 * - Propagation.REQUIRED: Joins existing transaction or creates new one
 * - Isolation.REPEATABLE_READ: Prevents non-repeatable reads during update
 * - Rollback on any exception for data integrity
 */
public class ActualizarEventoUseCase {

    private final EventoRepositoryPort eventoRepository;
    private final VenueRepositoryPort venueRepository;

    public ActualizarEventoUseCase(EventoRepositoryPort eventoRepository, VenueRepositoryPort venueRepository) {
        this.eventoRepository = eventoRepository;
        this.venueRepository = venueRepository;
    }

    /**
     * Execute the use case to update an event
     * 
     * @param id                Event ID
     * @param eventoActualizado Updated event data
     * @return Updated event
     * @throws RecursoNoEncontradoException if event not found
     * @throws IllegalArgumentException     if validation fails
     */
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        rollbackFor = Exception.class
    )
    public Evento ejecutar(Long id, Evento eventoActualizado) {
        // Verify event exists
        if (!eventoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Evento", id);
        }

        // Business rule: Venue must exist
        if (!venueRepository.existsById(eventoActualizado.getVenueId())) {
            throw new IllegalArgumentException(
                    String.format("Venue con ID %d no encontrado", eventoActualizado.getVenueId()));
        }

        // Business rule: Event name must be unique (excluding current event)
        if (eventoRepository.existsByNameAndIdNot(eventoActualizado.getName(), id)) {
            throw new IllegalArgumentException(
                    String.format("Ya existe otro evento con el nombre: %s", eventoActualizado.getName()));
        }

        // Set ID to ensure we're updating the correct event
        eventoActualizado.setId(id);

        // Save and return
        return eventoRepository.save(eventoActualizado);
    }
}
