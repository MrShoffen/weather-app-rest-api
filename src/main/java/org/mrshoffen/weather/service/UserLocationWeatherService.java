package org.mrshoffen.weather.service;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.model.dto.out.LocationResponseDto;
import org.mrshoffen.weather.model.dto.out.LocationWeatherDto;
import org.mrshoffen.weather.model.dto.out.WeatherForecastDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLocationWeatherService {

    private final OpenWeatherApiService openWeatherApiService;

    private final UserLocationService userLocationService;

    public List<LocationWeatherDto> getCurrentWeatherForAllSavedLocations(Integer userId) {
        return userLocationService
                .getAllSavedLocations(userId)
                .stream()
                .map(location ->
                        new LocationWeatherDto(
                                location,
                                openWeatherApiService
                                        .getCurrentWeatherByCoordinates(location.getLatitude(), location.getLongitude())
                        ))
                .toList();
    }

    public WeatherForecastDto getWeatherForecastForLocation(Integer userId, Integer locationId) {

        LocationResponseDto location = userLocationService.getLocationByLocationId(userId, locationId);


        return openWeatherApiService.getWeatherForecastByCoordinates(location.getLatitude(), location.getLongitude());
    }


}
