package com.codeup.riwi.tiqueteracatalogo.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * JPA Entity representing an Event.
 * Mapped to 'evento' table in database with unique name constraint.
 */
@Entity
@Table(name = "evento", uniqueConstraints = {
        @UniqueConstraint(name = "uk_evento_name", columnNames = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false, length = 100)
    private String categoria;

    // ManyToOne relationship with VenueEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false, foreignKey = @ForeignKey(name = "fk_evento_venue"))
    private VenueEntity venue;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Double price;

    // Audit timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper method to get venueId for backward compatibility with existing code
    @Transient
    public Long getVenueId() {
        return venue != null ? venue.getId() : null;
    }

    // Helper method to set venueId for backward compatibility
    public void setVenueId(Long venueId) {
        if (venueId != null) {
            if (this.venue == null) {
                this.venue = new VenueEntity();
            }
            this.venue.setId(venueId);
        } else {
            this.venue = null;
        }
    }
}
