package com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories;

import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.VenueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for VenueJpaEntity.
 * This is the infrastructure layer - uses Spring Data JPA.
 */
@Repository
public interface VenueJpaRepository extends JpaRepository<VenueJpaEntity, Long> {

    /**
     * Find venues by city (case-insensitive)
     * 
     * @param city City name
     * @return List of venues
     */
    List<VenueJpaEntity> findByCityIgnoreCase(String city);
}
