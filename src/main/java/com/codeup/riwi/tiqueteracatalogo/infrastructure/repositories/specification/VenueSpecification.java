package com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.specification;

import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.EventoJpaEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.VenueJpaEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications for VenueJpaEntity.
 * Provides dynamic, composable query filters using the Criteria API.
 * 
 * Benefits:
 * - Type-safe queries using Criteria API
 * - Composable: combine multiple specifications with and/or
 * - Reusable across different query scenarios
 * - Optimized for performance with proper joins
 * 
 * Usage:
 * Specification<VenueJpaEntity> spec = VenueSpecification.byCity("Bogotá")
 *     .and(VenueSpecification.minCapacity(100));
 * repository.findAll(spec);
 */
public class VenueSpecification {

    private VenueSpecification() {
        // Utility class - prevent instantiation
    }

    // ============ Location Filters ============

    /**
     * Filter venues by city (case-insensitive, exact match)
     * 
     * @param city City name
     * @return Specification for city filter
     */
    public static Specification<VenueJpaEntity> byCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                criteriaBuilder.lower(root.get("city")),
                city.toLowerCase()
            );
        };
    }

    /**
     * Filter venues by city (case-insensitive, contains)
     * 
     * @param city City name or part of it
     * @return Specification for city search
     */
    public static Specification<VenueJpaEntity> cityLike(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("city")),
                "%" + city.toLowerCase() + "%"
            );
        };
    }

    /**
     * Filter venues by country (case-insensitive)
     * 
     * @param country Country name
     * @return Specification for country filter
     */
    public static Specification<VenueJpaEntity> byCountry(String country) {
        return (root, query, criteriaBuilder) -> {
            if (country == null || country.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                criteriaBuilder.lower(root.get("country")),
                country.toLowerCase()
            );
        };
    }

    /**
     * Filter venues by address (contains, case-insensitive)
     * 
     * @param address Address keyword
     * @return Specification for address search
     */
    public static Specification<VenueJpaEntity> addressContains(String address) {
        return (root, query, criteriaBuilder) -> {
            if (address == null || address.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("address")),
                "%" + address.toLowerCase() + "%"
            );
        };
    }

    /**
     * Filter venues by city and country
     * 
     * @param city City name
     * @param country Country name
     * @return Combined specification for location
     */
    public static Specification<VenueJpaEntity> byLocation(String city, String country) {
        return byCity(city).and(byCountry(country));
    }

    // ============ Capacity Filters ============

    /**
     * Filter venues with minimum capacity
     * 
     * @param minCapacity Minimum capacity (inclusive)
     * @return Specification for minimum capacity filter
     */
    public static Specification<VenueJpaEntity> minCapacity(Integer minCapacity) {
        return (root, query, criteriaBuilder) -> {
            if (minCapacity == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("capacity"), minCapacity);
        };
    }

    /**
     * Filter venues with maximum capacity
     * 
     * @param maxCapacity Maximum capacity (inclusive)
     * @return Specification for maximum capacity filter
     */
    public static Specification<VenueJpaEntity> maxCapacity(Integer maxCapacity) {
        return (root, query, criteriaBuilder) -> {
            if (maxCapacity == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("capacity"), maxCapacity);
        };
    }

    /**
     * Filter venues within a capacity range
     * 
     * @param minCapacity Minimum capacity (inclusive)
     * @param maxCapacity Maximum capacity (inclusive)
     * @return Specification for capacity range filter
     */
    public static Specification<VenueJpaEntity> capacityBetween(Integer minCapacity, Integer maxCapacity) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (minCapacity != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("capacity"), minCapacity));
            }
            if (maxCapacity != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("capacity"), maxCapacity));
            }
            
            return predicates.isEmpty()
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ============ Name Search ============

    /**
     * Search venues by name (contains, case-insensitive)
     * 
     * @param name Name to search for
     * @return Specification for name search
     */
    public static Specification<VenueJpaEntity> nameLike(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
            );
        };
    }

    /**
     * Search venues by name (exact, case-insensitive)
     * 
     * @param name Exact name to match
     * @return Specification for exact name match
     */
    public static Specification<VenueJpaEntity> byName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                criteriaBuilder.lower(root.get("name")),
                name.toLowerCase()
            );
        };
    }

    // ============ Event-Related Filters ============

    /**
     * Filter venues that have at least one event
     * 
     * @return Specification for venues with events
     */
    public static Specification<VenueJpaEntity> hasEvents() {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<EventoJpaEntity> eventoRoot = subquery.from(EventoJpaEntity.class);
            subquery.select(criteriaBuilder.count(eventoRoot))
                   .where(criteriaBuilder.equal(eventoRoot.get("venue"), root));
            
            return criteriaBuilder.greaterThan(subquery, 0L);
        };
    }

    /**
     * Filter venues that have no events
     * 
     * @return Specification for empty venues
     */
    public static Specification<VenueJpaEntity> hasNoEvents() {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<EventoJpaEntity> eventoRoot = subquery.from(EventoJpaEntity.class);
            subquery.select(criteriaBuilder.count(eventoRoot))
                   .where(criteriaBuilder.equal(eventoRoot.get("venue"), root));
            
            return criteriaBuilder.equal(subquery, 0L);
        };
    }

    /**
     * Filter venues with at least N events
     * 
     * @param minEvents Minimum number of events
     * @return Specification for venues with minimum events
     */
    public static Specification<VenueJpaEntity> hasMinimumEvents(long minEvents) {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<EventoJpaEntity> eventoRoot = subquery.from(EventoJpaEntity.class);
            subquery.select(criteriaBuilder.count(eventoRoot))
                   .where(criteriaBuilder.equal(eventoRoot.get("venue"), root));
            
            return criteriaBuilder.greaterThanOrEqualTo(subquery, minEvents);
        };
    }

    // ============ Fetch Optimizations ============

    /**
     * Fetch events eagerly to avoid N+1 queries
     * WARNING: Use only for read operations that need events
     * 
     * @return Specification that fetches events
     */
    public static Specification<VenueJpaEntity> fetchEvents() {
        return (root, query, criteriaBuilder) -> {
            // Only apply fetch for non-count queries
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("eventos", JoinType.LEFT);
                query.distinct(true);
            }
            return criteriaBuilder.conjunction();
        };
    }

    // ============ Combined/Utility Filters ============

    /**
     * General keyword search across name, city, and address
     * 
     * @param keyword Keyword to search for
     * @return Specification for combined search
     */
    public static Specification<VenueJpaEntity> searchByKeyword(String keyword) {
        return nameLike(keyword)
            .or(cityLike(keyword))
            .or(addressContains(keyword));
    }

    /**
     * Find suitable venues for an event of given capacity
     * Venues with capacity >= required capacity
     * 
     * @param requiredCapacity Required capacity
     * @return Specification for suitable venues
     */
    public static Specification<VenueJpaEntity> suitableFor(Integer requiredCapacity) {
        return minCapacity(requiredCapacity);
    }

    /**
     * Find venues in a specific city with minimum capacity
     * Common use case for event planning
     * 
     * @param city City name
     * @param minCapacity Minimum venue capacity
     * @return Combined specification
     */
    public static Specification<VenueJpaEntity> inCityWithCapacity(String city, Integer minCapacity) {
        return byCity(city).and(minCapacity(minCapacity));
    }
}
