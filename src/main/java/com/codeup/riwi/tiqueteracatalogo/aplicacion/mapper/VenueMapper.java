package com.codeup.riwi.tiqueteracatalogo.aplicacion.mapper;

import com.codeup.riwi.tiqueteracatalogo.dominio.models.Venue;
import com.codeup.riwi.tiqueteracatalogo.aplicacion.dto.VenueRequest;
import com.codeup.riwi.tiqueteracatalogo.aplicacion.dto.VenueResponse;

/**
 * Mapper to convert between Venue domain model and DTOs.
 */
public class VenueMapper {

    public static Venue toEntity(VenueRequest request) {
        if (request == null)
            return null;

        Venue venue = new Venue();
        venue.setName(request.getName());
        venue.setAddress(request.getAddress());
        venue.setCity(request.getCity());
        venue.setCountry(request.getCountry());
        venue.setCapacity(request.getCapacity());

        return venue;
    }

    public static VenueResponse toResponse(Venue venue) {
        if (venue == null)
            return null;

        VenueResponse response = new VenueResponse();
        response.setId(venue.getId());
        response.setName(venue.getName());
        response.setAddress(venue.getAddress());
        response.setCity(venue.getCity());
        response.setCountry(venue.getCountry());
        response.setCapacity(venue.getCapacity());

        return response;
    }
}
