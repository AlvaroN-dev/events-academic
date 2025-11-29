package com.codeup.riwi.tiqueteracatalogo.application.dto;

import com.codeup.riwi.tiqueteracatalogo.application.validation.ValidationGroups.OnCreate;
import com.codeup.riwi.tiqueteracatalogo.application.validation.ValidationGroups.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * DTO for creating or updating a Venue.
 * Includes advanced Bean Validation with validation groups for different scenarios.
 * 
 * <p>Validation Groups:</p>
 * <ul>
 *   <li>{@link OnCreate} - Validations applied when creating a new venue</li>
 *   <li>{@link OnUpdate} - Validations applied when updating an existing venue</li>
 * </ul>
 * 
 * @author TiqueteraCatalogo Team
 * @version 2.0
 */
@Schema(description = "Datos de entrada para crear o actualizar un venue")
public class VenueRequest {

    @NotBlank(message = "{validation.venue.name.required}", groups = {OnCreate.class, OnUpdate.class})
    @Size(min = 3, max = 200, message = "{validation.venue.name.size}", groups = {OnCreate.class, OnUpdate.class})
    @Schema(description = "Nombre del venue", example = "Teatro Nacional", required = true)
    private String name;

    @NotBlank(message = "{validation.venue.address.required}", groups = OnCreate.class)
    @Size(min = 5, max = 300, message = "{validation.venue.address.size}", groups = {OnCreate.class, OnUpdate.class})
    @Schema(description = "Dirección completa del venue", example = "Calle 71 #10-25", required = true)
    private String address;

    @NotBlank(message = "{validation.venue.city.required}", groups = OnCreate.class)
    @Size(min = 2, max = 100, message = "{validation.venue.city.size}", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s\\-]+$", 
             message = "{validation.venue.city.pattern}", 
             groups = {OnCreate.class, OnUpdate.class})
    @Schema(description = "Ciudad donde se encuentra el venue", example = "Bogotá", required = true)
    private String city;

    @NotBlank(message = "{validation.venue.country.required}", groups = OnCreate.class)
    @Size(min = 2, max = 100, message = "{validation.venue.country.size}", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s\\-]+$", 
             message = "{validation.venue.country.pattern}", 
             groups = {OnCreate.class, OnUpdate.class})
    @Schema(description = "País donde se encuentra el venue", example = "Colombia", required = true)
    private String country;

    @NotNull(message = "{validation.venue.capacity.required}", groups = OnCreate.class)
    @Positive(message = "{validation.venue.capacity.positive}", groups = {OnCreate.class, OnUpdate.class})
    @Max(value = 1000000, message = "{validation.venue.capacity.max}", groups = {OnCreate.class, OnUpdate.class})
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
