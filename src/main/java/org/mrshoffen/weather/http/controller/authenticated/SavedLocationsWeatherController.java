package org.mrshoffen.weather.http.controller.authenticated;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.http.controller.authenticated.resolver.AuthorizedUser;
import org.mrshoffen.weather.model.dto.out.LocationWeatherDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.model.dto.out.WeatherForecastDto;
import org.mrshoffen.weather.service.UserLocationWeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather/api/user/locations")
public class SavedLocationsWeatherController {

    private final UserLocationWeatherService userLocationWeatherService;

    @GetMapping("/weather")
    ResponseEntity<List<LocationWeatherDto>> getCurrentWeatherForAllLocations(@AuthorizedUser UserResponseDto authorizedUser) {

        List<LocationWeatherDto> locationsWithWeather =
                userLocationWeatherService.getCurrentWeatherForAllSavedLocations(authorizedUser.getId());
        return ResponseEntity.ok(locationsWithWeather);
    }

    @GetMapping("{locationId:\\d+}/forecast")
    ResponseEntity<WeatherForecastDto> getWeatherForecastForLocation(@AuthorizedUser UserResponseDto authorizedUser, @PathVariable("locationId") Integer locationId) {

        WeatherForecastDto forecast = userLocationWeatherService.getWeatherForecastForLocation(authorizedUser.getId(), locationId);

        return ResponseEntity.ok(forecast);
    }

}
