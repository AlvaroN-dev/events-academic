package com.codeup.riwi.tiqueteracatalogo.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * DTO para recibir datos de creación/actualización de Venues.
 * Incluye validaciones y documentación Swagger.
 */
@Schema(description = "Datos de entrada para crear o actualizar un venue")
public class VenueRequest {
    
    @NotBlank(message = "El nombre del venue es obligatorio")
    @Schema(description = "Nombre del venue", example = "Teatro Nacional", required = true)
    private String name;

    @NotBlank(message = "La dirección es obligatoria")
    @Schema(description = "Dirección completa del venue", example = "Calle 71 #10-25", required = true)
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    @Schema(description = "Ciudad donde se encuentra el venue", example = "Bogotá", required = true)
    private String city;

    @NotBlank(message = "El país es obligatorio")
    @Schema(description = "País donde se encuentra el venue", example = "Colombia", required = true)
    private String country;

    @Positive(message = "La capacidad debe ser mayor a 0")
    @Schema(description = "Capacidad máxima del venue", example = "1500", required = true)
    private Integer capacity;

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
