package org.mrshoffen.weather.exception.authorization;

public class SessionExpiredException extends RuntimeException{
    public SessionExpiredException(String message) {
        super(message);
    }
}
