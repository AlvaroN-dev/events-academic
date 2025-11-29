package com.codeup.riwi.tiqueteracatalogo.domain.ports.out;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Venue;

import java.util.List;
import java.util.Optional;

/**
 * Output port (interface) for Venue repository.
 * Defines the contract for persistence operations.
 * Will be implemented by infrastructure layer (JPA adapter).
 */
public interface VenueRepositoryPort {

    /**
     * Find all venues
     * 
     * @return List of all venues
     */
    List<Venue> findAll();

    /**
     * Find venue by ID
     * 
     * @param id Venue ID
     * @return Optional containing the venue if found
     */
    Optional<Venue> findById(Long id);

    /**
     * Save a venue (create or update)
     * 
     * @param venue Venue to save
     * @return Saved venue
     */
    Venue save(Venue venue);

    /**
     * Delete venue by ID
     * 
     * @param id Venue ID
     */
    void deleteById(Long id);

    /**
     * Check if venue exists by ID
     * 
     * @param id Venue ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Find venues by city (case-insensitive)
     * 
     * @param city City name
     * @return List of venues in the city
     */
    List<Venue> findByCity(String city);

    /**
     * Count total venues
     * 
     * @return Total number of venues
     */
    long count();
}
