package com.codeup.riwi.tiqueteracatalogo.domain.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Pure domain model representing an Event.
 * No framework dependencies - just business logic.
 */
public class Evento {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String categoria;
    private Long venueId;
    private Integer capacity;
    private Double price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Evento() {
    }

    // Full constructor
    public Evento(Long id, String name, String description, LocalDateTime eventDate,
            String categoria, Long venueId, Integer capacity, Double price,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.categoria = categoria;
        this.venueId = venueId;
        this.capacity = capacity;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Evento evento = (Evento) o;
        return Objects.equals(id, evento.id) &&
                Objects.equals(name, evento.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", eventDate=" + eventDate +
                ", categoria='" + categoria + '\'' +
                ", venueId=" + venueId +
                '}';
    }
}
