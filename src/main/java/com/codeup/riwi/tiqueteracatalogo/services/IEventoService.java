package com.codeup.riwi.tiqueteracatalogo.services;

import com.codeup.riwi.tiqueteracatalogo.web.dto.EventoRequest;
import com.codeup.riwi.tiqueteracatalogo.web.dto.EventoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de negocio para Eventos.
 * Separa el contrato de la implementación.
 */
public interface IEventoService {

    /**
     * Obtiene todos los eventos
     * 
     * @return Lista de respuestas de eventos
     */
    List<EventoResponse> getAllEventos();

    /**
     * Busca un evento por ID
     * 
     * @param id ID del evento
     * @return Optional con respuesta del evento
     */
    Optional<EventoResponse> getEventoById(Long id);

    /**
     * Crea un nuevo evento
     * 
     * @param request Datos del evento
     * @return Respuesta con evento creado
     */
    EventoResponse createEvento(EventoRequest request);

    /**
     * Actualiza un evento existente
     * 
     * @param id      ID del evento
     * @param request Nuevos datos
     * @return Optional con evento actualizado
     */
    Optional<EventoResponse> updateEvento(Long id, EventoRequest request);

    /**
     * Elimina un evento
     * 
     * @param id ID del evento
     * @return true si se eliminó
     */
    boolean deleteEvento(Long id);

    /**
     * Busca eventos por venue ID
     * 
     * @param venueId ID del venue
     * @return Lista de eventos del venue
     */
    List<EventoResponse> getEventosByVenueId(Long venueId);

    /**
     * Cuenta total de eventos
     * 
     * @return Número de eventos
     */
    long countEventos();

    /**
     * Obtiene eventos paginados con filtros opcionales
     * 
     * @param ciudad      Filtro por ciudad del venue (opcional)
     * @param categoria   Filtro por categoría del evento (opcional)
     * @param fechaInicio Filtro por fecha de inicio (opcional)
     * @param pageable    Parámetros de paginación y ordenamiento
     * @return Página de eventos filtrados
     */
    Page<EventoResponse> getEventosPaginados(String ciudad, String categoria, LocalDateTime fechaInicio,
            Pageable pageable);
}
