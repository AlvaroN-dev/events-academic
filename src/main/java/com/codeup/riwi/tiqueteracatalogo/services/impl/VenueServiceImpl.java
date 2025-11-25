package com.codeup.riwi.tiqueteracatalogo.services.impl;

import com.codeup.riwi.tiqueteracatalogo.web.dto.VenueRequest;
import com.codeup.riwi.tiqueteracatalogo.web.dto.VenueResponse;
import com.codeup.riwi.tiqueteracatalogo.domain.entity.VenueEntity;
import com.codeup.riwi.tiqueteracatalogo.domain.mapper.VenueMapper;
import com.codeup.riwi.tiqueteracatalogo.repository.VenueRepository;
import com.codeup.riwi.tiqueteracatalogo.services.IVenueService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de lógica de negocio para Venues.
 * Usa repositorio para persistencia y mapper para transformaciones.
 */
@Service
public class VenueServiceImpl implements IVenueService {

    private final VenueRepository venueRepository;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param venueRepository Repositorio de venues
     */
    public VenueServiceImpl(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Override
    public List<VenueResponse> getAllVenues() {
        return venueRepository.findAll().stream()
                .map(VenueMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<VenueResponse> getVenueById(Long id) {
        return venueRepository.findById(id)
                .map(VenueMapper::toResponse);
    }

    @Override
    public VenueResponse createVenue(VenueRequest request) {
        VenueEntity entity = VenueMapper.toEntity(request);
        VenueEntity savedEntity = venueRepository.save(entity);
        return VenueMapper.toResponse(savedEntity);
    }

    @Override
    public Optional<VenueResponse> updateVenue(Long id, VenueRequest request) {
        return venueRepository.findById(id)
                .map(entity -> {
                    VenueMapper.updateEntityFromRequest(entity, request);
                    VenueEntity updated = venueRepository.save(entity);
                    return VenueMapper.toResponse(updated);
                });
    }

    @Override
    public boolean deleteVenue(Long id) {
        if (venueRepository.existsById(id)) {
            venueRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<VenueResponse> getVenuesByCity(String city) {
        return venueRepository.findByCityIgnoreCase(city).stream()
                .map(VenueMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countVenues() {
        return venueRepository.count();
    }
}
