package com.codeup.riwi.tiqueteracatalogo.application.usecases.venue;

import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.EventoRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.VenueRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.excepcion.RecursoNoEncontradoException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for deleting a venue.
 * 
 * Entity Lifecycle Impact:
 * - When a venue is deleted, ALL associated events are also deleted
 *   (due to cascade = CascadeType.ALL and orphanRemoval = true)
 * - This is a DESTRUCTIVE operation - use with caution
 * - Consider implementing soft delete if data preservation is required
 * 
 * Transaction Configuration:
 * - Propagation.REQUIRED: Ensures all delete operations are in same transaction
 * - Isolation.SERIALIZABLE: Highest isolation for delete with cascades
 * - Rollback on any exception to maintain data integrity
 */
public class EliminarVenueUseCase {

    private final VenueRepositoryPort venueRepository;
    private final EventoRepositoryPort eventoRepository;

    public EliminarVenueUseCase(VenueRepositoryPort venueRepository, 
                                 EventoRepositoryPort eventoRepository) {
        this.venueRepository = venueRepository;
        this.eventoRepository = eventoRepository;
    }

    /**
     * Execute the use case to delete a venue and all its events
     * 
     * @param id Venue ID
     * @throws RecursoNoEncontradoException if venue not found
     */
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.SERIALIZABLE,
        rollbackFor = Exception.class
    )
    public void ejecutar(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Venue", id);
        }

        // Note: Events are deleted automatically due to cascade configuration
        // in VenueJpaEntity (cascade = CascadeType.ALL, orphanRemoval = true)
        venueRepository.deleteById(id);
    }

    /**
     * Execute the use case to delete a venue (returns boolean)
     * 
     * @param id Venue ID
     * @return true if deleted, false if not found
     */
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.SERIALIZABLE,
        rollbackFor = Exception.class
    )
    public boolean ejecutarConBoolean(Long id) {
        if (!venueRepository.existsById(id)) {
            return false;
        }

        venueRepository.deleteById(id);
        return true;
    }

    /**
     * Get the count of events that would be deleted if this venue is removed
     * Useful for confirmation dialogs
     * 
     * @param venueId Venue ID
     * @return Number of events that would be deleted
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public long contarEventosAfectados(Long venueId) {
        return eventoRepository.findByVenueId(venueId).size();
    }
}
