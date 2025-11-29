package com.codeup.riwi.tiqueteracatalogo.infrastructure.controllers;

import com.codeup.riwi.tiqueteracatalogo.application.usecases.evento.*;
import com.codeup.riwi.tiqueteracatalogo.domain.models.Evento;
import com.codeup.riwi.tiqueteracatalogo.application.dto.EventoRequest;
import com.codeup.riwi.tiqueteracatalogo.application.dto.EventoResponse;
import com.codeup.riwi.tiqueteracatalogo.application.mapper.EventoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "API para gestión de eventos")
public class EventController {

	// Use cases from hexagonal architecture
	private final CrearEventoUseCase crearEventoUseCase;
	private final ObtenerEventoUseCase obtenerEventoUseCase;
	private final ListarEventosUseCase listarEventosUseCase;
	private final ActualizarEventoUseCase actualizarEventoUseCase;
	private final EliminarEventoUseCase eliminarEventoUseCase;

	public EventController(
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

	@Operation(summary = "Obtener todos los eventos", description = "Retorna una lista completa de todos los eventos registrados en el sistema")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de eventos obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventoResponse.class), examples = @ExampleObject(value = "[{\"id\":1,\"name\":\"Concierto Rock\",\"description\":\"Gran concierto\",\"eventDate\":\"2025-12-15T20:00:00\",\"venueId\":1,\"capacity\":1000,\"price\":80000.0}]")))
	})
	@GetMapping
	public ResponseEntity<List<EventoResponse>> getAllEvents() {
		List<Evento> eventos = listarEventosUseCase.ejecutar();
		List<EventoResponse> response = eventos.stream()
				.map(EventoMapper::toResponse)
				.collect(Collectors.toList());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Obtener evento por ID", description = "Retorna un evento específico buscado por su identificador único")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Evento encontrado exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = EventoResponse.class),
            examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Concierto Rock\",\"description\":\"Gran concierto\",\"eventDate\":\"2025-12-15T20:00:00\",\"venueId\":1,\"capacity\":1000,\"price\":80000.0}"))),
			@ApiResponse(responseCode = "404", description = "Evento no encontrado")
	})
	@GetMapping("/{id}")
	public ResponseEntity<EventoResponse> getEventById(
			@Parameter(description = "ID del evento a buscar", required = true, example = "1") @PathVariable Long id) {
		Evento evento = obtenerEventoUseCase.ejecutar(id);
		return ResponseEntity.ok(EventoMapper.toResponse(evento));
	}

	@Operation(summary = "Crear nuevo evento", description = "Crea un nuevo evento en el sistema con la información proporcionada")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Evento creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventoResponse.class), examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Concierto Rock\",\"description\":\"Gran concierto\",\"eventDate\":\"2025-12-15T20:00:00\",\"venueId\":1,\"capacity\":1000,\"price\":80000.0}"))),
			@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
	})
	@PostMapping
	public ResponseEntity<EventoResponse> createEvent(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del evento a crear", required = true, content = @Content(schema = @Schema(implementation = EventoRequest.class), examples = @ExampleObject(value = "{\"name\":\"Concierto Rock\",\"description\":\"Gran concierto de rock\",\"eventDate\":\"2025-12-15T20:00:00\",\"categoria\":\"Música\",\"venueId\":1,\"capacity\":1000,\"price\":80000.00}"))) @Valid @RequestBody EventoRequest request) {
		Evento evento = EventoMapper.toEntity(request);
		Evento created = crearEventoUseCase.ejecutar(evento);
		return ResponseEntity.status(HttpStatus.CREATED).body(EventoMapper.toResponse(created));
	}

	@Operation(summary = "Actualizar evento existente", description = "Actualiza completamente la información de un evento existente")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Evento actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventoResponse.class))),
			@ApiResponse(responseCode = "404", description = "Evento no encontrado"),
			@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
	})
	@PutMapping("/{id}")
	public ResponseEntity<EventoResponse> updateEvent(
			@Parameter(description = "ID del evento a actualizar", required = true, example = "1") @PathVariable Long id,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos del evento", required = true, content = @Content(schema = @Schema(implementation = EventoRequest.class), examples = @ExampleObject(value = "{\"name\":\"Concierto Rock Actualizado\",\"description\":\"Descripción actualizada\",\"eventDate\":\"2025-12-15T20:00:00\",\"categoria\":\"Música\",\"venueId\":1,\"capacity\":1200,\"price\":90000.00}"))) @Valid @RequestBody EventoRequest request) {
		Evento evento = EventoMapper.toEntity(request);
		Evento updated = actualizarEventoUseCase.ejecutar(id, evento);
		return ResponseEntity.ok(EventoMapper.toResponse(updated));
	}

	@Operation(summary = "Eliminar evento", description = "Elimina permanentemente un evento del sistema")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Evento eliminado exitosamente"),
			@ApiResponse(responseCode = "404", description = "Evento no encontrado")
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEvent(
			@Parameter(description = "ID del evento a eliminar", required = true, example = "1") @PathVariable Long id) {
		eliminarEventoUseCase.ejecutar(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Obtener eventos por venue", description = "Retorna todos los eventos programados para un venue específico")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de eventos obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventoResponse.class)))
	})
	@GetMapping("/venue/{venueId}")
	public ResponseEntity<List<EventoResponse>> getEventsByVenueId(
			@Parameter(description = "ID del venue", required = true, example = "1") @PathVariable Long venueId) {
		List<Evento> eventos = listarEventosUseCase.ejecutarPorVenue(venueId);
		List<EventoResponse> response = eventos.stream()
				.map(EventoMapper::toResponse)
				.collect(Collectors.toList());
		return ResponseEntity.ok(response);
	}
}
