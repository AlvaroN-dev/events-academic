package com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories;

import com.codeup.riwi.tiqueteracatalogo.domain.models.EventStatus;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.EventoJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for EventoJpaEntity.
 * This is the infrastructure layer - uses Spring Data JPA.
 * 
 * Optimization strategies implemented:
 * 1. JPQL queries for complex operations (no native SQL)
 * 2. @EntityGraph for eager fetching when needed (avoid N+1)
 * 3. JpaSpecificationExecutor for dynamic filters
 * 4. Pagination support for large result sets
 * 5. JOIN FETCH in JPQL for related entities
 * 
 * Note: With the ManyToOne relationship to Venue, queries now use
 * the venue entity reference instead of venueId field.
 */
@Repository
public interface EventoJpaRepository extends JpaRepository<EventoJpaEntity, Long>, 
                                             JpaSpecificationExecutor<EventoJpaEntity> {

    // ============ Basic Queries ============

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

    // ============ Venue-Related Queries (JPQL optimized) ============

    /**
     * Find events by venue ID using the ManyToOne relationship.
     * Uses JOIN FETCH to avoid N+1 when accessing venue data.
     * 
     * @param venueId Venue ID
     * @return List of events with venue pre-loaded
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue v " +
           "WHERE v.id = :venueId " +
           "ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> findByVenueId(@Param("venueId") Long venueId);

    /**
     * Find events by venue ID with pagination
     * 
     * @param venueId Venue ID
     * @param pageable Pagination info
     * @return Page of events
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "WHERE e.venue.id = :venueId " +
           "ORDER BY e.eventDate ASC")
    Page<EventoJpaEntity> findByVenueIdPaged(@Param("venueId") Long venueId, Pageable pageable);

    /**
     * Find events by venue city using JPQL JOIN
     * 
     * @param city City name (case-insensitive)
     * @return List of events in venues of that city
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue v " +
           "WHERE LOWER(v.city) = LOWER(:city) " +
           "ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> findByVenueCity(@Param("city") String city);

    /**
     * Count events by venue ID
     * 
     * @param venueId Venue ID
     * @return Number of events for the venue
     */
    @Query("SELECT COUNT(e) FROM EventoJpaEntity e WHERE e.venue.id = :venueId")
    long countByVenueId(@Param("venueId") Long venueId);

    // ============ Date Range Queries (JPQL) ============

    /**
     * Find events within a date range using JPQL.
     * Optimized with JOIN FETCH for venue.
     * 
     * @param startDate Start of range (inclusive)
     * @param endDate End of range (inclusive)
     * @return List of events in range with venue pre-loaded
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue " +
           "WHERE e.eventDate BETWEEN :startDate AND :endDate " +
           "ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find events within a date range with pagination
     * 
     * @param startDate Start of range
     * @param endDate End of range
     * @param pageable Pagination info
     * @return Page of events
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "WHERE e.eventDate BETWEEN :startDate AND :endDate")
    Page<EventoJpaEntity> findByDateRangePaged(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Find upcoming events (after current date)
     * 
     * @param now Current datetime
     * @return List of upcoming events
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue " +
           "WHERE e.eventDate > :now " +
           "AND e.status = 'ACTIVE' " +
           "ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> findUpcomingEvents(@Param("now") LocalDateTime now);

    /**
     * Find past events (before current date)
     * 
     * @param now Current datetime
     * @return List of past events
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "WHERE e.eventDate < :now " +
           "ORDER BY e.eventDate DESC")
    List<EventoJpaEntity> findPastEvents(@Param("now") LocalDateTime now);

    // ============ Status-Based Queries (JPQL) ============

    /**
     * Find events by status
     * 
     * @param status Event status
     * @return List of events with that status
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue " +
           "WHERE e.status = :status " +
           "ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> findByStatus(@Param("status") EventStatus status);

    /**
     * Find active events (status = ACTIVE and future date)
     * 
     * @param now Current datetime
     * @return List of active upcoming events
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue " +
           "WHERE e.status = 'ACTIVE' " +
           "AND e.eventDate > :now " +
           "ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> findActiveEvents(@Param("now") LocalDateTime now);

    /**
     * Find cancelled events
     * 
     * @return List of cancelled events
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "WHERE e.status = 'CANCELLED' " +
           "ORDER BY e.updatedAt DESC")
    List<EventoJpaEntity> findCancelledEvents();

    // ============ Category-Related Queries (JPQL for ManyToMany) ============

    /**
     * Find events with categories (fetch join to avoid N+1)
     * Uses @EntityGraph for more flexible loading
     * 
     * @param id Event ID
     * @return Optional with event and its categories
     */
    @EntityGraph(attributePaths = {"venue", "categories"})
    @Query("SELECT e FROM EventoJpaEntity e WHERE e.id = :id")
    Optional<EventoJpaEntity> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find events with categories pre-loaded
     * 
     * @param id Event ID
     * @return Optional with event and its categories
     */
    @Query("SELECT DISTINCT e FROM EventoJpaEntity e " +
           "LEFT JOIN FETCH e.categories " +
           "WHERE e.id = :id")
    Optional<EventoJpaEntity> findByIdWithCategories(@Param("id") Long id);

    /**
     * Find events by category name using JPQL JOIN
     * 
     * @param categoryName Category name
     * @return List of events in the category
     */
    @Query("SELECT DISTINCT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue " +
           "JOIN e.categories c " +
           "WHERE LOWER(c.name) = LOWER(:categoryName) " +
           "ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> findByCategoryName(@Param("categoryName") String categoryName);

    /**
     * Find events by category ID
     * 
     * @param categoryId Category ID
     * @return List of events in the category
     */
    @Query("SELECT DISTINCT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue " +
           "JOIN e.categories c " +
           "WHERE c.id = :categoryId " +
           "ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> findByCategoryId(@Param("categoryId") Long categoryId);

    // ============ Complex Combined Queries (JPQL) ============

    /**
     * Find events by venue ID and date range
     * 
     * @param venueId Venue ID
     * @param startDate Start of range
     * @param endDate End of range
     * @return List of events matching criteria
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue v " +
           "WHERE v.id = :venueId " +
           "AND e.eventDate BETWEEN :startDate AND :endDate " +
           "ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> findByVenueIdAndDateRange(
            @Param("venueId") Long venueId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find events by venue ID and status
     * 
     * @param venueId Venue ID
     * @param status Event status
     * @return List of events matching criteria
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue v " +
           "WHERE v.id = :venueId " +
           "AND e.status = :status " +
           "ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> findByVenueIdAndStatus(
            @Param("venueId") Long venueId,
            @Param("status") EventStatus status);

    /**
     * Search events by name (contains, case-insensitive)
     * 
     * @param name Partial name to search
     * @return List of matching events
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue " +
           "WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> searchByName(@Param("name") String name);

    /**
     * Search events by keyword in name or description
     * 
     * @param keyword Keyword to search
     * @return List of matching events
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue " +
           "WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Find events by price range
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of events in price range
     */
    @Query("SELECT e FROM EventoJpaEntity e " +
           "JOIN FETCH e.venue " +
           "WHERE e.price BETWEEN :minPrice AND :maxPrice " +
           "ORDER BY e.price ASC")
    List<EventoJpaEntity> findByPriceRange(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);

    // ============ EntityGraph Optimized Queries ============

    /**
     * Find all events with venue eagerly loaded (avoids N+1)
     * 
     * @return List of all events with venue
     */
    @EntityGraph(attributePaths = {"venue"})
    @Query("SELECT e FROM EventoJpaEntity e ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> findAllWithVenue();

    /**
     * Find all events with venue eagerly loaded with pagination
     * 
     * @param pageable Pagination info
     * @return Page of events with venue
     */
    @EntityGraph(attributePaths = {"venue"})
    @Query("SELECT e FROM EventoJpaEntity e")
    Page<EventoJpaEntity> findAllWithVenuePaged(Pageable pageable);

    /**
     * Find all events with full details (venue + categories)
     * 
     * @return List of events with all relationships loaded
     */
    @EntityGraph(attributePaths = {"venue", "categories"})
    @Query("SELECT DISTINCT e FROM EventoJpaEntity e ORDER BY e.eventDate ASC")
    List<EventoJpaEntity> findAllWithDetails();

    // ============ Statistics Queries ============

    /**
     * Count events by status
     * 
     * @param status Event status
     * @return Number of events with that status
     */
    @Query("SELECT COUNT(e) FROM EventoJpaEntity e WHERE e.status = :status")
    long countByStatus(@Param("status") EventStatus status);

    /**
     * Count upcoming events for a venue
     * 
     * @param venueId Venue ID
     * @param now Current datetime
     * @return Number of upcoming events
     */
    @Query("SELECT COUNT(e) FROM EventoJpaEntity e " +
           "WHERE e.venue.id = :venueId " +
           "AND e.eventDate > :now " +
           "AND e.status = 'ACTIVE'")
    long countUpcomingByVenueId(@Param("venueId") Long venueId, @Param("now") LocalDateTime now);

    /**
     * Get total capacity of active events
     * 
     * @return Sum of capacities of active events
     */
    @Query("SELECT COALESCE(SUM(e.capacity), 0) FROM EventoJpaEntity e WHERE e.status = 'ACTIVE'")
    long sumActiveEventsCapacity();
}
