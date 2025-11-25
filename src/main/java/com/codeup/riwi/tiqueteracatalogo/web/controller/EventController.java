package com.codeup.riwi.tiqueteracatalogo.web.controller;

import com.codeup.riwi.tiqueteracatalogo.web.dto.EventoRequest;
import com.codeup.riwi.tiqueteracatalogo.web.dto.EventoResponse;
import com.codeup.riwi.tiqueteracatalogo.web.advice.ErrorResponse;
import com.codeup.riwi.tiqueteracatalogo.web.advice.ResourceNotFoundException;
import com.codeup.riwi.tiqueteracatalogo.services.IEventoService;
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

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "API para gestión de eventos")
public class EventController {

        private final IEventoService eventoService;

        public EventController(IEventoService eventoService) {
                this.eventoService = eventoService;
        }

        @Operation(summary = "Obtener todos los eventos", description = "Retorna una lista completa de todos los eventos registrados en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de eventos obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventoResponse.class), examples = @ExampleObject(value = "[{\"id\":1,\"name\":\"Concierto Rock\",\"description\":\"Gran concierto\",\"eventDate\":\"2025-12-15T20:00:00\",\"venueId\":1,\"capacity\":1000,\"price\":80000.0}]")))
        })
        @GetMapping
        public ResponseEntity<List<EventoResponse>> getAllEvents() {
                List<EventoResponse> events = eventoService.getAllEventos();
                return ResponseEntity.ok(events);
        }

        @Operation(summary = "Obtener evento por ID", description = "Retorna un evento específico buscado por su identificador único")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Evento encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventoResponse.class), examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Concierto Rock\",\"description\":\"Gran concierto\",\"eventDate\":\"2025-12-15T20:00:00\",\"venueId\":1,\"capacity\":1000,\"price\":80000.0}"))),
                        @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"timestamp\":\"2025-10-28T10:30:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Evento con ID 999 no encontrado\",\"path\":\"/api/events/999\"}")))
        })
        @GetMapping("/{id}")
        public ResponseEntity<EventoResponse> getEventById(
                        @Parameter(description = "ID del evento a buscar", required = true, example = "1") @PathVariable Long id) {
                return eventoService.getEventoById(id)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
        }

        @Operation(summary = "Crear nuevo evento", description = "Crea un nuevo evento en el sistema con la información proporcionada")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Evento creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventoResponse.class), examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Concierto Rock\",\"description\":\"Gran concierto\",\"eventDate\":\"2025-12-15T20:00:00\",\"venueId\":1,\"capacity\":1000,\"price\":80000.0}"))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"timestamp\":\"2025-10-28T10:30:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Error de validación en los datos enviados\",\"path\":\"/api/events\",\"details\":[\"name: El nombre del evento es obligatorio\"]}")))
        })
        @PostMapping
        public ResponseEntity<EventoResponse> createEvent(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del evento a crear", required = true, content = @Content(schema = @Schema(implementation = EventoRequest.class), examples = @ExampleObject(value = "{\"name\":\"Concierto Rock\",\"description\":\"Gran concierto de rock\",\"eventDate\":\"2025-12-15T20:00:00\",\"venueId\":1,\"capacity\":1000,\"price\":80000.00}"))) @Valid @RequestBody EventoRequest request) {
                EventoResponse created = eventoService.createEvento(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }

        @Operation(summary = "Actualizar evento existente", description = "Actualiza completamente la información de un evento existente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Evento actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventoResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PutMapping("/{id}")
        public ResponseEntity<EventoResponse> updateEvent(
                        @Parameter(description = "ID del evento a actualizar", required = true, example = "1") @PathVariable Long id,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos del evento", required = true, content = @Content(schema = @Schema(implementation = EventoRequest.class), examples = @ExampleObject(value = "{\"name\":\"Concierto Rock Actualizado\",\"description\":\"Descripción actualizada\",\"eventDate\":\"2025-12-15T20:00:00\",\"venueId\":1,\"capacity\":1200,\"price\":90000.00}"))) @Valid @RequestBody EventoRequest request) {
                return eventoService.updateEvento(id, request)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
        }

        @Operation(summary = "Eliminar evento", description = "Elimina permanentemente un evento del sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Evento eliminado exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteEvent(
                        @Parameter(description = "ID del evento a eliminar", required = true, example = "1") @PathVariable Long id) {
                boolean deleted = eventoService.deleteEvento(id);
                if (!deleted) {
                        throw new ResourceNotFoundException("Evento", id);
                }
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Obtener eventos por venue", description = "Retorna todos los eventos programados para un venue específico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de eventos obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventoResponse.class)))
        })
        @GetMapping("/venue/{venueId}")
        public ResponseEntity<List<EventoResponse>> getEventsByVenueId(
                        @Parameter(description = "ID del venue", required = true, example = "1") @PathVariable Long venueId) {
                List<EventoResponse> events = eventoService.getEventosByVenueId(venueId);
                return ResponseEntity.ok(events);
        }

        @Operation(summary = "Obtener eventos paginados con filtros", description = "Retorna una página de eventos con filtros opcionales por ciudad, categoría y fecha de inicio. "
                        +
                        "Soporta paginación y ordenamiento mediante parámetros estándar de Spring Data.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Página de eventos obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = org.springframework.data.domain.Page.class))),
                        @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/paginated")
        public ResponseEntity<org.springframework.data.domain.Page<EventoResponse>> getEventosPaginados(
                        @Parameter(description = "Filtro por ciudad del venue (opcional)", example = "Bogotá") @RequestParam(required = false) String ciudad,

                        @Parameter(description = "Filtro por categoría del evento (opcional)", example = "Concierto") @RequestParam(required = false) String categoria,

                        @Parameter(description = "Filtro por fecha de inicio (eventos después de esta fecha, opcional)", example = "2025-12-01T00:00:00") @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime fechaInicio,

                        @Parameter(description = "Número de página (inicia en 0)", example = "0") @RequestParam(defaultValue = "0") int page,

                        @Parameter(description = "Tamaño de página", example = "10") @RequestParam(defaultValue = "10") int size,

                        @Parameter(description = "Campo de ordenamiento", example = "eventDate") @RequestParam(defaultValue = "eventDate") String sort,

                        @Parameter(description = "Dirección de ordenamiento (asc/desc)", example = "desc") @RequestParam(defaultValue = "desc") String direction) {
                org.springframework.data.domain.Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                                ? org.springframework.data.domain.Sort.Direction.ASC
                                : org.springframework.data.domain.Sort.Direction.DESC;

                org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page,
                                size, sortDirection, sort);

                org.springframework.data.domain.Page<EventoResponse> eventosPage = eventoService
                                .getEventosPaginados(ciudad, categoria, fechaInicio, pageable);

                return ResponseEntity.ok(eventosPage);
        }
}
