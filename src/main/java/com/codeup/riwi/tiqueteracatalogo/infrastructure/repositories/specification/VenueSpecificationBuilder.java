package com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.specification;

import com.codeup.riwi.tiqueteracatalogo.infrastructure.dto.VenueFilterDTO;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.VenueJpaEntity;
import org.springframework.data.jpa.domain.Specification;

/**
 * Builder for combining VenueSpecifications based on filter DTO.
 * Provides a fluent API to construct complex queries from filter parameters.
 * 
 * Usage:
 * Specification<VenueJpaEntity> spec = VenueSpecificationBuilder.build(filterDTO);
 * repository.findAll(spec);
 */
public class VenueSpecificationBuilder {

    private VenueSpecificationBuilder() {
        // Utility class
    }

    /**
     * Build a combined Specification from the filter DTO.
     * Only applies filters for non-null parameters.
     * 
     * @param filter The filter DTO with search criteria
     * @return Combined Specification for all provided filters
     */
    public static Specification<VenueJpaEntity> build(VenueFilterDTO filter) {
        if (filter == null) {
            return Specification.allOf();
        }

        Specification<VenueJpaEntity> spec = Specification.allOf();

        // Name search
        if (filter.name() != null && !filter.name().isBlank()) {
            spec = spec.and(VenueSpecification.nameLike(filter.name()));
        }

        // Location filters
        if (filter.city() != null && !filter.city().isBlank()) {
            spec = spec.and(VenueSpecification.byCity(filter.city()));
        }
        if (filter.country() != null && !filter.country().isBlank()) {
            spec = spec.and(VenueSpecification.byCountry(filter.country()));
        }
        if (filter.address() != null && !filter.address().isBlank()) {
            spec = spec.and(VenueSpecification.addressContains(filter.address()));
        }

        // Capacity filters
        if (filter.minCapacity() != null || filter.maxCapacity() != null) {
            spec = spec.and(VenueSpecification.capacityBetween(filter.minCapacity(), filter.maxCapacity()));
        }

        // General keyword search
        if (filter.keyword() != null && !filter.keyword().isBlank()) {
            spec = spec.and(VenueSpecification.searchByKeyword(filter.keyword()));
        }

        // Event-related filters
        if (Boolean.TRUE.equals(filter.withEventsOnly())) {
            spec = spec.and(VenueSpecification.hasEvents());
        }
        if (Boolean.TRUE.equals(filter.emptyOnly())) {
            spec = spec.and(VenueSpecification.hasNoEvents());
        }
        if (filter.minEvents() != null && filter.minEvents() > 0) {
            spec = spec.and(VenueSpecification.hasMinimumEvents(filter.minEvents()));
        }

        return spec;
    }

    /**
     * Build a specification with eager fetch for events.
     * Use this for detail operations that need event data.
     * WARNING: Can lead to large result sets for venues with many events.
     * 
     * @param filter The filter DTO
     * @return Specification with events fetch
     */
    public static Specification<VenueJpaEntity> buildWithEventsFetch(VenueFilterDTO filter) {
        return build(filter).and(VenueSpecification.fetchEvents());
    }
}
