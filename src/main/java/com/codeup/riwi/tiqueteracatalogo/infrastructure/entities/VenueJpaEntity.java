package com.codeup.riwi.tiqueteracatalogo.infrastructure.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity for Venue persistence.
 * This is the infrastructure layer - contains JPA annotations.
 * Maps to 'venue' table in database.
 * 
 * Relationship: OneToMany with EventoJpaEntity (Venue owns many Events)
 * - mappedBy: The relationship is managed by EventoJpaEntity.venue
 * - cascade: PERSIST and MERGE to propagate save operations
 * - orphanRemoval: false - Events can exist independently
 * - fetch: LAZY to avoid loading all events when fetching a venue
 */
@Entity
@Table(name = "venue")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 300)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false)
    private Integer capacity;

    /**
     * OneToMany relationship with EventoJpaEntity.
     * - mappedBy: Indicates that EventoJpaEntity.venue is the owning side
     * - cascade: CascadeType.ALL propagates all operations (persist, merge, remove, etc.)
     * - orphanRemoval: true - When an event is removed from this list, it gets deleted
     * - fetch: LAZY - Events are loaded only when explicitly accessed (performance optimization)
     * - BatchSize: Optimizes N+1 by loading events in batches of 20 entities
     * - Fetch SUBSELECT: Alternative strategy - uses a subselect for loading all collections
     */
    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 20) // N+1 optimization: loads events in batches
    @Fetch(FetchMode.SUBSELECT) // Uses subselect for all pending initializations
    @ToString.Exclude // Avoid infinite recursion with Lombok toString
    @EqualsAndHashCode.Exclude // Avoid issues with lazy loading in equals/hashCode
    private List<EventoJpaEntity> eventos = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // ============ Helper methods for bidirectional relationship management ============

    /**
     * Add an event to this venue (maintains bidirectional consistency)
     * @param evento Event to add
     */
    public void addEvento(EventoJpaEntity evento) {
        eventos.add(evento);
        evento.setVenue(this);
    }

    /**
     * Remove an event from this venue (maintains bidirectional consistency)
     * @param evento Event to remove
     */
    public void removeEvento(EventoJpaEntity evento) {
        eventos.remove(evento);
        evento.setVenue(null);
    }
}
