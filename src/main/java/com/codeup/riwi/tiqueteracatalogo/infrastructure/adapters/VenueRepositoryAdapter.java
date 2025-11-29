package com.codeup.riwi.tiqueteracatalogo.infrastructure.adapters;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Venue;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.VenueRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.VenueJpaEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.VenueJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter that implements VenueRepositoryPort using JPA.
 * 
 * This adapter handles the translation between domain models and JPA entities,
 * properly managing the bidirectional OneToMany relationship with Events.
 * 
 * Note: Transaction management is handled at the Use Case layer (application layer)
 * following hexagonal architecture principles. This adapter only handles
 * entity mapping and delegates persistence to JPA repository.
 * 
 * Entity Lifecycle:
 * - persist: Creates new venue, events can be added via addEvento() method
 * - merge: Updates venue data, doesn't affect events unless explicitly modified
 * - remove: Deletes venue AND all associated events (due to cascade and orphanRemoval)
 */
@Component
public class VenueRepositoryAdapter implements VenueRepositoryPort {

    private final VenueJpaRepository jpaRepository;

    public VenueRepositoryAdapter(VenueJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Venue> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Venue> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Venue save(Venue venue) {
        VenueJpaEntity entity = toEntity(venue);
        VenueJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    /**
     * Delete venue by ID.
     * Due to cascade = CascadeType.ALL and orphanRemoval = true on VenueJpaEntity,
     * this will also delete all events associated with this venue.
     * 
     * @param id Venue ID to delete
     */
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Venue> findByCity(String city) {
        return jpaRepository.findByCityIgnoreCase(city).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    // ============ Mapper methods ============

    /**
     * Convert JPA entity to domain model.
     * Note: Events are NOT loaded here due to LAZY fetch.
     * Use findEventosByVenueId if you need events for a venue.
     */
    private Venue toDomain(VenueJpaEntity entity) {
        if (entity == null)
            return null;

        Venue venue = new Venue();
        venue.setId(entity.getId());
        venue.setName(entity.getName());
        venue.setAddress(entity.getAddress());
        venue.setCity(entity.getCity());
        venue.setCountry(entity.getCountry());
        venue.setCapacity(entity.getCapacity());
        venue.setCreatedAt(entity.getCreatedAt());
        venue.setUpdatedAt(entity.getUpdatedAt());
        
        // Note: Events are LAZY loaded - we don't load them here to avoid N+1
        // If events are needed, use EventoRepositoryPort.findByVenueId(venueId)

        return venue;
    }

    /**
     * Convert domain model to JPA entity.
     * Note: Events are managed separately through EventoRepositoryAdapter.
     */
    private VenueJpaEntity toEntity(Venue venue) {
        if (venue == null)
            return null;

        VenueJpaEntity entity = new VenueJpaEntity();
        entity.setId(venue.getId());
        entity.setName(venue.getName());
        entity.setAddress(venue.getAddress());
        entity.setCity(venue.getCity());
        entity.setCountry(venue.getCountry());
        entity.setCapacity(venue.getCapacity());
        entity.setCreatedAt(venue.getCreatedAt());
        entity.setUpdatedAt(venue.getUpdatedAt());
        
        // Note: Events are NOT set here - they are managed through their own repository
        // This maintains the separation of concerns and avoids complex bidirectional updates

        return entity;
    }
}
