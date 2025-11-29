package com.codeup.riwi.tiqueteracatalogo.application.services;

import com.codeup.riwi.tiqueteracatalogo.application.usecases.venue.*;
import com.codeup.riwi.tiqueteracatalogo.domain.models.Venue;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application Service for Venue operations.
 * Orchestrates use cases and provides a unified interface for the presentation
 * layer.
 * This follows the hexagonal architecture pattern.
 */
@Service
public class VenueService {

    private final CrearVenueUseCase crearVenueUseCase;
    private final ObtenerVenueUseCase obtenerVenueUseCase;
    private final ListarVenuesUseCase listarVenuesUseCase;
    private final ActualizarVenueUseCase actualizarVenueUseCase;
    private final EliminarVenueUseCase eliminarVenueUseCase;

    public VenueService(
            CrearVenueUseCase crearVenueUseCase,
            ObtenerVenueUseCase obtenerVenueUseCase,
            ListarVenuesUseCase listarVenuesUseCase,
            ActualizarVenueUseCase actualizarVenueUseCase,
            EliminarVenueUseCase eliminarVenueUseCase) {
        this.crearVenueUseCase = crearVenueUseCase;
        this.obtenerVenueUseCase = obtenerVenueUseCase;
        this.listarVenuesUseCase = listarVenuesUseCase;
        this.actualizarVenueUseCase = actualizarVenueUseCase;
        this.eliminarVenueUseCase = eliminarVenueUseCase;
    }

    public Venue crear(Venue venue) {
        return crearVenueUseCase.ejecutar(venue);
    }

    public Venue obtenerPorId(Long id) {
        return obtenerVenueUseCase.ejecutar(id);
    }

    public List<Venue> listarTodos() {
        return listarVenuesUseCase.ejecutar();
    }

    public long contar() {
        return listarVenuesUseCase.contar();
    }

    public Venue actualizar(Long id, Venue venue) {
        return actualizarVenueUseCase.ejecutar(id, venue);
    }

    public void eliminar(Long id) {
        eliminarVenueUseCase.ejecutar(id);
    }
}
