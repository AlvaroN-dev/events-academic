package com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories;

import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for CategoryJpaEntity.
 * This is the infrastructure layer - uses Spring Data JPA.
 * 
 * Manages the ManyToMany relationship with Events from the Category side.
 */
@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {

    /**
     * Find category by name (case-insensitive)
     * 
     * @param name Category name
     * @return Optional with category if found
     */
    Optional<CategoryJpaEntity> findByNameIgnoreCase(String name);

    /**
     * Check if category exists by name
     * 
     * @param name Category name
     * @return true if exists
     */
    boolean existsByName(String name);

    /**
     * Check if category exists by name excluding a specific ID
     * 
     * @param name Category name
     * @param id   Category ID to exclude
     * @return true if exists
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Find all active categories
     * 
     * @return List of active categories
     */
    List<CategoryJpaEntity> findByActiveTrue();

    /**
     * Find categories with events (fetch join to avoid N+1)
     * 
     * @param id Category ID
     * @return Optional with category and its events
     */
    @Query("SELECT c FROM CategoryJpaEntity c LEFT JOIN FETCH c.eventos WHERE c.id = :id")
    Optional<CategoryJpaEntity> findByIdWithEventos(@Param("id") Long id);

    /**
     * Count events in a category
     * 
     * @param categoryId Category ID
     * @return Number of events in the category
     */
    @Query("SELECT COUNT(e) FROM CategoryJpaEntity c JOIN c.eventos e WHERE c.id = :categoryId")
    long countEventosByCategoryId(@Param("categoryId") Long categoryId);
}
