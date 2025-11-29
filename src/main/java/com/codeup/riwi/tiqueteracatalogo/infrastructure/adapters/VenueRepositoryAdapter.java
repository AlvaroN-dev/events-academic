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

    // Mapper methods
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

        return venue;
    }

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

        return entity;
    }
}
