package org.mrshoffen.weather.service;

import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.exception.weather.OpenWeatherApiException;
import org.mrshoffen.weather.model.dto.out.LocationDto;
import org.mrshoffen.weather.model.dto.out.WeatherDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class OpenWeatherApiService {

    @Value("${app.weather.open-weather-api.base-url}")
    private String baseUrl;

    @Value("${app.weather.open-weather-api.geocoding.url}")
    private String geocodingUrl;

    @Value("${app.weather.open-weather-api.geocoding.default-limit}")
    private int geocodingDefaultLimit;

    @Value("${app.weather.open-weather-api.current-weather-url}")
    private String currentWeatherUrl;

    @Value("${app.weather.open-weather-api.key}")
    private String apiKey;

    private final RestClient restClient;

    public List<LocationDto> findLocationsByName(String locationName) {

        String encodedLocation = URLEncoder.encode(locationName, StandardCharsets.UTF_8);

        URI uri = URI.create(
                baseUrl
                        + geocodingUrl
                        .formatted(encodedLocation, geocodingDefaultLimit,  apiKey)
        );

        List<LocationDto> locations = restClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                        (request, response) -> {
                            throw new OpenWeatherApiException("Open weather API error: %s, status: %s"
                                    .formatted(response.getStatusText(), response.getStatusCode()));
                        })
                .body(new ParameterizedTypeReference<>() {
                });

        return locations;
    }

    public WeatherDto getWeatherByCoordinates(double lat, double lon) {

        URI uri = URI.create(
                baseUrl
                        + currentWeatherUrl
                        .formatted(lat, lon,  apiKey)
        );

        WeatherDto weather = restClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.isError(),
                        (request, response) -> {
                            throw new OpenWeatherApiException("Open weather API error: %s, status: %s"
                                    .formatted(response.getStatusText(), response.getStatusCode()));
                        })
                .body(new ParameterizedTypeReference<>() {
                });

        return weather;
    }


}
