package org.mrshoffen.weather.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.exception.location.LocationAlreadySavedException;
import org.mrshoffen.weather.mapper.LocationMapper;
import org.mrshoffen.weather.model.dto.in.LocationSaveDto;
import org.mrshoffen.weather.model.dto.out.LocationDto;
import org.mrshoffen.weather.model.entity.Location;
import org.mrshoffen.weather.repository.LocationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLocationService {

    private final LocationMapper locationMapper;

    private final LocationRepository locationRepository;

    public LocationDto saveLocationForUser(Integer userId, LocationSaveDto locationSaveDto) {


        Location entity = locationMapper.toEntity(locationSaveDto, userId);

        try {
            locationRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new LocationAlreadySavedException("This location is already saved", e);
        }

        return locationMapper.toDto(entity);
    }

    public List<LocationDto> getAllSavedLocations(Integer userId) {

        return locationRepository.findAllByUserId(userId)
                .stream()
                .map(locationMapper::toDto)
                .toList();

    }
}
