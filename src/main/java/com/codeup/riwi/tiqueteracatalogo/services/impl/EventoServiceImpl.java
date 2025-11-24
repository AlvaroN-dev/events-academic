package com.codeup.riwi.tiqueteracatalogo.services.impl;

import com.codeup.riwi.tiqueteracatalogo.web.dto.EventoRequest;
import com.codeup.riwi.tiqueteracatalogo.web.dto.EventoResponse;
import com.codeup.riwi.tiqueteracatalogo.domain.entity.EventoEntity;
import com.codeup.riwi.tiqueteracatalogo.domain.mapper.EventoMapper;
import com.codeup.riwi.tiqueteracatalogo.repository.EventoRepository;
import com.codeup.riwi.tiqueteracatalogo.services.IEventoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de lógica de negocio para Eventos.
 * Usa repositorio para persistencia y mapper para transformaciones.
 */
@Service
public class EventoServiceImpl implements IEventoService {

    private final EventoRepository eventoRepository;

    /**
     * Constructor con inyección de dependencias
     * @param eventoRepository Repositorio de eventos
     */
    public EventoServiceImpl(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Override
    public List<EventoResponse> getAllEventos() {
        return eventoRepository.findAll().stream()
                .map(EventoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EventoResponse> getEventoById(Long id) {
        return eventoRepository.findById(id)
                .map(EventoMapper::toResponse);
    }

    @Override
    public EventoResponse createEvento(EventoRequest request) {
        EventoEntity entity = EventoMapper.toEntity(request);
        EventoEntity savedEntity = eventoRepository.save(entity);
        return EventoMapper.toResponse(savedEntity);
    }

    @Override
    public Optional<EventoResponse> updateEvento(Long id, EventoRequest request) {
        return eventoRepository.findById(id)
                .map(entity -> {
                    EventoMapper.updateEntityFromRequest(entity, request);
                    EventoEntity updated = eventoRepository.update(entity);
                    return EventoMapper.toResponse(updated);
                });
    }

    @Override
    public boolean deleteEvento(Long id) {
        return eventoRepository.deleteById(id);
    }

    @Override
    public List<EventoResponse> getEventosByVenueId(Long venueId) {
        return eventoRepository.findByVenueId(venueId).stream()
                .map(EventoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countEventos() {
        return eventoRepository.count();
    }
}
