package org.mrshoffen.weather.exception.weather;

public class OpenWeatherApiException extends RuntimeException {

    public OpenWeatherApiException(String message) {
        super(message);
    }
}
