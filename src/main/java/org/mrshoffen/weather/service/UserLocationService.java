package org.mrshoffen.weather.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.exception.location.LocationAlreadySavedException;
import org.mrshoffen.weather.exception.location.LocationNotFoundException;
import org.mrshoffen.weather.mapper.LocationMapper;
import org.mrshoffen.weather.model.dto.in.LocationSaveDto;
import org.mrshoffen.weather.model.dto.out.LocationResponseDto;
import org.mrshoffen.weather.model.entity.Location;
import org.mrshoffen.weather.repository.LocationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLocationService {

    private final LocationMapper locationMapper;

    private final LocationRepository locationRepository;

    @Transactional
    public LocationResponseDto saveLocationForUser(Integer userId, LocationSaveDto locationSaveDto) {


        Location entity = locationMapper.toEntity(locationSaveDto, userId);

        try {
            locationRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new LocationAlreadySavedException("This location is already saved", e);
        }

        return locationMapper.toDto(entity);
    }

    public List<LocationResponseDto> getAllSavedLocations(Integer userId) {

        return locationRepository.findAllByUserIdOrderById(userId)
                .stream()
                .map(locationMapper::toDto)
                .toList();

    }

    public LocationResponseDto getLocationByLocationId(Integer userId, Integer locationId) {

        return locationRepository.findByIdAndUserId(locationId, userId)
                .map(locationMapper::toDto)
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));
    }

    @Transactional
    public void deleteSavedLocation(Integer userId, Integer locationId) {

        Location locationForDelete = locationRepository
                .findByIdAndUserId(locationId, userId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found"));

        locationRepository.delete(locationForDelete);

    }
}
