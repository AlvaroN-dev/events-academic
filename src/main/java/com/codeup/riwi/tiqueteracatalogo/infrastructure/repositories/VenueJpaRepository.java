package com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories;

import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.VenueJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for VenueJpaEntity.
 * This is the infrastructure layer - uses Spring Data JPA.
 * 
 * Optimization strategies implemented:
 * 1. JPQL queries for complex operations (no native SQL)
 * 2. @EntityGraph for eager fetching when needed (avoid N+1)
 * 3. JpaSpecificationExecutor for dynamic filters
 * 4. Pagination support for large result sets
 * 5. JOIN FETCH in JPQL for related entities
 */
@Repository
public interface VenueJpaRepository extends JpaRepository<VenueJpaEntity, Long>,
                                            JpaSpecificationExecutor<VenueJpaEntity> {

    // ============ Basic Queries ============

    /**
     * Check if venue exists by name (case-insensitive)
     * 
     * @param name Venue name
     * @return true if exists
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Check if venue exists by name excluding a specific ID
     * 
     * @param name Venue name
     * @param id Venue ID to exclude
     * @return true if exists
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Find venue by name (case-insensitive, exact match)
     * 
     * @param name Venue name
     * @return Optional with venue if found
     */
    Optional<VenueJpaEntity> findByNameIgnoreCase(String name);

    // ============ Location-Based Queries (JPQL) ============

    /**
     * Find venues by city (case-insensitive)
     * 
     * @param city City name
     * @return List of venues
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE LOWER(v.city) = LOWER(:city) " +
           "ORDER BY v.name ASC")
    List<VenueJpaEntity> findByCityIgnoreCase(@Param("city") String city);

    /**
     * Find venues by city with pagination
     * 
     * @param city City name
     * @param pageable Pagination info
     * @return Page of venues
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE LOWER(v.city) = LOWER(:city)")
    Page<VenueJpaEntity> findByCityIgnoreCasePaged(@Param("city") String city, Pageable pageable);

    /**
     * Find venues by country (case-insensitive)
     * 
     * @param country Country name
     * @return List of venues
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE LOWER(v.country) = LOWER(:country) " +
           "ORDER BY v.city ASC, v.name ASC")
    List<VenueJpaEntity> findByCountryIgnoreCase(@Param("country") String country);

    /**
     * Find venues by city and country
     * 
     * @param city City name
     * @param country Country name
     * @return List of venues
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE LOWER(v.city) = LOWER(:city) " +
           "AND LOWER(v.country) = LOWER(:country) " +
           "ORDER BY v.name ASC")
    List<VenueJpaEntity> findByCityAndCountry(
            @Param("city") String city,
            @Param("country") String country);

    /**
     * Search venues by city (contains, case-insensitive)
     * 
     * @param city Partial city name
     * @return List of matching venues
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE LOWER(v.city) LIKE LOWER(CONCAT('%', :city, '%')) " +
           "ORDER BY v.city ASC")
    List<VenueJpaEntity> searchByCity(@Param("city") String city);

    /**
     * Search venues by address (contains, case-insensitive)
     * 
     * @param address Partial address
     * @return List of matching venues
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE LOWER(v.address) LIKE LOWER(CONCAT('%', :address, '%')) " +
           "ORDER BY v.name ASC")
    List<VenueJpaEntity> searchByAddress(@Param("address") String address);

    // ============ Capacity-Based Queries (JPQL) ============

    /**
     * Find venues with capacity greater than or equal to minimum
     * 
     * @param minCapacity Minimum capacity
     * @return List of venues with sufficient capacity
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE v.capacity >= :minCapacity " +
           "ORDER BY v.capacity ASC")
    List<VenueJpaEntity> findByMinCapacity(@Param("minCapacity") Integer minCapacity);

    /**
     * Find venues with capacity less than or equal to maximum
     * 
     * @param maxCapacity Maximum capacity
     * @return List of venues
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE v.capacity <= :maxCapacity " +
           "ORDER BY v.capacity DESC")
    List<VenueJpaEntity> findByMaxCapacity(@Param("maxCapacity") Integer maxCapacity);

    /**
     * Find venues within a capacity range
     * 
     * @param minCapacity Minimum capacity
     * @param maxCapacity Maximum capacity
     * @return List of venues in range
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE v.capacity BETWEEN :minCapacity AND :maxCapacity " +
           "ORDER BY v.capacity ASC")
    List<VenueJpaEntity> findByCapacityBetween(
            @Param("minCapacity") Integer minCapacity,
            @Param("maxCapacity") Integer maxCapacity);

    /**
     * Find venues by city with minimum capacity
     * 
     * @param city City name
     * @param minCapacity Minimum capacity
     * @return List of suitable venues
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE LOWER(v.city) = LOWER(:city) " +
           "AND v.capacity >= :minCapacity " +
           "ORDER BY v.capacity ASC")
    List<VenueJpaEntity> findByCityAndMinCapacity(
            @Param("city") String city,
            @Param("minCapacity") Integer minCapacity);

    // ============ Text Search Queries (JPQL) ============

    /**
     * Search venues by name (contains, case-insensitive)
     * 
     * @param name Partial name to search
     * @return List of matching venues
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "ORDER BY v.name ASC")
    List<VenueJpaEntity> searchByName(@Param("name") String name);

    /**
     * Search venues by keyword in name, city, or address
     * 
     * @param keyword Keyword to search
     * @return List of matching venues
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(v.city) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(v.address) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY v.name ASC")
    List<VenueJpaEntity> searchByKeyword(@Param("keyword") String keyword);

    // ============ Event-Related Queries (JPQL with JOIN) ============

    /**
     * Find venue by ID with events eagerly loaded (avoids N+1)
     * 
     * @param id Venue ID
     * @return Optional with venue and its events
     */
    @EntityGraph(attributePaths = {"eventos"})
    @Query("SELECT v FROM VenueJpaEntity v WHERE v.id = :id")
    Optional<VenueJpaEntity> findByIdWithEvents(@Param("id") Long id);

    /**
     * Find venues that have at least one event
     * 
     * @return List of venues with events
     */
    @Query("SELECT DISTINCT v FROM VenueJpaEntity v " +
           "JOIN v.eventos e " +
           "ORDER BY v.name ASC")
    List<VenueJpaEntity> findVenuesWithEvents();

    /**
     * Find venues that have no events
     * 
     * @return List of empty venues
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE v.eventos IS EMPTY " +
           "ORDER BY v.name ASC")
    List<VenueJpaEntity> findVenuesWithoutEvents();

    /**
     * Find venues with minimum number of events
     * 
     * @param minEvents Minimum event count
     * @return List of busy venues
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "WHERE SIZE(v.eventos) >= :minEvents " +
           "ORDER BY SIZE(v.eventos) DESC")
    List<VenueJpaEntity> findVenuesWithMinimumEvents(@Param("minEvents") int minEvents);

    /**
     * Find venues with active events (events with status ACTIVE)
     * 
     * @return List of venues with active events
     */
    @Query("SELECT DISTINCT v FROM VenueJpaEntity v " +
           "JOIN v.eventos e " +
           "WHERE e.status = 'ACTIVE' " +
           "ORDER BY v.name ASC")
    List<VenueJpaEntity> findVenuesWithActiveEvents();

    // ============ Statistics Queries ============

    /**
     * Count venues by city
     * 
     * @param city City name
     * @return Number of venues in city
     */
    @Query("SELECT COUNT(v) FROM VenueJpaEntity v WHERE LOWER(v.city) = LOWER(:city)")
    long countByCity(@Param("city") String city);

    /**
     * Get total capacity of all venues
     * 
     * @return Sum of all venue capacities
     */
    @Query("SELECT COALESCE(SUM(v.capacity), 0) FROM VenueJpaEntity v")
    long sumTotalCapacity();

    /**
     * Get average venue capacity
     * 
     * @return Average capacity
     */
    @Query("SELECT COALESCE(AVG(v.capacity), 0) FROM VenueJpaEntity v")
    double averageCapacity();

    /**
     * Get venue with most events
     * 
     * @return Venue with highest event count
     */
    @Query("SELECT v FROM VenueJpaEntity v " +
           "ORDER BY SIZE(v.eventos) DESC " +
           "LIMIT 1")
    Optional<VenueJpaEntity> findMostActiveVenue();

    /**
     * Get distinct cities where venues exist
     * 
     * @return List of city names
     */
    @Query("SELECT DISTINCT v.city FROM VenueJpaEntity v ORDER BY v.city ASC")
    List<String> findDistinctCities();

    /**
     * Get distinct countries where venues exist
     * 
     * @return List of country names
     */
    @Query("SELECT DISTINCT v.country FROM VenueJpaEntity v ORDER BY v.country ASC")
    List<String> findDistinctCountries();
}
