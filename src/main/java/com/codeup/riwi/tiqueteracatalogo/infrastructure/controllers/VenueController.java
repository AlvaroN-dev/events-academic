package com.codeup.riwi.tiqueteracatalogo.infrastructure.controllers;

import com.codeup.riwi.tiqueteracatalogo.application.usecases.venue.*;
import com.codeup.riwi.tiqueteracatalogo.application.validation.ValidationGroups.OnCreate;
import com.codeup.riwi.tiqueteracatalogo.application.validation.ValidationGroups.OnUpdate;
import com.codeup.riwi.tiqueteracatalogo.domain.models.Venue;
import com.codeup.riwi.tiqueteracatalogo.application.dto.VenueRequest;
import com.codeup.riwi.tiqueteracatalogo.application.dto.VenueResponse;
import com.codeup.riwi.tiqueteracatalogo.application.mapper.VenueMapper;
import com.codeup.riwi.tiqueteracatalogo.infrastructure.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Venue management.
 * Provides CRUD operations for venues with advanced validation.
 * 
 * <p>This controller uses validation groups to apply different validations
 * for create and update operations:</p>
 * <ul>
 *   <li>{@link OnCreate} - Applied on POST requests (all fields required)</li>
 *   <li>{@link OnUpdate} - Applied on PUT requests (partial validation)</li>
 * </ul>
 * 
 * @author TiqueteraCatalogo Team
 * @version 2.0
 */
@RestController
@RequestMapping("/api/venues")
@Tag(name = "Venues", description = "API para gestión de venues/lugares")
@Validated
public class VenueController {

        // Use cases from hexagonal architecture
        private final CrearVenueUseCase crearVenueUseCase;
        private final ObtenerVenueUseCase obtenerVenueUseCase;
        private final ListarVenuesUseCase listarVenuesUseCase;
        private final ActualizarVenueUseCase actualizarVenueUseCase;
        private final EliminarVenueUseCase eliminarVenueUseCase;

        public VenueController(
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

        @Operation(summary = "Obtener todos los venues", description = "Retorna una lista completa de todos los venues registrados")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de venues obtenida exitosamente")
        })
        @GetMapping
        public ResponseEntity<List<VenueResponse>> getAllVenues() {
                List<Venue> venues = listarVenuesUseCase.ejecutar();
                List<VenueResponse> response = venues.stream()
                                .map(VenueMapper::toResponse)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Obtener venue por ID", description = "Retorna un venue específico buscado por su identificador único")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Venue encontrado exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Venue no encontrado",
                        content = @Content(mediaType = "application/problem+json",
                        schema = @Schema(implementation = ApiErrorResponse.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<VenueResponse> getVenueById(
                        @Parameter(description = "ID del venue a buscar", required = true, example = "1") 
                        @PathVariable @Positive(message = "{validation.id.positive}") Long id) {
                Venue venue = obtenerVenueUseCase.ejecutar(id);
                return ResponseEntity.ok(VenueMapper.toResponse(venue));
        }

        @Operation(summary = "Crear nuevo venue", 
                description = "Crea un nuevo venue en el sistema. Aplica validaciones estrictas para creación. Requiere rol ADMIN o MODERATOR.",
                security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Venue creado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                        content = @Content(mediaType = "application/problem+json",
                        schema = @Schema(implementation = ApiErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "No autenticado"),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado")
        })
        @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
        public ResponseEntity<VenueResponse> createVenue(
                        @Validated(OnCreate.class) @RequestBody VenueRequest request) {
                Venue venue = VenueMapper.toEntity(request);
                Venue created = crearVenueUseCase.ejecutar(venue);
                return ResponseEntity.status(HttpStatus.CREATED).body(VenueMapper.toResponse(created));
        }

        @Operation(summary = "Actualizar venue existente", 
                description = "Actualiza completamente la información de un venue existente. Aplica validaciones flexibles para actualización. Requiere rol ADMIN o MODERATOR.",
                security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Venue actualizado exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Venue no encontrado",
                        content = @Content(mediaType = "application/problem+json",
                        schema = @Schema(implementation = ApiErrorResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                        content = @Content(mediaType = "application/problem+json",
                        schema = @Schema(implementation = ApiErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "No autenticado"),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado")
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
        public ResponseEntity<VenueResponse> updateVenue(
                        @Parameter(description = "ID del venue a actualizar", required = true, example = "1") 
                        @PathVariable @Positive(message = "{validation.id.positive}") Long id,
                        @Validated(OnUpdate.class) @RequestBody VenueRequest request) {
                Venue venue = VenueMapper.toEntity(request);
                Venue updated = actualizarVenueUseCase.ejecutar(id, venue);
                return ResponseEntity.ok(VenueMapper.toResponse(updated));
        }

        @Operation(summary = "Eliminar venue", 
                description = "Elimina permanentemente un venue del sistema. Requiere rol ADMIN.",
                security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Venue eliminado exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Venue no encontrado",
                        content = @Content(mediaType = "application/problem+json",
                        schema = @Schema(implementation = ApiErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "No autenticado"),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo ADMIN")
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> deleteVenue(
                        @Parameter(description = "ID del venue a eliminar", required = true, example = "1") 
                        @PathVariable @Positive(message = "{validation.id.positive}") Long id) {
                eliminarVenueUseCase.ejecutar(id);
                return ResponseEntity.noContent().build();
        }
}
