package com.codeup.riwi.tiqueteracatalogo.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;


@Schema(description = "Respuesta con información completa del evento")
public class EventoResponse {

    @Schema(description = "ID único del evento", example = "1")
    private Long id;

    @Schema(description = "Nombre del evento", example = "Concierto Rock 2025")
    private String name;

    @Schema(description = "Descripción del evento", example = "Gran concierto de rock")
    private String description;

    @Schema(description = "Fecha y hora del evento", example = "2025-12-15T20:00:00")
    private LocalDateTime eventDate;

    @Schema(description = "ID del venue asociado", example = "1")
    private Long venueId;

    @Schema(description = "Capacidad del evento", example = "1000")
    private Integer capacity;

    @Schema(description = "Precio de la entrada", example = "80000.00")
    private Double price;

    @Schema(description = "Categoría del evento", example = "Concierto")
    private String categoria;

    // Constructores
    public EventoResponse() {
    }

    public EventoResponse(Long id, String name, String description, LocalDateTime eventDate,
            Long venueId, Integer capacity, Double price, String categoria) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.venueId = venueId;
        this.capacity = capacity;
        this.price = price;
        this.categoria = categoria;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
