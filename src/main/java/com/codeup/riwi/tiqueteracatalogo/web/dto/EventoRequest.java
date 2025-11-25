package com.codeup.riwi.tiqueteracatalogo.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * DTO for receiving Event creation/update data.
 * Includes comprehensive validations and Swagger documentation.
 */
@Schema(description = "Datos de entrada para crear o actualizar un evento")
public class EventoRequest {

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    @Schema(description = "Nombre del evento", example = "Concierto Rock 2025", required = true)
    private String name;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Schema(description = "Descripción detallada del evento", example = "Gran concierto de rock con bandas internacionales")
    private String description;

    @NotNull(message = "La fecha del evento es obligatoria")
    @Future(message = "La fecha del evento debe ser en el futuro")
    @Schema(description = "Fecha y hora del evento", example = "2025-12-15T20:00:00", required = true)
    private LocalDateTime eventDate;

    @NotNull(message = "El venue ID es obligatorio")
    @Positive(message = "El venue ID debe ser un número positivo")
    @Schema(description = "ID del venue donde se realizará el evento", example = "1", required = true)
    private Long venueId;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser mayor a 0")
    @Max(value = 1000000, message = "La capacidad no puede exceder 1,000,000 personas")
    @Schema(description = "Capacidad máxima del evento", example = "1000", required = true)
    private Integer capacity;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "999999999.99", message = "El precio no puede exceder 999,999,999.99")
    @Schema(description = "Precio de la entrada", example = "80000.00", required = true)
    private Double price;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(min = 3, max = 100, message = "La categoría debe tener entre 3 y 100 caracteres")
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
