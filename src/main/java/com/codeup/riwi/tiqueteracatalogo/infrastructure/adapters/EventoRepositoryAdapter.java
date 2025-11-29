package com.codeup.riwi.tiqueteracatalogo.infrastructure.adapters;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Category;
import com.codeup.riwi.tiqueteracatalogo.domain.models.Evento;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.EventoRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.CategoryJpaEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.EventoJpaEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.VenueJpaEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.EventoJpaRepository;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.VenueJpaRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapter that implements EventoRepositoryPort using JPA.
 * 
 * This adapter handles the translation between domain models and JPA entities,
 * properly managing the bidirectional relationships with Venue and Categories.
 * 
 * Note: Transaction management is handled at the Use Case layer (application layer)
 * following hexagonal architecture principles. This adapter only handles
 * entity mapping and delegates persistence to JPA repository.
 */
@Component
public class EventoRepositoryAdapter implements EventoRepositoryPort {

    private final EventoJpaRepository jpaRepository;
    private final VenueJpaRepository venueJpaRepository;

    public EventoRepositoryAdapter(EventoJpaRepository jpaRepository, 
                                   VenueJpaRepository venueJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.venueJpaRepository = venueJpaRepository;
    }

    @Override
    public List<Evento> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Evento> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Evento save(Evento evento) {
        EventoJpaEntity entity = toEntity(evento);
        EventoJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        return jpaRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public List<Evento> findByVenueId(Long venueId) {
        return jpaRepository.findByVenueId(venueId).stream()
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
     * Handles LAZY loaded relationships safely.
     */
    private Evento toDomain(EventoJpaEntity entity) {
        if (entity == null)
            return null;

        Evento evento = new Evento();
        evento.setId(entity.getId());
        evento.setName(entity.getName());
        evento.setDescription(entity.getDescription());
        evento.setEventDate(entity.getEventDate());
        evento.setCategoria(entity.getCategoria());
        
        // Get venueId from the ManyToOne relationship
        evento.setVenueId(entity.getVenueId());
        
        evento.setCapacity(entity.getCapacity());
        evento.setPrice(entity.getPrice());
        evento.setCreatedAt(entity.getCreatedAt());
        evento.setUpdatedAt(entity.getUpdatedAt());

        // Map categories (ManyToMany) - only if initialized
        if (entity.getCategories() != null) {
            Set<Category> categories = entity.getCategories().stream()
                    .map(this::categoryToDomain)
                    .collect(Collectors.toSet());
            evento.setCategories(categories);
        }

        return evento;
    }

    /**
     * Convert domain model to JPA entity.
     * Properly establishes the ManyToOne relationship with Venue.
     */
    private EventoJpaEntity toEntity(Evento evento) {
        if (evento == null)
            return null;

        EventoJpaEntity entity = new EventoJpaEntity();
        entity.setId(evento.getId());
        entity.setName(evento.getName());
        entity.setDescription(evento.getDescription());
        entity.setEventDate(evento.getEventDate());
        entity.setCategoria(evento.getCategoria());
        
        // Set the ManyToOne relationship with Venue
        if (evento.getVenueId() != null) {
            VenueJpaEntity venue = venueJpaRepository.findById(evento.getVenueId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Venue con ID " + evento.getVenueId() + " no encontrado"));
            entity.setVenue(venue);
        }
        
        entity.setCapacity(evento.getCapacity());
        entity.setPrice(evento.getPrice());
        entity.setCreatedAt(evento.getCreatedAt());
        entity.setUpdatedAt(evento.getUpdatedAt());

        // Map categories (ManyToMany)
        if (evento.getCategories() != null && !evento.getCategories().isEmpty()) {
            Set<CategoryJpaEntity> categoryEntities = evento.getCategories().stream()
                    .map(this::categoryToEntity)
                    .collect(Collectors.toSet());
            entity.setCategories(categoryEntities);
        }

        return entity;
    }

    /**
     * Convert Category JPA entity to domain model
     */
    private Category categoryToDomain(CategoryJpaEntity entity) {
        if (entity == null) return null;
        
        Category category = new Category();
        category.setId(entity.getId());
        category.setName(entity.getName());
        category.setDescription(entity.getDescription());
        category.setColor(entity.getColor());
        category.setActive(entity.getActive());
        category.setCreatedAt(entity.getCreatedAt());
        category.setUpdatedAt(entity.getUpdatedAt());
        return category;
    }

    /**
     * Convert Category domain model to JPA entity
     */
    private CategoryJpaEntity categoryToEntity(Category category) {
        if (category == null) return null;
        
        CategoryJpaEntity entity = new CategoryJpaEntity();
        entity.setId(category.getId());
        entity.setName(category.getName());
        entity.setDescription(category.getDescription());
        entity.setColor(category.getColor());
        entity.setActive(category.getActive());
        entity.setCreatedAt(category.getCreatedAt());
        entity.setUpdatedAt(category.getUpdatedAt());
        return entity;
    }
}
