package org.mrshoffen.weather.exception.location;

public class LocationAlreadySavedException extends RuntimeException{

    public LocationAlreadySavedException(String message) {
        super(message);
    }

    public LocationAlreadySavedException(String message, Throwable cause) {
        super(message, cause);
    }
}
