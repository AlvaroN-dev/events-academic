package com.codeup.riwi.tiqueteracatalogo.infrastructure.dto;

import com.codeup.riwi.tiqueteracatalogo.domain.models.EventStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for dynamic event filtering.
 * Used with EventoSpecification to build queries based on provided criteria.
 * All fields are optional - only non-null fields are used for filtering.
 */
@Schema(description = "Filtros dinámicos para búsqueda de eventos")
public record EventoFilterDTO(
    
    @Schema(description = "Estado del evento", example = "ACTIVE")
    EventStatus status,
    
    @Schema(description = "Lista de estados del evento para filtrar")
    List<EventStatus> statuses,
    
    @Schema(description = "Fecha de inicio del rango (desde)", example = "2025-01-01T00:00:00")
    LocalDateTime startDate,
    
    @Schema(description = "Fecha de fin del rango (hasta)", example = "2025-12-31T23:59:59")
    LocalDateTime endDate,
    
    @Schema(description = "ID del venue donde se realiza el evento", example = "1")
    Long venueId,
    
    @Schema(description = "Ciudad del venue", example = "Bogotá")
    String venueCity,
    
    @Schema(description = "ID de la categoría del evento", example = "1")
    Long categoryId,
    
    @Schema(description = "Nombre de la categoría", example = "Música")
    String categoryName,
    
    @Schema(description = "Búsqueda por nombre del evento (contiene)", example = "concierto")
    String name,
    
    @Schema(description = "Búsqueda por palabra clave en nombre o descripción", example = "rock")
    String keyword,
    
    @Schema(description = "Precio mínimo", example = "10.0")
    Double minPrice,
    
    @Schema(description = "Precio máximo", example = "100.0")
    Double maxPrice,
    
    @Schema(description = "Capacidad mínima del evento", example = "50")
    Integer minCapacity,
    
    @Schema(description = "Capacidad máxima del evento", example = "500")
    Integer maxCapacity,
    
    @Schema(description = "Solo eventos próximos (futuros)", example = "true")
    Boolean upcomingOnly,
    
    @Schema(description = "Solo eventos con categorías asignadas", example = "false")
    Boolean withCategoriesOnly
) {
    /**
     * Builder pattern for easier construction
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EventStatus status;
        private List<EventStatus> statuses;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Long venueId;
        private String venueCity;
        private Long categoryId;
        private String categoryName;
        private String name;
        private String keyword;
        private Double minPrice;
        private Double maxPrice;
        private Integer minCapacity;
        private Integer maxCapacity;
        private Boolean upcomingOnly;
        private Boolean withCategoriesOnly;

        public Builder status(EventStatus status) {
            this.status = status;
            return this;
        }

        public Builder statuses(List<EventStatus> statuses) {
            this.statuses = statuses;
            return this;
        }

        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder venueId(Long venueId) {
            this.venueId = venueId;
            return this;
        }

        public Builder venueCity(String venueCity) {
            this.venueCity = venueCity;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder minPrice(Double minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public Builder maxPrice(Double maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public Builder minCapacity(Integer minCapacity) {
            this.minCapacity = minCapacity;
            return this;
        }

        public Builder maxCapacity(Integer maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Builder upcomingOnly(Boolean upcomingOnly) {
            this.upcomingOnly = upcomingOnly;
            return this;
        }

        public Builder withCategoriesOnly(Boolean withCategoriesOnly) {
            this.withCategoriesOnly = withCategoriesOnly;
            return this;
        }

        public EventoFilterDTO build() {
            return new EventoFilterDTO(
                status, statuses, startDate, endDate, venueId, venueCity,
                categoryId, categoryName, name, keyword, minPrice, maxPrice,
                minCapacity, maxCapacity, upcomingOnly, withCategoriesOnly
            );
        }
    }
}
