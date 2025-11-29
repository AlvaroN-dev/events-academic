package com.codeup.riwi.tiqueteracatalogo.infrastructure.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA Entity for Category/Tag persistence.
 * This is the infrastructure layer - contains JPA annotations.
 * Maps to 'category' table in database.
 * 
 * Relationship: ManyToMany with EventoJpaEntity
 * - This is the INVERSE side of the relationship
 * - mappedBy: The relationship is managed by EventoJpaEntity.categories
 * - No cascade: Categories have independent lifecycle
 * - fetch: LAZY to avoid loading all events when fetching a category
 * 
 * Use cases:
 * - Categorize events (e.g., "Music", "Sports", "Conference")
 * - Filter events by category
 * - Events can belong to multiple categories
 */
@Entity
@Table(name = "category", uniqueConstraints = {
        @UniqueConstraint(name = "uk_category_name", columnNames = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String color; // For UI display (e.g., "#FF5733")

    @Column(nullable = false)
    private Boolean active = true;

    /**
     * ManyToMany relationship with EventoJpaEntity.
     * - This is the INVERSE side (mappedBy = "categories" in EventoJpaEntity)
     * - fetch: LAZY - Events loaded only when explicitly accessed
     * - No cascade: Category lifecycle is independent of events
     * 
     * Note: When a Category is removed, it will be automatically removed
     * from all associated events' categories collections (handled by JPA)
     */
    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @ToString.Exclude // Avoid infinite recursion with Lombok toString
    @EqualsAndHashCode.Exclude // Avoid issues with lazy loading in equals/hashCode
    private Set<EventoJpaEntity> eventos = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // ============ Convenience constructor ============

    public CategoryJpaEntity(String name, String description, String color) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.active = true;
    }

    // ============ Helper methods ============

    /**
     * Get the count of events in this category
     * @return Number of events
     */
    public int getEventCount() {
        return eventos != null ? eventos.size() : 0;
    }
}
