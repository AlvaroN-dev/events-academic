package com.codeup.riwi.tiqueteracatalogo.infrastructure.adapters;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Evento;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.EventoRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.EventoJpaEntity;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories.EventoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter that implements EventoRepositoryPort using JPA.
 */
@Component
public class EventoRepositoryAdapter implements EventoRepositoryPort {

    private final EventoJpaRepository jpaRepository;

    public EventoRepositoryAdapter(EventoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
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

    // Mapper methods
    private Evento toDomain(EventoJpaEntity entity) {
        if (entity == null)
            return null;

        Evento evento = new Evento();
        evento.setId(entity.getId());
        evento.setName(entity.getName());
        evento.setDescription(entity.getDescription());
        evento.setEventDate(entity.getEventDate());
        evento.setCategoria(entity.getCategoria());
        evento.setVenueId(entity.getVenueId());
        evento.setCapacity(entity.getCapacity());
        evento.setPrice(entity.getPrice());
        evento.setCreatedAt(entity.getCreatedAt());
        evento.setUpdatedAt(entity.getUpdatedAt());

        return evento;
    }

    private EventoJpaEntity toEntity(Evento evento) {
        if (evento == null)
            return null;

        EventoJpaEntity entity = new EventoJpaEntity();
        entity.setId(evento.getId());
        entity.setName(evento.getName());
        entity.setDescription(evento.getDescription());
        entity.setEventDate(evento.getEventDate()); // ✅ FIXED: was evento.setEventDate
        entity.setCategoria(evento.getCategoria());
        entity.setVenueId(evento.getVenueId());
        entity.setCapacity(evento.getCapacity());
        entity.setPrice(evento.getPrice());
        entity.setCreatedAt(evento.getCreatedAt());
        entity.setUpdatedAt(evento.getUpdatedAt());

        return entity;
    }
}
