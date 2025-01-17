package org.mrshoffen.weather.http.advice;

import org.mrshoffen.weather.exception.image.ImageNotFoundException;
import org.mrshoffen.weather.exception.image.IncorrectImageFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.mrshoffen.weather.http.advice.ProblemDetailUtil.getProblemDetailResponseEntity;

@RestControllerAdvice
public class ImageControllerAdvice {

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

}
