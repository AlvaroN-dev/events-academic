package com.codeup.riwi.tiqueteracatalogo.services;

import com.codeup.riwi.tiqueteracatalogo.web.dto.VenueRequest;
import com.codeup.riwi.tiqueteracatalogo.web.dto.VenueResponse;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de negocio para Venues.
 * Separa el contrato de la implementación.
 */
public interface IVenueService {
    
    /**
     * Obtiene todos los venues
     * @return Lista de respuestas de venues
     */
    List<VenueResponse> getAllVenues();
    
    /**
     * Busca un venue por ID
     * @param id ID del venue
     * @return Optional con respuesta del venue
     */
    Optional<VenueResponse> getVenueById(Long id);
    
    /**
     * Crea un nuevo venue
     * @param request Datos del venue
     * @return Respuesta con venue creado
     */
    VenueResponse createVenue(VenueRequest request);
    
    /**
     * Actualiza un venue existente
     * @param id ID del venue
     * @param request Nuevos datos
     * @return Optional con venue actualizado
     */
    Optional<VenueResponse> updateVenue(Long id, VenueRequest request);
    
    /**
     * Elimina un venue
     * @param id ID del venue
     * @return true si se eliminó
     */
    boolean deleteVenue(Long id);
    
    /**
     * Busca venues por ciudad
     * @param city Nombre de la ciudad
     * @return Lista de venues en la ciudad
     */
    List<VenueResponse> getVenuesByCity(String city);
    
    /**
     * Cuenta total de venues
     * @return Número de venues
     */
    long countVenues();
}
