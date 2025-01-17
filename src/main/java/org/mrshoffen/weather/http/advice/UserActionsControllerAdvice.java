package org.mrshoffen.weather.http.advice;

import org.mrshoffen.weather.exception.authentication.IncorrectPasswordException;
import org.mrshoffen.weather.exception.authentication.UserAlreadyExistsException;
import org.mrshoffen.weather.exception.authentication.UserNotFoundException;
import org.mrshoffen.weather.exception.authorization.UserAlreadyAuthorizedException;
import org.mrshoffen.weather.exception.authorization.UserUnauthorizedException;
import org.mrshoffen.weather.exception.location.LocationAlreadySavedException;
import org.mrshoffen.weather.exception.location.LocationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.mrshoffen.weather.http.advice.ProblemDetailUtil.getProblemDetailResponseEntity;

@RestControllerAdvice
public class UserActionsControllerAdvice {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyExistException(UserAlreadyExistsException e) {
        return getProblemDetailResponseEntity(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFoundException(UserNotFoundException e) {
        return getProblemDetailResponseEntity(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ProblemDetail> handleIncorrectPasswordException(IncorrectPasswordException e) {
        return getProblemDetailResponseEntity(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorizedUserException(UserUnauthorizedException e) {
        return getProblemDetailResponseEntity(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(UserAlreadyAuthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyAuthorizedException(UserAlreadyAuthorizedException e) {
        return getProblemDetailResponseEntity(HttpStatus.FORBIDDEN, e);

    }

    @ExceptionHandler(LocationAlreadySavedException.class)
    public ResponseEntity<ProblemDetail> handleLocationAlreadySavedException(LocationAlreadySavedException e) {
        return getProblemDetailResponseEntity(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleLocationNotFoundException(LocationNotFoundException e) {
        return getProblemDetailResponseEntity(HttpStatus.NOT_FOUND, e);
    }

}
