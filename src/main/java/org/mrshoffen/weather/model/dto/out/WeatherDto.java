package org.mrshoffen.weather.model.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDto {

    @JsonProperty("weather")
    private List<Weather> weather;

    @JsonProperty("main")
    private Temperature temperature;

    @JsonProperty("wind")
    private Wind wind;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String main;
        private String description;
        private String icon;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Temperature {
        @JsonProperty("temp")
        private float currentTemp;
        @JsonProperty("feels_like")
        private float feelsLikeTemp;
        @JsonProperty("temp_min")
        private float minTemp;
        @JsonProperty("temp_max")
        private float maxTemp;
        @JsonProperty("humidity")
        private int humidityPercentage;
        private int pressure;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private float speed;
    }
}
