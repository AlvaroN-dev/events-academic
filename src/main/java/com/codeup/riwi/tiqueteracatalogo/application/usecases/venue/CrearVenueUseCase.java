package com.codeup.riwi.tiqueteracatalogo.application.usecases.venue;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Venue;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.VenueRepositoryPort;

/**
 * Use case for creating a new venue.
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
    public Venue ejecutar(Venue venue) {
        return venueRepository.save(venue);
    }
}
