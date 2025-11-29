package com.codeup.riwi.tiqueteracatalogo.domain.ports.out;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Evento;

import java.util.List;
import java.util.Optional;

/**
 * Output port (interface) for Evento repository.
 * Defines the contract for persistence operations.
 * Will be implemented by infrastructure layer (JPA adapter).
 */
public interface EventoRepositoryPort {

    /**
     * Find all events
     * 
     * @return List of all events
     */
    List<Evento> findAll();

    /**
     * Find event by ID
     * 
     * @param id Event ID
     * @return Optional containing the event if found
     */
    Optional<Evento> findById(Long id);

    /**
     * Save an event (create or update)
     * 
     * @param evento Event to save
     * @return Saved event
     */
    Evento save(Evento evento);

    /**
     * Delete event by ID
     * 
     * @param id Event ID
     */
    void deleteById(Long id);

    /**
     * Check if event exists by ID
     * 
     * @param id Event ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Check if event exists by name
     * 
     * @param name Event name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if event exists by name excluding a specific ID
     * 
     * @param name Event name
     * @param id   Event ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Find events by venue ID
     * 
     * @param venueId Venue ID
     * @return List of events for the venue
     */
    List<Evento> findByVenueId(Long venueId);

    /**
     * Count total events
     * 
     * @return Total number of events
     */
    long count();
}
