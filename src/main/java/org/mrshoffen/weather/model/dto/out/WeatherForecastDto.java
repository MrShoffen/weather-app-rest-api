package org.mrshoffen.weather.model.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.time.ZoneOffset;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherForecastDto {
    @JsonProperty("list")
    private List<WeatherDto> weatherList;
}
