package com.codeup.riwi.tiqueteracatalogo.web.controller;

import com.codeup.riwi.tiqueteracatalogo.web.dto.VenueRequest;
import com.codeup.riwi.tiqueteracatalogo.web.dto.VenueResponse;
import com.codeup.riwi.tiqueteracatalogo.web.advice.ErrorResponse;
import com.codeup.riwi.tiqueteracatalogo.web.advice.ResourceNotFoundException;
import com.codeup.riwi.tiqueteracatalogo.services.IVenueService;
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
@RequestMapping("/api/venues")
@Tag(name = "Venues", description = "API para gestión de lugares/venues")
public class VenueController {
        private final IVenueService venueService;

        public VenueController(IVenueService venueService) {
                this.venueService = venueService;
        }

        @Operation(summary = "Obtener todos los venues", description = "Retorna una lista completa de todos los venues registrados en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de venues obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VenueResponse.class), examples = @ExampleObject(value = "[{\"id\":1,\"name\":\"Teatro Nacional\",\"address\":\"Calle 71 #10-25\",\"city\":\"Bogotá\",\"country\":\"Colombia\",\"capacity\":1500}]")))
        })
        @GetMapping
        public ResponseEntity<List<VenueResponse>> getAllVenues() {
                List<VenueResponse> venues = venueService.getAllVenues();
                return ResponseEntity.ok(venues);
        }

        @Operation(summary = "Obtener venue por ID", description = "Retorna un venue específico buscado por su identificador único")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Venue encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VenueResponse.class), examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Teatro Nacional\",\"address\":\"Calle 71 #10-25\",\"city\":\"Bogotá\",\"country\":\"Colombia\",\"capacity\":1500}"))),
                        @ApiResponse(responseCode = "404", description = "Venue no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"timestamp\":\"2025-10-28T10:30:00\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Venue con ID 999 no encontrado\",\"path\":\"/api/venues/999\"}")))
        })
        @GetMapping("/{id}")
        public ResponseEntity<VenueResponse> getVenueById(
                        @Parameter(description = "ID del venue a buscar", required = true, example = "1") @PathVariable Long id) {
                return venueService.getVenueById(id)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new ResourceNotFoundException("Venue", id));
        }

        @Operation(summary = "Crear nuevo venue", description = "Crea un nuevo venue en el sistema con la información proporcionada")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Venue creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VenueResponse.class), examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Teatro Nacional\",\"address\":\"Calle 71 #10-25\",\"city\":\"Bogotá\",\"country\":\"Colombia\",\"capacity\":1500}"))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"timestamp\":\"2025-10-28T10:30:00\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Error de validación en los datos enviados\",\"path\":\"/api/venues\",\"details\":[\"name: El nombre del venue es obligatorio\"]}")))
        })
        @PostMapping
        public ResponseEntity<VenueResponse> createVenue(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del venue a crear", required = true, content = @Content(schema = @Schema(implementation = VenueRequest.class), examples = @ExampleObject(value = "{\"name\":\"Movistar Arena\",\"address\":\"Calle 61 #50-20\",\"city\":\"Bogotá\",\"country\":\"Colombia\",\"capacity\":14000}"))) @Valid @RequestBody VenueRequest request) {
                VenueResponse created = venueService.createVenue(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }

        @Operation(summary = "Actualizar venue existente", description = "Actualiza completamente la información de un venue existente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Venue actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VenueResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Venue no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PutMapping("/{id}")
        public ResponseEntity<VenueResponse> updateVenue(
                        @Parameter(description = "ID del venue a actualizar", required = true, example = "1") @PathVariable Long id,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos del venue", required = true, content = @Content(schema = @Schema(implementation = VenueRequest.class), examples = @ExampleObject(value = "{\"name\":\"Movistar Arena Renovado\",\"address\":\"Calle 61 #50-20\",\"city\":\"Bogotá\",\"country\":\"Colombia\",\"capacity\":15000}"))) @Valid @RequestBody VenueRequest request) {
                return venueService.updateVenue(id, request)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new ResourceNotFoundException("Venue", id));
        }

        @Operation(summary = "Eliminar venue", description = "Elimina permanentemente un venue del sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Venue eliminado exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Venue no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteVenue(
                        @Parameter(description = "ID del venue a eliminar", required = true, example = "1") @PathVariable Long id) {
                boolean deleted = venueService.deleteVenue(id);
                if (!deleted) {
                        throw new ResourceNotFoundException("Venue", id);
                }
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Obtener venues por ciudad", description = "Retorna todos los venues ubicados en una ciudad específica")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de venues obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VenueResponse.class)))
        })
        @GetMapping("/city/{city}")
        public ResponseEntity<List<VenueResponse>> getVenuesByCity(
                        @Parameter(description = "Nombre de la ciudad", required = true, example = "Bogotá") @PathVariable String city) {
                List<VenueResponse> venues = venueService.getVenuesByCity(city);
                return ResponseEntity.ok(venues);
        }
}
