package com.codeup.riwi.tiqueteracatalogo.repository.specification;

import com.codeup.riwi.tiqueteracatalogo.domain.entity.EventoEntity;
import com.codeup.riwi.tiqueteracatalogo.domain.entity.VenueEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Specifications for dynamic filtering of EventoEntity.
 * Provides reusable predicates for complex queries.
 */
public class EventoSpecification {

    /**
     * Filter by venue city (case-insensitive)
     * 
     * @param ciudad City name
     * @return Specification for filtering by city
     */
    public static Specification<EventoEntity> hasCity(String ciudad) {
        return (root, query, criteriaBuilder) -> {
            if (ciudad == null || ciudad.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            Join<EventoEntity, VenueEntity> venueJoin = root.join("venue");
            return criteriaBuilder.like(
                    criteriaBuilder.lower(venueJoin.get("city")),
                    "%" + ciudad.toLowerCase() + "%");
        };
    }

    /**
     * Filter by categoria (case-insensitive)
     * 
     * @param categoria Event categoria
     * @return Specification for filtering by categoria
     */
    public static Specification<EventoEntity> hasCategoria(String categoria) {
        return (root, query, criteriaBuilder) -> {
            if (categoria == null || categoria.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("categoria")),
                    "%" + categoria.toLowerCase() + "%");
        };
    }

    /**
     * Filter by events after a specific date
     * 
     * @param fechaInicio Start date
     * @return Specification for filtering by date
     */
    public static Specification<EventoEntity> hasEventDateAfter(LocalDateTime fechaInicio) {
        return (root, query, criteriaBuilder) -> {
            if (fechaInicio == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), fechaInicio);
        };
    }

    /**
     * Combine all filters
     * 
     * @param ciudad      City filter
     * @param categoria   Categoria filter
     * @param fechaInicio Date filter
     * @return Combined specification
     */
    public static Specification<EventoEntity> withFilters(String ciudad, String categoria, LocalDateTime fechaInicio) {
        return Specification.where(hasCity(ciudad))
                .and(hasCategoria(categoria))
                .and(hasEventDateAfter(fechaInicio));
    }
}
