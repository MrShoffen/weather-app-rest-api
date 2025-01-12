package org.mrshoffen.weather.http.controller;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.model.dto.out.LocationDto;
import org.mrshoffen.weather.model.dto.out.WeatherDto;
import org.mrshoffen.weather.service.OpenWeatherApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather/api/weather")
public class WeatherController {

    private final OpenWeatherApiService weatherService;

    @GetMapping
    public ResponseEntity<WeatherDto> getLocations(@RequestParam(value = "lat") Double lat,
                                                          @RequestParam(value = "lon") Double lon) {

        WeatherDto weatherDto = weatherService.getWeatherByCoordinates(lat, lon);

        return ResponseEntity.ok(weatherDto);
    }
}
