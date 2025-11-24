package com.codeup.riwi.tiqueteracatalogo.repository;

import com.codeup.riwi.tiqueteracatalogo.domain.entity.VenueEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repositorio para gestionar VenueEntity en memoria.
 * Simula persistencia sin base de datos real.
 */
@Repository
public class VenueRepository {
    
    // Almacenamiento en memoria
    private final List<VenueEntity> venues = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Obtiene todos los venues
     * @return Lista de venues
     */
    public List<VenueEntity> findAll() {
        return new ArrayList<>(venues);
    }

    /**
     * Busca un venue por ID
     * @param id ID del venue
     * @return Optional con venue si existe
     */
    public Optional<VenueEntity> findById(Long id) {
        return venues.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }

    /**
     * Busca venues por ciudad
     * @param city Nombre de la ciudad
     * @return Lista de venues en la ciudad
     */
    public List<VenueEntity> findByCity(String city) {
        return venues.stream()
                .filter(v -> v.getCity().equalsIgnoreCase(city))
                .toList();
    }

    /**
     * Guarda un nuevo venue
     * @param venue Venue a guardar
     * @return Venue guardado con ID
     */
    public VenueEntity save(VenueEntity venue) {
        if (venue.getId() == null) {
            venue.setId(idGenerator.getAndIncrement());
        }
        venues.add(venue);
        return venue;
    }

    /**
     * Actualiza un venue existente
     * @param venue Venue a actualizar
     * @return Venue actualizado
     */
    public VenueEntity update(VenueEntity venue) {
        Optional<VenueEntity> existing = findById(venue.getId());
        if (existing.isPresent()) {
            VenueEntity v = existing.get();
            v.setName(venue.getName());
            v.setAddress(venue.getAddress());
            v.setCity(venue.getCity());
            v.setCountry(venue.getCountry());
            v.setCapacity(venue.getCapacity());
            return v;
        }
        return null;
    }

    /**
     * Elimina venue por ID
     * @param id ID del venue
     * @return true si eliminó
     */
    public boolean deleteById(Long id) {
        return venues.removeIf(v -> v.getId().equals(id));
    }

    /**
     * Verifica existencia por ID
     * @param id ID a verificar
     * @return true si existe
     */
    public boolean existsById(Long id) {
        return venues.stream().anyMatch(v -> v.getId().equals(id));
    }

    /**
     * Cuenta total de venues
     * @return Número de venues
     */
    public long count() {
        return venues.size();
    }
}
