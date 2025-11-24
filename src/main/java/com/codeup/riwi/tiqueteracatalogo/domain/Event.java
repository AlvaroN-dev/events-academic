package com.codeup.riwi.tiqueteracatalogo.domain;

import java.time.LocalDateTime;

public class Event {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private Long venueId;
    private Integer capacity;
    private Double price;

    // Constructor
    public Event() {
    }

    public Event(Long id, String name, String description, LocalDateTime eventDate,
                    Long venueId, Integer capacity, Double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.venueId = venueId;
        this.capacity = capacity;
        this.price = price;
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
}
