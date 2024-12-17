package org.mrshoffen.weather.advice;


import org.mrshoffen.weather.exception.image.ImageNotFoundException;
import org.mrshoffen.weather.exception.authentication.IncorrectPasswordException;
import org.mrshoffen.weather.exception.UserAlreadyExistsException;
import org.mrshoffen.weather.exception.authentication.UserNotFoundException;
import org.mrshoffen.weather.exception.authorization.SessionExpiredException;
import org.mrshoffen.weather.exception.authorization.UserAlreadyAuthorizedException;
import org.mrshoffen.weather.exception.authorization.UserUnauthorizedException;
import org.mrshoffen.weather.exception.image.IncorrectImageFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalControllerAdvice {

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

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ProblemDetail> handleSessionExpiredException(SessionExpiredException e) {
        return getProblemDetailResponseEntity(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(UserAlreadyAuthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUserAlreadyAuthorizedException(UserAlreadyAuthorizedException e) {
        return getProblemDetailResponseEntity(HttpStatus.FORBIDDEN, e);

    }

    //images
    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleImageNotFoundException(ImageNotFoundException e) {
        return getProblemDetailResponseEntity(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(IncorrectImageFormatException.class)
    public ResponseEntity<ProblemDetail> handleIncorrectImageFormatException(IncorrectImageFormatException e) {
        return getProblemDetailResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ProblemDetail> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return getProblemDetailResponseEntity(HttpStatus.PAYLOAD_TOO_LARGE, e);
    }

    private static ResponseEntity<ProblemDetail> getProblemDetailResponseEntity(HttpStatus status, Exception e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        problemDetail.setTitle(e.getClass().getSimpleName());
        return ResponseEntity
                .status(status)
                .body(problemDetail);
    }


}
