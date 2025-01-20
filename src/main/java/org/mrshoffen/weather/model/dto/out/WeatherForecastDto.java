package org.mrshoffen.weather.model.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherForecastDto {
    @JsonProperty("list")
    private List<WeatherDto> weatherList;

    @JsonProperty("timezone")
    private ZoneOffset timezone;

    @JsonProperty("city")
    private void unpackNested(Map<String, Object> city) {
        this.timezone = ZoneOffset.ofTotalSeconds((int) city.get("timezone"));
    }
}
