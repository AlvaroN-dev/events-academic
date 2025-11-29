package com.codeup.riwi.tiqueteracatalogo.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for dynamic venue filtering.
 * Used with VenueSpecification to build queries based on provided criteria.
 * All fields are optional - only non-null fields are used for filtering.
 */
@Schema(description = "Filtros dinámicos para búsqueda de venues")
public record VenueFilterDTO(
    
    @Schema(description = "Búsqueda por nombre del venue (contiene)", example = "estadio")
    String name,
    
    @Schema(description = "Ciudad del venue (exacto)", example = "Bogotá")
    String city,
    
    @Schema(description = "País del venue", example = "Colombia")
    String country,
    
    @Schema(description = "Búsqueda en dirección (contiene)", example = "calle 50")
    String address,
    
    @Schema(description = "Capacidad mínima", example = "100")
    Integer minCapacity,
    
    @Schema(description = "Capacidad máxima", example = "10000")
    Integer maxCapacity,
    
    @Schema(description = "Búsqueda general (nombre, ciudad, dirección)", example = "centro")
    String keyword,
    
    @Schema(description = "Solo venues con eventos programados", example = "true")
    Boolean withEventsOnly,
    
    @Schema(description = "Solo venues sin eventos (disponibles)", example = "false")
    Boolean emptyOnly,
    
    @Schema(description = "Número mínimo de eventos", example = "5")
    Long minEvents
) {
    /**
     * Builder pattern for easier construction
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String city;
        private String country;
        private String address;
        private Integer minCapacity;
        private Integer maxCapacity;
        private String keyword;
        private Boolean withEventsOnly;
        private Boolean emptyOnly;
        private Long minEvents;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
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

        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder withEventsOnly(Boolean withEventsOnly) {
            this.withEventsOnly = withEventsOnly;
            return this;
        }

        public Builder emptyOnly(Boolean emptyOnly) {
            this.emptyOnly = emptyOnly;
            return this;
        }

        public Builder minEvents(Long minEvents) {
            this.minEvents = minEvents;
            return this;
        }

        public VenueFilterDTO build() {
            return new VenueFilterDTO(
                name, city, country, address, minCapacity, maxCapacity,
                keyword, withEventsOnly, emptyOnly, minEvents
            );
        }
    }
}
