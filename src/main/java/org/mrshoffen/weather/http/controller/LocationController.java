package org.mrshoffen.weather.http.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.model.dto.out.LocationDto;
import org.mrshoffen.weather.service.OpenWeatherApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather/api/locations")
public class LocationController {

    private final OpenWeatherApiService weatherService;

    @GetMapping
    public ResponseEntity<List<LocationDto>> getLocations(@RequestParam(value = "name") String name) {

        List<LocationDto> locationsByName = weatherService.findLocationsByName(name);

        return ResponseEntity.ok(locationsByName);
    }
}
