package com.codeup.riwi.tiqueteracatalogo.infrastructure.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * JPA Entity for Event persistence.
 * This is the infrastructure layer - contains JPA annotations.
 * Maps to 'evento' table in database.
 */
@Entity
@Table(name = "evento", uniqueConstraints = {
        @UniqueConstraint(name = "uk_evento_name", columnNames = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, name = "event_date")
    private LocalDateTime eventDate;

    @Column(nullable = false, length = 100)
    private String categoria;

    @Column(nullable = false, name = "venue_id")
    private Long venueId;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Double price;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;
}
