package com.codeup.riwi.tiqueteracatalogo.infrastructure.entities;

import com.codeup.riwi.tiqueteracatalogo.domain.models.EventStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA Entity for Event persistence.
 * This is the infrastructure layer - contains JPA annotations.
 * Maps to 'evento' table in database.
 * 
 * Relationships:
 * - ManyToOne with VenueJpaEntity (Event belongs to one Venue)
 * - ManyToMany with CategoryJpaEntity (Event can have multiple categories)
 * 
 * Entity Lifecycle considerations:
 * - persist: When persisting an Event, the Venue must already exist
 * - merge: Updates propagate to the Event, not to the Venue
 * - detach: Event becomes detached, lazy collections won't load
 * - remove: Only removes the Event, Venue remains (no cascade on Venue side)
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

    /**
     * Event status for lifecycle management.
     * - ACTIVE: Event is scheduled and tickets can be sold
     * - CANCELLED: Event has been cancelled
     * - POSTPONED: Event is postponed
     * - COMPLETED: Event has already taken place
     * - DRAFT: Event is not yet published
     * - SOLD_OUT: All tickets are sold
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatus status = EventStatus.ACTIVE;

    /**
     * ManyToOne relationship with VenueJpaEntity.
     * - This is the OWNING side of the relationship (has the foreign key)
     * - fetch: LAZY - Venue is loaded only when explicitly accessed (performance)
     * - optional: false - An event MUST belong to a venue
     * - JoinColumn: Creates 'venue_id' foreign key column
     * 
     * Cascade behavior: No cascade - Venue lifecycle is independent
     * If Venue is deleted, events should be handled explicitly (business rule)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_evento_venue"))
    @ToString.Exclude // Avoid infinite recursion
    @EqualsAndHashCode.Exclude // Avoid issues with lazy loading in equals/hashCode
    private VenueJpaEntity venue;

    /**
     * ManyToMany relationship with CategoryJpaEntity (Optional feature).
     * - This is the OWNING side - defines the join table
     * - fetch: LAZY - Categories loaded only when accessed
     * - cascade: PERSIST and MERGE - When saving event, save new categories too
     * - BatchSize: Optimizes N+1 by loading categories in batches
     * 
     * Join table: evento_category
     * - evento_id: Foreign key to evento table
     * - category_id: Foreign key to category table
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "evento_category",
            joinColumns = @JoinColumn(name = "evento_id", foreignKey = @ForeignKey(name = "fk_evento_category_evento")),
            inverseJoinColumns = @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_evento_category_category"))
    )
    @BatchSize(size = 20) // Optimizes N+1: loads categories in batches of 20
    @Fetch(FetchMode.SUBSELECT) // Alternative N+1 solution: uses subselect for all collections
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CategoryJpaEntity> categories = new HashSet<>();

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

    // ============ Helper methods for relationship management ============

    /**
     * Get the venue ID without loading the full Venue entity (LAZY optimization)
     * @return Venue ID or null
     */
    public Long getVenueId() {
        return venue != null ? venue.getId() : null;
    }

    /**
     * Add a category to this event (maintains bidirectional consistency)
     * @param category Category to add
     */
    public void addCategory(CategoryJpaEntity category) {
        categories.add(category);
        category.getEventos().add(this);
    }

    /**
     * Remove a category from this event (maintains bidirectional consistency)
     * @param category Category to remove
     */
    public void removeCategory(CategoryJpaEntity category) {
        categories.remove(category);
        category.getEventos().remove(this);
    }
}
