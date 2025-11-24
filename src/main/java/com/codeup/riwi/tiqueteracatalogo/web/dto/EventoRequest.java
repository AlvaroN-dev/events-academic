package com.codeup.riwi.tiqueteracatalogo.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * DTO para recibir datos de creación/actualización de Eventos.
 * Incluye validaciones y documentación Swagger.
 */
@Schema(description = "Datos de entrada para crear o actualizar un evento")
public class EventoRequest {

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Schema(description = "Nombre del evento", example = "Concierto Rock 2025", required = true)
    private String name;

    @Schema(description = "Descripción detallada del evento", example = "Gran concierto de rock con bandas internacionales")
    private String description;

    @NotNull(message = "La fecha del evento es obligatoria")
    @Schema(description = "Fecha y hora del evento", example = "2025-12-15T20:00:00", required = true)
    private LocalDateTime eventDate;

    @NotNull(message = "El venue ID es obligatorio")
    @Schema(description = "ID del venue donde se realizará el evento", example = "1", required = true)
    private Long venueId;

    @Positive(message = "La capacidad debe ser mayor a 0")
    @Schema(description = "Capacidad máxima del evento", example = "1000", required = true)
    private Integer capacity;

    @Positive(message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio de la entrada", example = "80000.00", required = true)
    private Double price;

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
