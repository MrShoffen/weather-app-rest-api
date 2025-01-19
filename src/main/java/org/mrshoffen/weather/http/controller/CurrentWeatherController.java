package org.mrshoffen.weather.http.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.model.dto.out.WeatherDto;
import org.mrshoffen.weather.model.dto.out.WeatherForecastDto;
import org.mrshoffen.weather.service.OpenWeatherApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather/api/weather")
@Validated
public class CurrentWeatherController {

    private final OpenWeatherApiService weatherService;

    @GetMapping
    public ResponseEntity<WeatherDto> getLocations(@RequestParam("lat")
                                                   @NotNull(message = "Latitude can't be null!")
                                                   @Min(value = -90, message = "Latitude must be a number between -90 and 90.")
                                                   @Max(value = 90, message = "Latitude must be a number between -90 and 90.")
                                                   Double lat,

                                                   @RequestParam("lon")
                                                   @NotNull(message = "Longitude can't be null!")
                                                   @Min(value = -180, message = "Longitude must be a number between -180 and 180.")
                                                   @Max(value = 180, message = "Longitude must be a number between -180 and 180.")
                                                   Double lon) {

        WeatherDto weatherDto = weatherService.getCurrentWeatherByCoordinates(lat, lon);
        return ResponseEntity.ok(weatherDto);
    }

}
