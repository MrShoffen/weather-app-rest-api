package org.mrshoffen.weather.exception.authorization;

public class UserUnauthorizedException extends RuntimeException{

    public UserUnauthorizedException(String message) {
        super(message);
    }
}
