package com.codeup.riwi.tiqueteracatalogo.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * DTO for receiving Venue creation/update data.
 * Includes comprehensive validations and Swagger documentation.
 */
@Schema(description = "Datos de entrada para crear o actualizar un venue")
public class VenueRequest {

    @NotBlank(message = "El nombre del venue es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    @Schema(description = "Nombre del venue", example = "Teatro Nacional", required = true)
    private String name;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 5, max = 300, message = "La dirección debe tener entre 5 y 300 caracteres")
    @Schema(description = "Dirección completa del venue", example = "Calle 71 #10-25", required = true)
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(min = 2, max = 100, message = "La ciudad debe tener entre 2 y 100 caracteres")
    @Schema(description = "Ciudad donde se encuentra el venue", example = "Bogotá", required = true)
    private String city;

    @NotBlank(message = "El país es obligatorio")
    @Size(min = 2, max = 100, message = "El país debe tener entre 2 y 100 caracteres")
    @Schema(description = "País donde se encuentra el venue", example = "Colombia", required = true)
    private String country;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser mayor a 0")
    @Max(value = 1000000, message = "La capacidad no puede exceder 1,000,000 personas")
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
