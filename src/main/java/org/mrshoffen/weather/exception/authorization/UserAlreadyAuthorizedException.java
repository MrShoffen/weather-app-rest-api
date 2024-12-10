package org.mrshoffen.weather.exception.authorization;

public class UserAlreadyAuthorizedException extends RuntimeException{

    public UserAlreadyAuthorizedException(String message) {
        super(message);
    }
}
