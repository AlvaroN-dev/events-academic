package com.codeup.riwi.tiqueteracatalogo.services.impl;

import com.codeup.riwi.tiqueteracatalogo.web.dto.EventoRequest;
import com.codeup.riwi.tiqueteracatalogo.web.dto.EventoResponse;
import com.codeup.riwi.tiqueteracatalogo.domain.entity.EventoEntity;
import com.codeup.riwi.tiqueteracatalogo.domain.entity.VenueEntity;
import com.codeup.riwi.tiqueteracatalogo.domain.mapper.EventoMapper;
import com.codeup.riwi.tiqueteracatalogo.repository.EventoRepository;
import com.codeup.riwi.tiqueteracatalogo.repository.VenueRepository;
import com.codeup.riwi.tiqueteracatalogo.repository.specification.EventoSpecification;
import com.codeup.riwi.tiqueteracatalogo.services.IEventoService;
import com.codeup.riwi.tiqueteracatalogo.web.advice.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de lógica de negocio para Eventos.
 * Usa repositorio JPA para persistencia y mapper para transformaciones.
 */
@Service
@Transactional
public class EventoServiceImpl implements IEventoService {

    private final EventoRepository eventoRepository;
    private final VenueRepository venueRepository;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param eventoRepository Repositorio de eventos
     * @param venueRepository  Repositorio de venues
     */
    public EventoServiceImpl(EventoRepository eventoRepository, VenueRepository venueRepository) {
        this.eventoRepository = eventoRepository;
        this.venueRepository = venueRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoResponse> getAllEventos() {
        return eventoRepository.findAll().stream()
                .map(EventoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EventoResponse> getEventoById(Long id) {
        return eventoRepository.findById(id)
                .map(EventoMapper::toResponse);
    }

    @Override
    public EventoResponse createEvento(EventoRequest request) {
        // Validate unique name constraint
        if (eventoRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Ya existe un evento con el nombre: " + request.getName());
        }

        // Validate venue exists
        VenueEntity venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue", request.getVenueId()));

        EventoEntity entity = EventoMapper.toEntity(request);
        entity.setVenue(venue); // Set the actual venue entity
        EventoEntity savedEntity = eventoRepository.save(entity);
        return EventoMapper.toResponse(savedEntity);
    }

    @Override
    public Optional<EventoResponse> updateEvento(Long id, EventoRequest request) {
        return eventoRepository.findById(id)
                .map(entity -> {
                    // Validate unique name constraint (excluding current event)
                    if (eventoRepository.existsByNameAndIdNot(request.getName(), id)) {
                        throw new IllegalArgumentException("Ya existe otro evento con el nombre: " + request.getName());
                    }

                    // Validate venue exists
                    VenueEntity venue = venueRepository.findById(request.getVenueId())
                            .orElseThrow(() -> new ResourceNotFoundException("Venue", request.getVenueId()));

                    EventoMapper.updateEntityFromRequest(entity, request);
                    entity.setVenue(venue); // Update the venue relationship
                    EventoEntity updated = eventoRepository.save(entity);
                    return EventoMapper.toResponse(updated);
                });
    }

    @Override
    public boolean deleteEvento(Long id) {
        if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoResponse> getEventosByVenueId(Long venueId) {
        return eventoRepository.findByVenue_Id(venueId).stream()
                .map(EventoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countEventos() {
        return eventoRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventoResponse> getEventosPaginados(String ciudad, String categoria, LocalDateTime fechaInicio,
            Pageable pageable) {
        Specification<EventoEntity> spec = EventoSpecification.withFilters(ciudad, categoria, fechaInicio);
        Page<EventoEntity> eventosPage = eventoRepository.findAll(spec, pageable);
        return eventosPage.map(EventoMapper::toResponse);
    }
}
