package org.mrshoffen.weather.service;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.model.dto.out.LocationWeatherDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLocationWeatherService {

    private final OpenWeatherApiService openWeatherApiService;

    private final UserLocationService userLocationService;

    public List<LocationWeatherDto> getWeatherForAllSavedLocations(Integer userId) {

        return userLocationService
                .getAllSavedLocations(userId)
                .stream()
                .map(location ->
                        new LocationWeatherDto(
                                location,
                                openWeatherApiService.getWeatherByCoordinates(location.getLatitude(), location.getLongitude())
                        ))
                .toList();

    }
}
