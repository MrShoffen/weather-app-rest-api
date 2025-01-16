package org.mrshoffen.weather.model.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationWeatherDto {

    private LocationResponseDto location;

    private WeatherDto weather;
}
