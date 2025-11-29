package com.codeup.riwi.tiqueteracatalogo.application.usecases.venue;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Venue;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.VenueRepositoryPort;

import java.util.List;

/**
 * Use case for listing all venues.
 */
public class ListarVenuesUseCase {

    private final VenueRepositoryPort venueRepository;

    public ListarVenuesUseCase(VenueRepositoryPort venueRepository) {
        this.venueRepository = venueRepository;
    }

    /**
     * Execute the use case to list all venues
     * 
     * @return List of all venues
     */
    public List<Venue> ejecutar() {
        return venueRepository.findAll();
    }

    /**
     * Execute the use case to list venues by city
     * 
     * @param city City name
     * @return List of venues in the city
     */
    public List<Venue> ejecutarPorCiudad(String city) {
        return venueRepository.findByCity(city);
    }

    /**
     * Execute the use case to count total venues
     * 
     * @return Total number of venues
     */
    public long contar() {
        return venueRepository.count();
    }
}
