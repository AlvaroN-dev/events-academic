package com.codeup.riwi.tiqueteracatalogo.infrastructure.config;

import com.codeup.riwi.tiqueteracatalogo.application.usecases.evento.*;
import com.codeup.riwi.tiqueteracatalogo.application.usecases.venue.*;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.EventoRepositoryPort;
import com.codeup.riwi.tiqueteracatalogo.domain.ports.out.VenueRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to wire use cases with their dependencies.
 * This is where we inject the repository ports (implemented by adapters)
 * into the use cases.
 */
@Configuration
public class UseCaseConfiguration {

    // ==================== EVENT USE CASES ====================

    @Bean
    public CrearEventoUseCase crearEventoUseCase(
            EventoRepositoryPort eventoRepository,
            VenueRepositoryPort venueRepository) {
        return new CrearEventoUseCase(eventoRepository, venueRepository);
    }

    @Bean
    public ObtenerEventoUseCase obtenerEventoUseCase(EventoRepositoryPort eventoRepository) {
        return new ObtenerEventoUseCase(eventoRepository);
    }

    @Bean
    public ListarEventosUseCase listarEventosUseCase(EventoRepositoryPort eventoRepository) {
        return new ListarEventosUseCase(eventoRepository);
    }

    @Bean
    public ActualizarEventoUseCase actualizarEventoUseCase(
            EventoRepositoryPort eventoRepository,
            VenueRepositoryPort venueRepository) {
        return new ActualizarEventoUseCase(eventoRepository, venueRepository);
    }

    @Bean
    public EliminarEventoUseCase eliminarEventoUseCase(EventoRepositoryPort eventoRepository) {
        return new EliminarEventoUseCase(eventoRepository);
    }

    // ==================== VENUE USE CASES ====================

    @Bean
    public CrearVenueUseCase crearVenueUseCase(VenueRepositoryPort venueRepository) {
        return new CrearVenueUseCase(venueRepository);
    }

    @Bean
    public ObtenerVenueUseCase obtenerVenueUseCase(VenueRepositoryPort venueRepository) {
        return new ObtenerVenueUseCase(venueRepository);
    }

    @Bean
    public ListarVenuesUseCase listarVenuesUseCase(VenueRepositoryPort venueRepository) {
        return new ListarVenuesUseCase(venueRepository);
    }

    @Bean
    public ActualizarVenueUseCase actualizarVenueUseCase(VenueRepositoryPort venueRepository) {
        return new ActualizarVenueUseCase(venueRepository);
    }

    @Bean
    public EliminarVenueUseCase eliminarVenueUseCase(VenueRepositoryPort venueRepository) {
        return new EliminarVenueUseCase(venueRepository);
    }
}
