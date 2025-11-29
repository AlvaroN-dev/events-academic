package com.codeup.riwi.tiqueteracatalogo.application.dto;

import com.codeup.riwi.tiqueteracatalogo.application.validation.ValidationGroups.OnCreate;
import com.codeup.riwi.tiqueteracatalogo.application.validation.ValidationGroups.OnUpdate;
import com.codeup.riwi.tiqueteracatalogo.application.validation.constraints.FutureDate;
import com.codeup.riwi.tiqueteracatalogo.application.validation.constraints.ValidPrice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * DTO for creating or updating an Event.
 * Includes advanced Bean Validation with validation groups for different scenarios.
 * 
 * <p>Validation Groups:</p>
 * <ul>
 *   <li>{@link OnCreate} - Validations applied when creating a new event</li>
 *   <li>{@link OnUpdate} - Validations applied when updating an existing event</li>
 * </ul>
 * 
 * @author TiqueteraCatalogo Team
 * @version 2.0
 */
@Schema(description = "Datos de entrada para crear o actualizar un evento")
public class EventoRequest {

    @NotBlank(message = "{validation.event.name.required}", groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 3, max = 200, message = "{validation.event.name.size}", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\\s\\-_.,!¡¿?()]+$", 
             message = "{validation.event.name.pattern}", 
             groups = {OnCreate.class, OnUpdate.class})
    @Schema(description = "Nombre del evento", example = "Concierto Rock 2025", required = true)
    private String name;

    @Size(max = 1000, message = "{validation.event.description.size}", groups = {OnCreate.class, OnUpdate.class})
    @Schema(description = "Descripción detallada del evento", example = "Gran concierto de rock con bandas internacionales")
    private String description;

    @NotNull(message = "{validation.event.date.required}", groups = {OnCreate.class, OnUpdate.class})
    @FutureDate(minHoursAhead = 24, message = "{validation.event.date.future.hours}", groups = OnCreate.class)
    @FutureDate(message = "{validation.event.date.future}", groups = OnUpdate.class)
    @Schema(description = "Fecha y hora del evento", example = "2025-12-15T20:00:00", required = true)
    private LocalDateTime eventDate;

    @NotNull(message = "{validation.event.venueId.required}", groups = OnCreate.class)
    @Positive(message = "{validation.event.venueId.positive}", groups = {OnCreate.class, OnUpdate.class})
    @Schema(description = "ID del venue donde se realizará el evento", example = "1", required = true)
    private Long venueId;

    @NotNull(message = "{validation.event.capacity.required}", groups = OnCreate.class)
    @Positive(message = "{validation.event.capacity.positive}", groups = {OnCreate.class, OnUpdate.class})
    @Max(value = 1000000, message = "{validation.event.capacity.max}", groups = {OnCreate.class, OnUpdate.class})
    @Schema(description = "Capacidad máxima del evento", example = "1000", required = true)
    private Integer capacity;

    @NotNull(message = "{validation.event.price.required}", groups = OnCreate.class)
    @ValidPrice(min = 0.0, max = 999999999.99, allowZero = true, 
                message = "{validation.price.invalid}", groups = {OnCreate.class, OnUpdate.class})
    @Schema(description = "Precio de la entrada", example = "80000.00", required = true)
    private Double price;

    @NotBlank(message = "{validation.event.category.required}", groups = OnCreate.class)
    @Size(min = 3, max = 100, message = "{validation.event.category.size}", groups = {OnCreate.class, OnUpdate.class})
    @Schema(description = "Categoría del evento", example = "Concierto", required = true)
    private String categoria;

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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
