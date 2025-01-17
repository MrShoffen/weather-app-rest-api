package org.mrshoffen.weather.http.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.model.dto.out.LocationResponseDto;
import org.mrshoffen.weather.service.OpenWeatherApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather/api/locations")
@Validated
public class LocationController {

    private final OpenWeatherApiService weatherService;

    @GetMapping
    public ResponseEntity<List<LocationResponseDto>> getLocations(@RequestParam(value = "name")
                                                                  @Size(max = 128, message = "Name can't be larger than 128 symbols")
                                                                  @NotNull(message = "Name can't be null!")
                                                                  @NotBlank(message = "Name can't be empty!")
                                                                  String name) {

        List<LocationResponseDto> locationsByName = weatherService.findLocationsByName(name);

        return ResponseEntity.ok(locationsByName);
    }
}
