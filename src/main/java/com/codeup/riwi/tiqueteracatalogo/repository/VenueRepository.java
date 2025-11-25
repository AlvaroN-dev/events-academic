package com.codeup.riwi.tiqueteracatalogo.repository;

import com.codeup.riwi.tiqueteracatalogo.domain.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for VenueEntity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface VenueRepository extends JpaRepository<VenueEntity, Long> {

    /**
     * Find venues by city (case-insensitive)
     * 
     * @param city City name
     * @return List of venues in the specified city
     */
    List<VenueEntity> findByCityIgnoreCase(String city);
}
