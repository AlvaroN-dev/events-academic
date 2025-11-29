package com.codeup.riwi.tiqueteracatalogo.application.usecases.venue;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Venue;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.VenueRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.excepcion.RecursoNoEncontradoException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Use case for retrieving a venue by ID.
 * 
 * Transaction Configuration:
 * - readOnly = true: Optimizes for read operations (no flush, cache optimization)
 * - Propagation.SUPPORTS: Uses existing transaction if available, otherwise non-transactional
 */
public class ObtenerVenueUseCase {

    private final VenueRepositoryPort venueRepository;

    public ObtenerVenueUseCase(VenueRepositoryPort venueRepository) {
        this.venueRepository = venueRepository;
    }

    /**
     * Execute the use case to get a venue by ID
     * 
     * @param id Venue ID
     * @return Venue if found
     * @throws RecursoNoEncontradoException if venue not found
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Venue ejecutar(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venue", id));
    }

    /**
     * Execute the use case to get a venue by ID (returns Optional)
     * 
     * @param id Venue ID
     * @return Optional containing venue if found
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Optional<Venue> ejecutarOptional(Long id) {
        return venueRepository.findById(id);
    }
}
