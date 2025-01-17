package org.mrshoffen.weather.http.controller.authenticated;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.http.resolver.AuthorizedUser;
import org.mrshoffen.weather.model.dto.out.LocationWeatherDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.service.UserLocationWeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather/api/user/locations/weather")
public class SavedLocationsWeatherController {

    private final UserLocationWeatherService userLocationWeatherService;

    @GetMapping
    ResponseEntity<List<LocationWeatherDto>> getWeatherForAllSavedLocations(@AuthorizedUser UserResponseDto authorizedUser) {

        List<LocationWeatherDto> locationsWithWeather =
                userLocationWeatherService.getWeatherForAllSavedLocations(authorizedUser.getId());
        return ResponseEntity.ok(locationsWithWeather);

    }

}
