package com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.specification;

import com.codeup.riwi.tiqueteracatalogo.domain.models.EventStatus;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.CategoryJpaEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.EventoJpaEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.VenueJpaEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications for EventoJpaEntity.
 * Provides dynamic, composable query filters using the Criteria API.
 * 
 * Benefits:
 * - Type-safe queries (no string-based field names at runtime)
 * - Composable: combine multiple specifications with and/or
 * - Reusable: each method returns a specification that can be reused
 * - Dynamic: build queries based on runtime conditions
 * 
 * Usage:
 * Specification<EventoJpaEntity> spec = EventoSpecification.hasStatus(ACTIVE)
 *     .and(EventoSpecification.inDateRange(start, end))
 *     .and(EventoSpecification.byVenueId(1L));
 * repository.findAll(spec);
 */
public class EventoSpecification {

    private EventoSpecification() {
        // Utility class - prevent instantiation
    }

    // ============ Status Filters ============

    /**
     * Filter events by status
     * 
     * @param status Event status to filter by
     * @return Specification for status filter
     */
    public static Specification<EventoJpaEntity> hasStatus(EventStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction(); // No filter
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    /**
     * Filter events that are active (not cancelled or completed)
     * 
     * @return Specification for active events
     */
    public static Specification<EventoJpaEntity> isActive() {
        return hasStatus(EventStatus.ACTIVE);
    }

    /**
     * Filter events by multiple statuses (IN clause)
     * 
     * @param statuses List of statuses to include
     * @return Specification for status filter
     */
    public static Specification<EventoJpaEntity> hasStatusIn(List<EventStatus> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("status").in(statuses);
        };
    }

    // ============ Date Filters ============

