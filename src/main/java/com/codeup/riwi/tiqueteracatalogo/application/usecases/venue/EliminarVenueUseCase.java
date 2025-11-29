package com.codeup.riwi.tiqueteracatalogo.application.usecases.venue;

import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.VenueRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.excepcion.RecursoNoEncontradoException;

/**
 * Use case for deleting a venue.
 */
public class EliminarVenueUseCase {

    private final VenueRepositoryPort venueRepository;

    public EliminarVenueUseCase(VenueRepositoryPort venueRepository) {
        this.venueRepository = venueRepository;
    }

    /**
     * Execute the use case to delete a venue
     * 
     * @param id Venue ID
     * @throws RecursoNoEncontradoException if venue not found
     */
    public void ejecutar(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Venue", id);
        }

        venueRepository.deleteById(id);
    }

    /**
     * Execute the use case to delete a venue (returns boolean)
     * 
     * @param id Venue ID
     * @return true if deleted, false if not found
     */
    public boolean ejecutarConBoolean(Long id) {
        if (!venueRepository.existsById(id)) {
            return false;
        }

        venueRepository.deleteById(id);
        return true;
    }
}
