package com.codeup.riwi.tiqueteracatalogo.domain.mapper;

import com.codeup.riwi.tiqueteracatalogo.web.dto.VenueRequest;
import com.codeup.riwi.tiqueteracatalogo.web.dto.VenueResponse;
import com.codeup.riwi.tiqueteracatalogo.domain.entity.VenueEntity;

/**
 * Mapper para convertir entre VenueEntity y DTOs (Request/Response).
 * Centraliza las transformaciones entre las capas de dominio y presentación.
 */
public class VenueMapper {

    /**
     * Convierte VenueRequest a VenueEntity.
     * Usado al crear o actualizar venues.
     */
    public static VenueEntity toEntity(VenueRequest request) {
        VenueEntity entity = new VenueEntity();
        entity.setName(request.getName());
        entity.setAddress(request.getAddress());
        entity.setCity(request.getCity());
        entity.setCountry(request.getCountry());
        entity.setCapacity(request.getCapacity());
        return entity;
    }

    /**
     * Convierte VenueEntity a VenueResponse.
     * Usado al devolver datos al cliente.
     */
    public static VenueResponse toResponse(VenueEntity entity) {
        return new VenueResponse(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getCity(),
                entity.getCountry(),
                entity.getCapacity()
        );
    }

    /**
     * Actualiza un VenueEntity existente con datos de VenueRequest.
     * Preserva el ID original.
     */
    public static void updateEntityFromRequest(VenueEntity entity, VenueRequest request) {
        entity.setName(request.getName());
        entity.setAddress(request.getAddress());
        entity.setCity(request.getCity());
        entity.setCountry(request.getCountry());
        entity.setCapacity(request.getCapacity());
    }
}
