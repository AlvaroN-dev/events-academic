package com.codeup.riwi.tiqueteracatalogo.repository;

import com.codeup.riwi.tiqueteracatalogo.domain.entity.EventoEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repositorio para gestionar EventoEntity en memoria.
 * Simula persistencia sin base de datos real.
 */
@Repository
public class EventoRepository {
    
    // Almacenamiento en memoria
    private final List<EventoEntity> eventos = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Obtiene todos los eventos
     * @return Lista de eventos
     */
    public List<EventoEntity> findAll() {
        return new ArrayList<>(eventos);
    }

    /**
     * Busca un evento por ID
     * @param id ID del evento
     * @return Optional con evento si existe
     */
    public Optional<EventoEntity> findById(Long id) {
        return eventos.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    /**
     * Busca eventos por venue ID
     * @param venueId ID del venue
     * @return Lista de eventos del venue
     */
    public List<EventoEntity> findByVenueId(Long venueId) {
        return eventos.stream()
                .filter(e -> e.getVenueId().equals(venueId))
                .toList();
    }

    /**
     * Guarda un nuevo evento
     * @param evento Evento a guardar
     * @return Evento guardado con ID
     */
    public EventoEntity save(EventoEntity evento) {
        if (evento.getId() == null) {
            evento.setId(idGenerator.getAndIncrement());
        }
        eventos.add(evento);
        return evento;
    }

    /**
     * Actualiza un evento existente
     * @param evento Evento a actualizar
     * @return Evento actualizado
     */
    public EventoEntity update(EventoEntity evento) {
        Optional<EventoEntity> existing = findById(evento.getId());
        if (existing.isPresent()) {
            EventoEntity e = existing.get();
            e.setName(evento.getName());
            e.setDescription(evento.getDescription());
            e.setEventDate(evento.getEventDate());
            e.setVenueId(evento.getVenueId());
            e.setCapacity(evento.getCapacity());
            e.setPrice(evento.getPrice());
            return e;
        }
        return null;
    }

    /**
     * Elimina evento por ID
     * @param id ID del evento
     * @return true si eliminó
     */
    public boolean deleteById(Long id) {
        return eventos.removeIf(e -> e.getId().equals(id));
    }

    /**
     * Verifica existencia por ID
     * @param id ID a verificar
     * @return true si existe
     */
    public boolean existsById(Long id) {
        return eventos.stream().anyMatch(e -> e.getId().equals(id));
    }

    /**
     * Cuenta total de eventos
     * @return Número de eventos
     */
    public long count() {
        return eventos.size();
    }
}
