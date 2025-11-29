package com.codeup.riwi.tiqueteracatalogo.application.services;

import com.codeup.riwi.tiqueteracatalogo.application.usecases.evento.*;
import com.codeup.riwi.tiqueteracatalogo.domain.models.Evento;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application Service for Evento operations.
 * Orchestrates use cases and provides a unified interface for the presentation
 * layer.
 * This follows the hexagonal architecture pattern.
 */
@Service
public class EventoService {

    private final CrearEventoUseCase crearEventoUseCase;
    private final ObtenerEventoUseCase obtenerEventoUseCase;
    private final ListarEventosUseCase listarEventosUseCase;
    private final ActualizarEventoUseCase actualizarEventoUseCase;
    private final EliminarEventoUseCase eliminarEventoUseCase;

    public EventoService(
            CrearEventoUseCase crearEventoUseCase,
            ObtenerEventoUseCase obtenerEventoUseCase,
            ListarEventosUseCase listarEventosUseCase,
            ActualizarEventoUseCase actualizarEventoUseCase,
            EliminarEventoUseCase eliminarEventoUseCase) {
        this.crearEventoUseCase = crearEventoUseCase;
        this.obtenerEventoUseCase = obtenerEventoUseCase;
        this.listarEventosUseCase = listarEventosUseCase;
        this.actualizarEventoUseCase = actualizarEventoUseCase;
        this.eliminarEventoUseCase = eliminarEventoUseCase;
    }

    public Evento crear(Evento evento) {
        return crearEventoUseCase.ejecutar(evento);
    }

    public Evento obtenerPorId(Long id) {
        return obtenerEventoUseCase.ejecutar(id);
    }

    public List<Evento> listarTodos() {
        return listarEventosUseCase.ejecutar();
    }

    public List<Evento> listarPorVenue(Long venueId) {
        return listarEventosUseCase.ejecutarPorVenue(venueId);
    }

    public long contar() {
        return listarEventosUseCase.contar();
    }

    public Evento actualizar(Long id, Evento evento) {
        return actualizarEventoUseCase.ejecutar(id, evento);
    }

    public void eliminar(Long id) {
        eliminarEventoUseCase.ejecutar(id);
    }
}
