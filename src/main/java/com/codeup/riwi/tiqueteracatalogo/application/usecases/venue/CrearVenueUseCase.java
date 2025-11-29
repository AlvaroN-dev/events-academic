package com.codeup.riwi.tiqueteracatalogo.application.usecases.venue;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Venue;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.VenueRepositoryPort;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for creating a new venue.
 * 
 * Transaction Configuration:
 * - Propagation.REQUIRED: Joins existing transaction or creates new one
 * - Isolation.READ_COMMITTED: Prevents dirty reads, good balance for writes
 * - Rollback on any exception for data integrity
 */
public class CrearVenueUseCase {

    private final VenueRepositoryPort venueRepository;

    public CrearVenueUseCase(VenueRepositoryPort venueRepository) {
        this.venueRepository = venueRepository;
    }

    /**
     * Execute the use case to create a new venue
     * 
     * @param venue Venue to create
     * @return Created venue
     */
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        rollbackFor = Exception.class
    )
    public Venue ejecutar(Venue venue) {
        return venueRepository.save(venue);
    }
}
