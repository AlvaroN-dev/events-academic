package com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories;

import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.EventoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for EventoJpaEntity.
 * This is the infrastructure layer - uses Spring Data JPA.
 */
@Repository
public interface EventoJpaRepository extends JpaRepository<EventoJpaEntity, Long> {

    /**
     * Find events by venue ID
     * 
     * @param venueId Venue ID
     * @return List of events
     */
    List<EventoJpaEntity> findByVenueId(Long venueId);

    /**
     * Check if event exists by name
     * 
     * @param name Event name
     * @return true if exists
     */
    boolean existsByName(String name);

    /**
     * Check if event exists by name excluding a specific ID
     * 
     * @param name Event name
     * @param id   Event ID to exclude
     * @return true if exists
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Find event by name (case-insensitive)
     * 
     * @param name Event name
     * @return Optional with event if found
     */
    Optional<EventoJpaEntity> findByNameIgnoreCase(String name);
}