    /**
     * Filter events within a date range
     * 
     * @param startDate Start of the date range (inclusive)
     * @param endDate End of the date range (inclusive)
     * @return Specification for date range filter
     */
    public static Specification<EventoJpaEntity> inDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), endDate));
            }
            
            return predicates.isEmpty() 
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter events after a specific date
     * 
     * @param date Start date (exclusive)
     * @return Specification for date filter
     */
    public static Specification<EventoJpaEntity> afterDate(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThan(root.get("eventDate"), date);
        };
    }

    /**
     * Filter events before a specific date
     * 
     * @param date End date (exclusive)
     * @return Specification for date filter
     */
    public static Specification<EventoJpaEntity> beforeDate(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThan(root.get("eventDate"), date);
        };
    }

    /**
     * Filter upcoming events (from now onwards)
     * 
     * @return Specification for upcoming events
     */
    public static Specification<EventoJpaEntity> isUpcoming() {
        return afterDate(LocalDateTime.now());
    }

    /**
     * Filter past events
     * 
     * @return Specification for past events
     */
    public static Specification<EventoJpaEntity> isPast() {
        return beforeDate(LocalDateTime.now());
    }

    // ============ Venue Filters ============

    /**
     * Filter events by venue ID
     * 
     * @param venueId Venue ID to filter by
     * @return Specification for venue filter
     */
    public static Specification<EventoJpaEntity> byVenueId(Long venueId) {
        return (root, query, criteriaBuilder) -> {
            if (venueId == null) {
                return criteriaBuilder.conjunction();
            }
            // Use join to access venue relationship
            Join<EventoJpaEntity, VenueJpaEntity> venueJoin = root.join("venue", JoinType.INNER);
            return criteriaBuilder.equal(venueJoin.get("id"), venueId);
        };
    }

    /**
     * Filter events by venue city
     * 
     * @param city City name (case-insensitive)
     * @return Specification for venue city filter
     */
    public static Specification<EventoJpaEntity> byVenueCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            Join<EventoJpaEntity, VenueJpaEntity> venueJoin = root.join("venue", JoinType.INNER);
            return criteriaBuilder.equal(
                criteriaBuilder.lower(venueJoin.get("city")),
                city.toLowerCase()
            );
        };
    }

    /**
     * Filter events by venue with minimum capacity
     * 
     * @param minCapacity Minimum venue capacity
     * @return Specification for venue capacity filter
     */
    public static Specification<EventoJpaEntity> byVenueMinCapacity(Integer minCapacity) {
        return (root, query, criteriaBuilder) -> {
            if (minCapacity == null) {
                return criteriaBuilder.conjunction();
            }
            Join<EventoJpaEntity, VenueJpaEntity> venueJoin = root.join("venue", JoinType.INNER);
            return criteriaBuilder.greaterThanOrEqualTo(venueJoin.get("capacity"), minCapacity);
        };
    }

    // ============ Category Filters ============

    /**
     * Filter events by category ID
     * 
     * @param categoryId Category ID to filter by
     * @return Specification for category filter
     */
    public static Specification<EventoJpaEntity> byCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            // Use join for ManyToMany relationship
            Join<EventoJpaEntity, CategoryJpaEntity> categoryJoin = root.join("categories", JoinType.INNER);
            
            // Avoid duplicate results when joining
            if (query != null) {
                query.distinct(true);
            }
            
            return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
        };
    }

    /**
     * Filter events by category name
     * 
     * @param categoryName Category name (case-insensitive)
     * @return Specification for category name filter
     */
    public static Specification<EventoJpaEntity> byCategoryName(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            if (categoryName == null || categoryName.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            Join<EventoJpaEntity, CategoryJpaEntity> categoryJoin = root.join("categories", JoinType.INNER);
            
            if (query != null) {
                query.distinct(true);
            }
            
            return criteriaBuilder.equal(
                criteriaBuilder.lower(categoryJoin.get("name")),
                categoryName.toLowerCase()
            );
        };
    }

    /**
     * Filter events that have any category (not empty)
     * 
     * @return Specification for events with categories
     */
    public static Specification<EventoJpaEntity> hasCategories() {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<EventoJpaEntity> subRoot = subquery.from(EventoJpaEntity.class);
            subquery.select(subRoot.get("id"))
                   .where(criteriaBuilder.isNotEmpty(subRoot.get("categories")));
            
            return root.get("id").in(subquery);
        };
    }

    // ============ Text Search Filters ============

    /**
     * Search events by name (contains, case-insensitive)
     * 
     * @param name Name to search for
     * @return Specification for name search
     */
    public static Specification<EventoJpaEntity> nameLike(String name) {
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
     * Search events by description (contains, case-insensitive)
     * 
     * @param keyword Keyword to search for
     * @return Specification for description search
     */
    public static Specification<EventoJpaEntity> descriptionContains(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("description")),
                "%" + keyword.toLowerCase() + "%"
            );
        };
    }

    /**
     * Search events by name or description
     * 
     * @param keyword Keyword to search for
     * @return Specification for combined search
     */
    public static Specification<EventoJpaEntity> searchByKeyword(String keyword) {
        return nameLike(keyword).or(descriptionContains(keyword));
    }

    // ============ Price Filters ============

    /**
     * Filter events by price range
     * 
     * @param minPrice Minimum price (inclusive)
     * @param maxPrice Maximum price (inclusive)
     * @return Specification for price range filter
     */
    public static Specification<EventoJpaEntity> inPriceRange(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            
            return predicates.isEmpty()
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ============ Capacity Filters ============

    /**
     * Filter events by capacity range
     * 
     * @param minCapacity Minimum capacity (inclusive)
     * @param maxCapacity Maximum capacity (inclusive)
     * @return Specification for capacity range filter
     */
    public static Specification<EventoJpaEntity> inCapacityRange(Integer minCapacity, Integer maxCapacity) {
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

    // ============ Combined/Utility Filters ============

    /**
     * Filter events that are available for sale (active, upcoming, not sold out)
     * 
     * @return Specification for available events
     */
    public static Specification<EventoJpaEntity> isAvailableForSale() {
        return hasStatusIn(List.of(EventStatus.ACTIVE))
            .and(isUpcoming());
    }

    /**
     * Fetch venue eagerly in query to avoid N+1
     * Note: Use this with caution - for read operations only
     * 
     * @return Specification that fetches venue
     */
    public static Specification<EventoJpaEntity> fetchVenue() {
        return (root, query, criteriaBuilder) -> {
            // Only apply fetch for non-count queries
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("venue", JoinType.LEFT);
            }
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Fetch categories eagerly in query to avoid N+1
     * Note: Use this with caution - for read operations only
     * 
     * @return Specification that fetches categories
     */
    public static Specification<EventoJpaEntity> fetchCategories() {
        return (root, query, criteriaBuilder) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("categories", JoinType.LEFT);
                query.distinct(true);
            }
            return criteriaBuilder.conjunction();
        };
    }
}
