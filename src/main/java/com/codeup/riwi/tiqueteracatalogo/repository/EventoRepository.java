package com.codeup.riwi.tiqueteracatalogo.repository;

import com.codeup.riwi.tiqueteracatalogo.domain.entity.EventoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for EventoEntity.
 * Provides CRUD operations, custom query methods, and pagination/filtering
 * support.
 */
@Repository
public interface EventoRepository extends JpaRepository<EventoEntity, Long>, JpaSpecificationExecutor<EventoEntity> {

    /**
     * Find events by venue ID
     * 
     * @param venueId Venue ID
     * @return List of events for the specified venue
     */
    List<EventoEntity> findByVenue_Id(Long venueId);

    /**
     * Check if an event with the given name exists
     * 
     * @param name Event name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if an event with the given name exists excluding a specific ID
     * 
     * @param name Event name
     * @param id   Event ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Find event by name (case-insensitive)
     * 
     * @param name Event name
     * @return Optional with event if found
     */
    Optional<EventoEntity> findByNameIgnoreCase(String name);

    /**
     * Find events by venue city with pagination
     * 
     * @param city     City name
     * @param pageable Pagination parameters
     * @return Page of events
     */
    Page<EventoEntity> findByVenue_CityIgnoreCase(String city, Pageable pageable);

    /**
     * Find events by categoria with pagination
     * 
     * @param categoria Event categoria
     * @param pageable  Pagination parameters
     * @return Page of events
     */
    Page<EventoEntity> findByCategoriaIgnoreCase(String categoria, Pageable pageable);

    /**
     * Find events after a specific date with pagination
     * 
     * @param fecha    Start date
     * @param pageable Pagination parameters
     * @return Page of events
     */
    Page<EventoEntity> findByEventDateAfter(LocalDateTime fecha, Pageable pageable);
}
