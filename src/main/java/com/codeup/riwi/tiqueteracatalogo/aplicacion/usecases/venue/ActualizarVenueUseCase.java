package com.codeup.riwi.tiqueteracatalogo.aplicacion.usecases.venue;

import com.codeup.riwi.tiqueteracatalogo.dominio.models.Venue;
import com.codeup.riwi.tiqueteracatalogo.dominio.ports.out.VenueRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.dominio.excepcion.RecursoNoEncontradoException;

/**
 * Use case for updating an existing venue.
 */
public class ActualizarVenueUseCase {

    private final VenueRepositoryPort venueRepository;

    public ActualizarVenueUseCase(VenueRepositoryPort venueRepository) {
        this.venueRepository = venueRepository;
    }

    /**
     * Execute the use case to update a venue
     * 
     * @param id               Venue ID
     * @param venueActualizado Updated venue data
     * @return Updated venue
     * @throws RecursoNoEncontradoException if venue not found
     */
    public Venue ejecutar(Long id, Venue venueActualizado) {
        // Verify venue exists
        if (!venueRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Venue", id);
        }

        // Set ID to ensure we're updating the correct venue
        venueActualizado.setId(id);

        // Save and return
        return venueRepository.save(venueActualizado);
    }
}
