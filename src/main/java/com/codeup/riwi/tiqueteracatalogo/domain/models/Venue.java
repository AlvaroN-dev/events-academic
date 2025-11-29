package com.codeup.riwi.tiqueteracatalogo.domain.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Pure domain model representing a Venue.
 * No framework dependencies - just business logic.
 * 
 * Domain Relationships:
 * - OneToMany with Evento: A venue can host many events
 * 
 * Note: In hexagonal architecture, domain models are pure Java objects.
 * The actual JPA relationships are defined in infrastructure layer entities.
 */
public class Venue {

    private Long id;
    private String name;
    private String address;
    private String city;
    private String country;
    private Integer capacity;
    private List<Evento> eventos = new ArrayList<>(); // OneToMany: Events hosted at this venue
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Venue() {
        this.eventos = new ArrayList<>();
    }

    // Full constructor
    public Venue(Long id, String name, String address, String city, String country,
            Integer capacity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.country = country;
        this.capacity = capacity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.eventos = new ArrayList<>();
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

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos != null ? eventos : new ArrayList<>();
    }

    /**
     * Add an event to this venue
     * @param evento Event to add
     */
    public void addEvento(Evento evento) {
        if (eventos == null) {
            eventos = new ArrayList<>();
        }
        eventos.add(evento);
        evento.setVenueId(this.id);
    }

    /**
     * Remove an event from this venue
     * @param evento Event to remove
     */
    public void removeEvento(Evento evento) {
        if (eventos != null) {
            eventos.remove(evento);
        }
    }

    /**
     * Get the count of events at this venue
     * @return Number of events
     */
    public int getEventCount() {
        return eventos != null ? eventos.size() : 0;
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
        Venue venue = (Venue) o;
        return Objects.equals(id, venue.id) &&
                Objects.equals(name, venue.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Venue{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
