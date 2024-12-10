package org.mrshoffen.weather.advice;


import org.mrshoffen.weather.exception.*;
import org.mrshoffen.weather.exception.authentication.IncorrectPasswordException;
import org.mrshoffen.weather.exception.authentication.UserAlreadyExistsException;
import org.mrshoffen.weather.exception.authorization.SessionExpiredException;
import org.mrshoffen.weather.exception.authorization.UserAlreadyAuthorizedException;
import org.mrshoffen.weather.exception.authorization.UserUnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyExistException(UserAlreadyExistsException e) {
            return getProblemDetailResponseEntity(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFoundException(UserNotFoundException e) {
        return getProblemDetailResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ProblemDetail> handleIncorrectPasswordException(IncorrectPasswordException e) {
        return getProblemDetailResponseEntity(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorizedUserException(UserUnauthorizedException e) {
        return getProblemDetailResponseEntity(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ProblemDetail> handleSessionExpiredException(SessionExpiredException e) {
        return getProblemDetailResponseEntity(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(UserAlreadyAuthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyAuthorizedException(UserAlreadyAuthorizedException e) {
        return getProblemDetailResponseEntity(HttpStatus.FORBIDDEN, e.getMessage());

    }

    private static ResponseEntity<ProblemDetail> getProblemDetailResponseEntity(HttpStatus status, String errorMessage) {
        var problemDetail = ProblemDetail.forStatusAndDetail(status, errorMessage);

        return ResponseEntity
                .status(status)
                .body(problemDetail);
    }



}
