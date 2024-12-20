package org.mrshoffen.weather.exception.authorization;

public class SessionNotFoundException extends RuntimeException{
    public SessionNotFoundException(String message) {
        super(message);
    }
}
