package com.codeup.riwi.tiqueteracatalogo.application.mapper;

import com.codeup.riwi.tiqueteracatalogo.domain.models.Evento;
import com.codeup.riwi.tiqueteracatalogo.application.dto.EventoRequest;
import com.codeup.riwi.tiqueteracatalogo.application.dto.EventoResponse;

/**
 * Mapper to convert between Evento domain model and DTOs.
 */
public class EventoMapper {

    public static Evento toEntity(EventoRequest request) {
        if (request == null)
            return null;

        // Debug logging
        System.out.println("DEBUG - EventoMapper.toEntity:");
        System.out.println("  name: " + request.getName());
        System.out.println("  eventDate: " + request.getEventDate());
        System.out.println("  categoria: " + request.getCategoria());

        Evento evento = new Evento();
        evento.setName(request.getName());
        evento.setDescription(request.getDescription());
        evento.setEventDate(request.getEventDate());
        evento.setCategoria(request.getCategoria());
        evento.setVenueId(request.getVenueId());
        evento.setCapacity(request.getCapacity());
        evento.setPrice(request.getPrice());

        return evento;
    }

    public static EventoResponse toResponse(Evento evento) {
        if (evento == null)
            return null;

        EventoResponse response = new EventoResponse();
        response.setId(evento.getId());
        response.setName(evento.getName());
        response.setDescription(evento.getDescription());
        response.setEventDate(evento.getEventDate());
        response.setCategoria(evento.getCategoria());
        response.setVenueId(evento.getVenueId());
        response.setCapacity(evento.getCapacity());
        response.setPrice(evento.getPrice());

        return response;
    }
}
