package com.codeup.riwi.tiqueteracatalogo.domain.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Pure domain model representing an Event.
 * No framework dependencies - just business logic.
 * 
 * Domain Relationships:
 * - ManyToOne with Venue: An event belongs to exactly one venue
 * - ManyToMany with Category: An event can have multiple categories/tags
 * 
 * Note: In hexagonal architecture, domain models are pure Java objects.
 * The actual JPA relationships are defined in infrastructure layer entities.
 */
public class Evento {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String categoria;
    private EventStatus status = EventStatus.ACTIVE; // Default to active
    private Long venueId;
    private Venue venue; // Reference to the venue domain object (for rich domain model)
    private Set<Category> categories = new HashSet<>(); // ManyToMany with categories
    private Integer capacity;
    private Double price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Evento() {
        this.categories = new HashSet<>();
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
        this.categories = new HashSet<>();
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

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status != null ? status : EventStatus.ACTIVE;
    }

    /**
     * Check if event is active
     * @return true if status is ACTIVE
     */
    public boolean isActive() {
        return status == EventStatus.ACTIVE;
    }

    /**
     * Check if event is cancelled
     * @return true if status is CANCELLED
     */
    public boolean isCancelled() {
        return status == EventStatus.CANCELLED;
    }

    /**
     * Cancel the event
     */
    public void cancel() {
        this.status = EventStatus.CANCELLED;
    }

    /**
     * Mark event as completed
     */
    public void complete() {
        this.status = EventStatus.COMPLETED;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
        if (venue != null) {
            this.venueId = venue.getId();
        }
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories != null ? categories : new HashSet<>();
    }

    /**
     * Add a category to this event
     * @param category Category to add
     */
    public void addCategory(Category category) {
        if (categories == null) {
            categories = new HashSet<>();
        }
        categories.add(category);
    }

    /**
     * Remove a category from this event
     * @param category Category to remove
     */
    public void removeCategory(Category category) {
        if (categories != null) {
            categories.remove(category);
        }
    }

    /**
     * Check if event has a specific category
     * @param categoryName Category name to check
     * @return true if event has the category
     */
    public boolean hasCategory(String categoryName) {
        return categories != null && categories.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(categoryName));
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
