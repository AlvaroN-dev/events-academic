package com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.specification;

import com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.EventoFilterDTO;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.EventoJpaEntity;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builder for combining EventoSpecifications based on filter DTO.
 * Provides a fluent API to construct complex queries from filter parameters.
 * 
 * Usage:
 * Specification<EventoJpaEntity> spec = EventoSpecificationBuilder.build(filterDTO);
 * repository.findAll(spec);
 */
public class EventoSpecificationBuilder {

    private EventoSpecificationBuilder() {
        // Utility class
    }

    /**
     * Build a combined Specification from the filter DTO.
     * Only applies filters for non-null parameters.
     * 
     * @param filter The filter DTO with search criteria
     * @return Combined Specification for all provided filters
     */
    public static Specification<EventoJpaEntity> build(EventoFilterDTO filter) {
        if (filter == null) {
            return Specification.allOf();
        }

        Specification<EventoJpaEntity> spec = Specification.allOf();

        // Status filters
        if (filter.status() != null) {
            spec = spec.and(EventoSpecification.hasStatus(filter.status()));
        }
        if (filter.statuses() != null && !filter.statuses().isEmpty()) {
            spec = spec.and(EventoSpecification.hasStatusIn(filter.statuses()));
        }

        // Date filters
        if (filter.startDate() != null || filter.endDate() != null) {
            spec = spec.and(EventoSpecification.inDateRange(filter.startDate(), filter.endDate()));
        }
        if (Boolean.TRUE.equals(filter.upcomingOnly())) {
            spec = spec.and(EventoSpecification.isUpcoming());
        }

        // Venue filters
        if (filter.venueId() != null) {
            spec = spec.and(EventoSpecification.byVenueId(filter.venueId()));
        }
        if (filter.venueCity() != null && !filter.venueCity().isBlank()) {
            spec = spec.and(EventoSpecification.byVenueCity(filter.venueCity()));
        }

        // Category filters
        if (filter.categoryId() != null) {
            spec = spec.and(EventoSpecification.byCategoryId(filter.categoryId()));
        }
        if (filter.categoryName() != null && !filter.categoryName().isBlank()) {
            spec = spec.and(EventoSpecification.byCategoryName(filter.categoryName()));
        }
        if (Boolean.TRUE.equals(filter.withCategoriesOnly())) {
            spec = spec.and(EventoSpecification.hasCategories());
        }

        // Text search
        if (filter.name() != null && !filter.name().isBlank()) {
            spec = spec.and(EventoSpecification.nameLike(filter.name()));
        }
        if (filter.keyword() != null && !filter.keyword().isBlank()) {
            spec = spec.and(EventoSpecification.searchByKeyword(filter.keyword()));
        }

        // Price filters
        if (filter.minPrice() != null || filter.maxPrice() != null) {
            spec = spec.and(EventoSpecification.inPriceRange(filter.minPrice(), filter.maxPrice()));
        }

        // Capacity filters
        if (filter.minCapacity() != null || filter.maxCapacity() != null) {
            spec = spec.and(EventoSpecification.inCapacityRange(filter.minCapacity(), filter.maxCapacity()));
        }

        return spec;
    }

    /**
     * Build a specification with eager fetch for venue.
     * Use this for list operations that need venue data.
     * 
     * @param filter The filter DTO
     * @return Specification with venue fetch
     */
    public static Specification<EventoJpaEntity> buildWithVenueFetch(EventoFilterDTO filter) {
        return build(filter).and(EventoSpecification.fetchVenue());
    }

    /**
     * Build a specification with eager fetch for all relationships.
     * Use this for detail operations that need full data.
     * 
     * @param filter The filter DTO
     * @return Specification with all relationship fetches
     */
    public static Specification<EventoJpaEntity> buildWithAllFetches(EventoFilterDTO filter) {
        return build(filter)
                .and(EventoSpecification.fetchVenue())
                .and(EventoSpecification.fetchCategories());
    }
}
