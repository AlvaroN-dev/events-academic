package com.codeup.riwi.tiqueteracatalogo.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Respuesta con información completa del venue")
public class VenueResponse {
    
    @Schema(description = "ID único del venue", example = "1")
    private Long id;

    @Schema(description = "Nombre del venue", example = "Teatro Nacional")
    private String name;

    @Schema(description = "Dirección del venue", example = "Calle 71 #10-25")
    private String address;

    @Schema(description = "Ciudad del venue", example = "Bogotá")
    private String city;

    @Schema(description = "País del venue", example = "Colombia")
    private String country;

    @Schema(description = "Capacidad del venue", example = "1500")
    private Integer capacity;

    // Constructores
    public VenueResponse() {
    }

    public VenueResponse(Long id, String name, String address, String city, String country, Integer capacity) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.country = country;
        this.capacity = capacity;
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
