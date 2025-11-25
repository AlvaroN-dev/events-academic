package com.codeup.riwi.tiqueteracatalogo.domain.mapper;

import com.codeup.riwi.tiqueteracatalogo.web.dto.EventoRequest;
import com.codeup.riwi.tiqueteracatalogo.web.dto.EventoResponse;
import com.codeup.riwi.tiqueteracatalogo.domain.entity.EventoEntity;

/**
 * Mapper para convertir entre EventoEntity y DTOs (Request/Response).
 * Centraliza las transformaciones entre las capas de dominio y presentación.
 */
public class EventoMapper {

    /**
     * Convierte EventoRequest a EventoEntity.
     * Usado al crear o actualizar eventos.
     */
    public static EventoEntity toEntity(EventoRequest request) {
        EventoEntity entity = new EventoEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setEventDate(request.getEventDate());
        entity.setVenueId(request.getVenueId());
        entity.setCapacity(request.getCapacity());
        entity.setPrice(request.getPrice());
        entity.setCategoria(request.getCategoria());
        return entity;
    }

    /**
     * Convierte EventoEntity a EventoResponse.
     * Usado al devolver datos al cliente.
     */
    public static EventoResponse toResponse(EventoEntity entity) {
        return new EventoResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getEventDate(),
                entity.getVenueId(),
                entity.getCapacity(),
                entity.getPrice(),
                entity.getCategoria());
    }

    /**
     * Actualiza un EventoEntity existente con datos de EventoRequest.
     * Preserva el ID original.
     */
    public static void updateEntityFromRequest(EventoEntity entity, EventoRequest request) {
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setEventDate(request.getEventDate());
        entity.setVenueId(request.getVenueId());
        entity.setCapacity(request.getCapacity());
        entity.setPrice(request.getPrice());
        entity.setCategoria(request.getCategoria());
    }
}
